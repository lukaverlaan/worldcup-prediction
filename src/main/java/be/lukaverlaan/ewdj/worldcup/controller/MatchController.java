package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.domain.Prediction;
import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.form.PredictionForm;
import be.lukaverlaan.ewdj.worldcup.service.MatchService;
import be.lukaverlaan.ewdj.worldcup.service.PredictionService;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import be.lukaverlaan.ewdj.worldcup.service.WebClientService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;
    private final PredictionService predictionService;
    private final UserService userService;
    private final WebClientService webClientService;

    public MatchController(MatchService matchService, PredictionService predictionService,
                           UserService userService, WebClientService webClientService) {
        this.matchService = matchService;
        this.predictionService = predictionService;
        this.userService = userService;
        this.webClientService = webClientService;
    }

    @GetMapping("/{id}")
    public String matchDetail(@PathVariable Long id, Model model, Authentication auth) {
        Match match = matchService.findById(id);
        model.addAttribute("match", match);

        boolean canPredict = match.getDateTime().minusHours(1).isAfter(LocalDateTime.now());
        model.addAttribute("canPredict", canPredict);

        if (auth != null && auth.isAuthenticated()) {
            User user = userService.findByUsername(auth.getName());
            Optional<Prediction> existing = predictionService.findByUserAndMatch(user, match);
            PredictionForm form = new PredictionForm();
            existing.ifPresent(p -> {
                form.setPredictedScoreA(p.getPredictedScoreA());
                form.setPredictedScoreB(p.getPredictedScoreB());
            });
            model.addAttribute("predictionForm", form);
            model.addAttribute("existingPrediction", existing.orElse(null));
        }

        if (match.getStadiumCode() != null && !match.getStadiumCode().isBlank()) {
            Map capacity = webClientService.fetchStadiumCapacity(match.getStadiumCode());
            model.addAttribute("stadiumCapacity", capacity.get("capacity"));
        }

        return "match/detail";
    }

    @PostMapping("/{id}/predict")
    public String submitPrediction(@PathVariable Long id,
                                   @Valid @ModelAttribute("predictionForm") PredictionForm form,
                                   BindingResult result, Authentication auth,
                                   RedirectAttributes ra, Model model) {
        Match match = matchService.findById(id);
        if (result.hasErrors()) {
            model.addAttribute("match", match);
            model.addAttribute("canPredict", match.getDateTime().minusHours(1).isAfter(LocalDateTime.now()));
            return "match/detail";
        }
        User user = userService.findByUsername(auth.getName());
        try {
            predictionService.savePrediction(user, match, form);
            ra.addFlashAttribute("successMessage", "prediction.saved.success");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/matches/" + id;
    }
}

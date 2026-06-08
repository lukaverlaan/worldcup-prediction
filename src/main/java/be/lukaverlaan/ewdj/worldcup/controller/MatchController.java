package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.domain.Prediction;
import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.dto.PredictionStats;
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
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;
import java.util.List;
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

    @GetMapping
    public String matchList(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "upcoming") String tab,
                            Model model) {
        Page<Match> matchPage = "past".equals(tab)
                ? matchService.findPast(page, 15)
                : matchService.findUpcoming(page, 15);
        model.addAttribute("matches", matchPage.getContent());
        model.addAttribute("currentPage", matchPage.getNumber());
        model.addAttribute("totalPages", matchPage.getTotalPages());
        model.addAttribute("tab", tab);
        return "match/list";
    }

    @GetMapping("/{id}")
    public String matchDetail(@PathVariable Long id,
                              @RequestParam(required = false) String from,
                              @RequestParam(required = false) Long teamId,
                              Model model, Authentication auth) {
        Match match = matchService.findById(id);
        model.addAttribute("match", match);

        boolean canPredict = match.getDateTime().minusHours(1).isAfter(LocalDateTime.now());
        model.addAttribute("canPredict", canPredict);

        // Alle prognoses voor dit match
        List<Prediction> allPredictions = predictionService.findByMatch(match);
        PredictionStats globalStats = predictionService.computeStats(allPredictions);
        model.addAttribute("globalStats", globalStats);

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

            // Team stats: alles binnen transactie in de service
            PredictionStats teamStats = predictionService.computeTeamStats(match, user);
            if (teamStats != null) {
                model.addAttribute("teamStats", teamStats);
                model.addAttribute("teamName", predictionService.getFirstTeamName(user));
            }
        }

        // Bepaal backUrl en prev/next navigatie op basis van from-param
        String backUrl = switch (from != null ? from : "") {
            case "home"    -> "/";
            case "matches" -> "/matches";
            case "groups"  -> "/groups";
            case "team"    -> teamId != null ? "/teams/" + teamId : "/matches";
            default        -> "/matches";
        };
        model.addAttribute("backUrl", backUrl);
        model.addAttribute("fromParam", from);

        if (from != null && !from.isBlank()) {
            matchService.findPrevious(match.getDateTime())
                .ifPresent(m -> model.addAttribute("prevMatchId", m.getId()));
            matchService.findNext(match.getDateTime())
                .ifPresent(m -> model.addAttribute("nextMatchId", m.getId()));
            if (teamId != null) model.addAttribute("teamId", teamId);
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
                                   @RequestParam(required = false) String from,
                                   @RequestParam(required = false) Long teamId,
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
        String redirect = "redirect:/matches/" + id;
        if (from != null && !from.isBlank()) {
            redirect += "?from=" + from;
            if (teamId != null) redirect += "&teamId=" + teamId;
        }
        return redirect;
    }
}

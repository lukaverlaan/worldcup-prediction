package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.Prediction;
import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.repository.PredictionRepository;
import be.lukaverlaan.ewdj.worldcup.service.PredictionService;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Controller
public class PredictionOverviewController {

    private final UserService userService;
    private final PredictionRepository predictionRepository;
    private final PredictionService predictionService;

    public PredictionOverviewController(UserService userService, PredictionRepository predictionRepository,
                                        PredictionService predictionService) {
        this.userService = userService;
        this.predictionRepository = predictionRepository;
        this.predictionService = predictionService;
    }

    @GetMapping("/my-predictions")
    @Transactional(readOnly = true)
    public String myPredictions(@RequestParam(defaultValue = "upcoming") String tab,
                                Model model, Authentication auth) {
        User user = userService.findByUsername(auth.getName());
        List<Prediction> all = predictionRepository.findByUser(user);
        all.sort(Comparator.comparing(p -> p.getMatch().getDateTime()));

        List<Prediction> past = all.stream().filter(p -> p.getMatch().hasResult()).toList();
        List<Prediction> upcoming = all.stream().filter(p -> !p.getMatch().hasResult()).toList();

        int totalPoints = predictionRepository.sumPointsByUser(user);

        model.addAttribute("tab", tab);
        model.addAttribute("pastPredictions", past);
        model.addAttribute("upcomingPredictions", upcoming);
        model.addAttribute("totalPoints", totalPoints);
        model.addAttribute("streak", predictionService.getStreakForUser(user));
        model.addAttribute("user", user);
        return "my-predictions";
    }
}

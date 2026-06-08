package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.repository.PredictionRepository;
import be.lukaverlaan.ewdj.worldcup.service.PredictionService;
import be.lukaverlaan.ewdj.worldcup.service.TeamService;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ScoreboardController {

    private final TeamService teamService;
    private final UserService userService;
    private final PredictionRepository predictionRepository;
    private final PredictionService predictionService;

    public ScoreboardController(TeamService teamService, UserService userService,
                                PredictionRepository predictionRepository,
                                PredictionService predictionService) {
        this.teamService = teamService;
        this.userService = userService;
        this.predictionRepository = predictionRepository;
        this.predictionService = predictionService;
    }

    @GetMapping({"/leaderboard", "/scoreboard"})
    public String publicScoreboard(@RequestParam(defaultValue = "teams") String tab,
                                   Model model, Authentication auth) {
        model.addAttribute("tab", tab);
        model.addAttribute("top10", teamService.getTop10Teams());

        // Top gebruikers
        List<Object[]> rows = predictionRepository.findTopUsersByPoints();
        Map<User, Integer> topUsers = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(rows.size(), 10); i++) {
            topUsers.put((User) rows.get(i)[0], ((Number) rows.get(i)[1]).intValue());
        }
        model.addAttribute("topUsers", topUsers);

        Map<Long, Integer> userStreaks = new LinkedHashMap<>();
        for (User u : topUsers.keySet()) {
            userStreaks.put(u.getId(), predictionService.getStreakForUser(u));
        }
        model.addAttribute("userStreaks", userStreaks);

        if (auth != null && auth.isAuthenticated()) {
            User user = userService.findByUsername(auth.getName());
            model.addAttribute("currentUser", user);
            model.addAttribute("userTeamIds", teamService.getTeamIdsForUser(user));
        } else {
            model.addAttribute("userTeamIds", Collections.emptySet());
        }
        return "scoreboard/public";
    }
}

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
import be.lukaverlaan.ewdj.worldcup.domain.Team;
import java.util.ArrayList;
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

        // Alle teams gesorteerd ophalen (zelfde query als getTop10Teams maar volledig)
        List<Map<String, Object>> allTeams = teamService.getAllTeamsWithScores();
        List<Map<String, Object>> top10 = allTeams.size() > 10 ? new ArrayList<>(allTeams.subList(0, 10)) : allTeams;
        model.addAttribute("top10", top10);

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
            var userTeamIds = teamService.getTeamIdsForUser(user);
            model.addAttribute("userTeamIds", userTeamIds);

            // Teams van de gebruiker die buiten de top 10 vallen
            List<Map<String, Object>> myTeamsOutside = new ArrayList<>();
            for (int i = 10; i < allTeams.size(); i++) {
                Team t = (Team) allTeams.get(i).get("team");
                if (userTeamIds.contains(t.getId())) {
                    Map<String, Object> ranked = new LinkedHashMap<>(allTeams.get(i));
                    ranked.put("rank", i + 1);
                    myTeamsOutside.add(ranked);
                }
            }
            model.addAttribute("myTeamsOutside", myTeamsOutside);

            // Huidige gebruiker buiten top 10
            boolean inTop10 = topUsers.keySet().stream().anyMatch(u -> u.getId().equals(user.getId()));
            if (!inTop10) {
                for (int i = 10; i < rows.size(); i++) {
                    User u = (User) rows.get(i)[0];
                    if (u.getId().equals(user.getId())) {
                        model.addAttribute("currentUserRankOutside", i + 1);
                        model.addAttribute("currentUserScoreOutside", ((Number) rows.get(i)[1]).intValue());
                        model.addAttribute("currentUserStreakOutside", predictionService.getStreakForUser(u));
                        break;
                    }
                }
            }
        } else {
            model.addAttribute("userTeamIds", Collections.emptySet());
            model.addAttribute("myTeamsOutside", Collections.emptyList());
        }
        return "scoreboard/public";
    }
}

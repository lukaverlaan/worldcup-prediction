package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.Team;
import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.repository.PredictionRepository;
import be.lukaverlaan.ewdj.worldcup.service.MatchService;
import be.lukaverlaan.ewdj.worldcup.service.PredictionService;
import be.lukaverlaan.ewdj.worldcup.service.TeamService;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import be.lukaverlaan.ewdj.worldcup.domain.Match;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Slf4j
@Controller
public class HomeController {

    private final MatchService matchService;
    private final UserService userService;
    private final TeamService teamService;
    private final PredictionRepository predictionRepository;
    private final PredictionService predictionService;

    public HomeController(MatchService matchService, UserService userService,
                          TeamService teamService, PredictionRepository predictionRepository,
                          PredictionService predictionService) {
        this.matchService = matchService;
        this.userService = userService;
        this.teamService = teamService;
        this.predictionRepository = predictionRepository;
        this.predictionService = predictionService;
    }

    @GetMapping("/")
    public String home(Model model, Authentication auth) {
        log.info("GET /");
        // Homepage toont enkel 5 komende wedstrijden
        List<Match> upcomingMatches = matchService.findUpcoming(0, 5).getContent();
        model.addAttribute("matches", upcomingMatches);

        // Volgende wedstrijd (voor iedereen zichtbaar)
        matchService.findUpcoming(0, 1).getContent().stream().findFirst()
            .ifPresent(m -> model.addAttribute("nextMatch", m));

        // Top 3 teams voor het scoreboard widget
        List<Map<String, Object>> top10 = teamService.getTop10Teams();
        model.addAttribute("top3Teams", top10.size() > 3 ? top10.subList(0, 3) : top10);

        if (auth != null && auth.isAuthenticated()) {
            User user = userService.findByUsername(auth.getName());
            int totalPoints = predictionRepository.sumPointsByUser(user);
            int predictionCount = predictionRepository.findByUser(user).size();
            int streak = predictionService.getStreakForUser(user);
            model.addAttribute("totalPoints", totalPoints);
            model.addAttribute("predictionCount", predictionCount);
            model.addAttribute("streak", streak);

            // Rank per team
            List<Map<String, Object>> teamRankings = new ArrayList<>();
            for (Team team : teamService.findTeamsForUser(user)) {
                java.util.Map<Long, Integer> pts = predictionRepository.getPointsMapForUsers(team.getMembers());
                List<User> sorted = team.getMembers().stream()
                    .sorted((a, b) -> pts.getOrDefault(b.getId(), 0) - pts.getOrDefault(a.getId(), 0))
                    .toList();
                int rank = sorted.stream().map(User::getId).toList().indexOf(user.getId()) + 1;
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("teamName", team.getName());
                entry.put("teamId", team.getId());
                entry.put("rank", rank);
                entry.put("total", sorted.size());
                teamRankings.add(entry);
            }
            model.addAttribute("teamRankings", teamRankings);
        }

        return "index";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        log.info("GET /access-denied");
        return "error/access-denied";
    }
}

package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.Team;
import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.repository.PredictionRepository;
import be.lukaverlaan.ewdj.worldcup.service.MatchService;
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

    public HomeController(MatchService matchService, UserService userService,
                          TeamService teamService, PredictionRepository predictionRepository) {
        this.matchService = matchService;
        this.userService = userService;
        this.teamService = teamService;
        this.predictionRepository = predictionRepository;
    }

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "upcoming") String tab,
                       Model model, Authentication auth) {
        log.info("GET /, page={}, tab={}", page, tab);
        Page<Match> matchPage = "past".equals(tab)
                ? matchService.findPast(page, 10)
                : matchService.findUpcoming(page, 10);
        model.addAttribute("matches", matchPage.getContent());
        model.addAttribute("currentPage", matchPage.getNumber());
        model.addAttribute("totalPages", matchPage.getTotalPages());
        model.addAttribute("tab", tab);

        if (auth != null && auth.isAuthenticated()) {
            User user = userService.findByUsername(auth.getName());
            int totalPoints = predictionRepository.sumPointsByUser(user);
            int predictionCount = predictionRepository.findByUser(user).size();
            model.addAttribute("totalPoints", totalPoints);
            model.addAttribute("predictionCount", predictionCount);

            // Rank per team
            List<Map<String, Object>> teamRankings = new ArrayList<>();
            for (Team team : teamService.findTeamsForUser(user)) {
                List<User> sorted = team.getMembers().stream()
                    .sorted((a, b) -> predictionRepository.sumPointsByUser(b) - predictionRepository.sumPointsByUser(a))
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

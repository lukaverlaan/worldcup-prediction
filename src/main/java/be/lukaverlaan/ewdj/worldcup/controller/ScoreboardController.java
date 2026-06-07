package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.service.TeamService;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Collections;

@Controller
public class ScoreboardController {

    private final TeamService teamService;
    private final UserService userService;

    public ScoreboardController(TeamService teamService, UserService userService) {
        this.teamService = teamService;
        this.userService = userService;
    }

    @GetMapping("/scoreboard")
    public String publicScoreboard(Model model, Authentication auth) {
        model.addAttribute("top10", teamService.getTop10Teams());
        if (auth != null && auth.isAuthenticated()) {
            User user = userService.findByUsername(auth.getName());
            model.addAttribute("userTeamIds", teamService.getTeamIdsForUser(user));
        } else {
            model.addAttribute("userTeamIds", Collections.emptySet());
        }
        return "scoreboard/public";
    }
}

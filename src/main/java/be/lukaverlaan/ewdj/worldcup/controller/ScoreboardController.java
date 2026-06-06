package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ScoreboardController {

    private final TeamService teamService;

    public ScoreboardController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/scoreboard")
    public String publicScoreboard(Model model) {
        model.addAttribute("top10", teamService.getTop10Teams());
        return "scoreboard/public";
    }
}

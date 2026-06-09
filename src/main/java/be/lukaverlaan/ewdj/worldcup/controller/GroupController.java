package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.service.GroupStageService;
import be.lukaverlaan.ewdj.worldcup.service.PredictionService;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class GroupController {

    private final GroupStageService groupStageService;
    private final PredictionService predictionService;
    private final UserService userService;

    public GroupController(GroupStageService groupStageService,
                           PredictionService predictionService,
                           UserService userService) {
        this.groupStageService = groupStageService;
        this.predictionService = predictionService;
        this.userService = userService;
    }

    @GetMapping("/groups")
    public String groups(Model model, Authentication auth) {
        List<GroupStageService.GroupData> groups = groupStageService.getAllGroups();
        model.addAttribute("groups", groups);
        if (auth != null && auth.isAuthenticated()) {
            User user = userService.findByUsername(auth.getName());
            List<Match> allMatches = groups.stream()
                .flatMap(g -> g.matches().stream())
                .distinct()
                .toList();
            model.addAttribute("userPredictions", predictionService.getPredictionMapForUser(user, allMatches));
        }
        return "groups";
    }
}

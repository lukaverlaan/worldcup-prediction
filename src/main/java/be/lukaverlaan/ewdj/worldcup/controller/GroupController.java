package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.service.GroupStageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GroupController {

    private final GroupStageService groupStageService;

    public GroupController(GroupStageService groupStageService) {
        this.groupStageService = groupStageService;
    }

    @GetMapping("/groups")
    public String groups(Model model) {
        model.addAttribute("groups", groupStageService.getAllGroups());
        return "groups";
    }
}

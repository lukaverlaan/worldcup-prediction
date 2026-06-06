package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.service.MatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import be.lukaverlaan.ewdj.worldcup.domain.Match;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class HomeController {

    private final MatchService matchService;

    public HomeController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "upcoming") String tab,
                       Model model) {
        log.info("GET /, page={}, tab={}", page, tab);
        Page<Match> matchPage = "past".equals(tab)
                ? matchService.findPast(page, 10)
                : matchService.findUpcoming(page, 10);
        model.addAttribute("matches", matchPage.getContent());
        model.addAttribute("currentPage", matchPage.getNumber());
        model.addAttribute("totalPages", matchPage.getTotalPages());
        model.addAttribute("tab", tab);
        return "index";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        log.info("GET /access-denied");
        return "error/access-denied";
    }
}

package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.domain.Prediction;
import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.dto.PredictionStats;
import be.lukaverlaan.ewdj.worldcup.form.PredictionForm;
import be.lukaverlaan.ewdj.worldcup.dto.PredictionStats;
import be.lukaverlaan.ewdj.worldcup.service.CountryEntry;
import be.lukaverlaan.ewdj.worldcup.service.CountryRegistry;
import be.lukaverlaan.ewdj.worldcup.service.GroupStageService;
import be.lukaverlaan.ewdj.worldcup.service.MatchService;
import be.lukaverlaan.ewdj.worldcup.service.PredictionService;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import be.lukaverlaan.ewdj.worldcup.service.WebClientService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;
    private final PredictionService predictionService;
    private final UserService userService;
    private final WebClientService webClientService;
    private final GroupStageService groupStageService;
    private final CountryRegistry countryRegistry;

    public MatchController(MatchService matchService, PredictionService predictionService,
                           UserService userService, WebClientService webClientService,
                           GroupStageService groupStageService, CountryRegistry countryRegistry) {
        this.matchService = matchService;
        this.predictionService = predictionService;
        this.userService = userService;
        this.webClientService = webClientService;
        this.groupStageService = groupStageService;
        this.countryRegistry = countryRegistry;
    }

    @ModelAttribute("countries")
    public List<CountryEntry> countries() {
        return countryRegistry.getAllSorted();
    }

    @GetMapping
    public String matchList(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "upcoming") String tab,
                            @RequestParam(required = false) String country,
                            Model model, Authentication auth) {
        List<Match> matches;
        int currentPage = 0;
        int totalPages = 1;

        if (country != null && !country.isBlank()) {
            matches = "past".equals(tab)
                    ? matchService.findPastByCountry(country)
                    : matchService.findUpcomingByCountry(country);
        } else {
            Page<Match> matchPage = "past".equals(tab)
                    ? matchService.findPast(page, 15)
                    : matchService.findUpcoming(page, 15);
            matches = matchPage.getContent();
            currentPage = matchPage.getNumber();
            totalPages = matchPage.getTotalPages();
        }

        model.addAttribute("matches", matches);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("tab", tab);
        model.addAttribute("selectedCountry", country);
        if (auth != null && auth.isAuthenticated()) {
            User user = userService.findByUsername(auth.getName());
            model.addAttribute("userPredictions", predictionService.getPredictionMapForUser(user, matches));
        }
        return "match/list";
    }

    @GetMapping("/{id}")
    public String matchDetail(@PathVariable Long id,
                              @RequestParam(required = false) String from,
                              @RequestParam(required = false) Long teamId,
                              Model model, Authentication auth) {
        Match match = matchService.findById(id);
        model.addAttribute("match", match);

        boolean canPredict = match.getDateTime().minusHours(1).isAfter(LocalDateTime.now());
        model.addAttribute("canPredict", canPredict);

        // Alle prognoses voor dit match
        List<Prediction> allPredictions = predictionService.findByMatch(match);
        PredictionStats globalStats = predictionService.computeStats(allPredictions);
        model.addAttribute("globalStats", globalStats);

        if (auth != null && auth.isAuthenticated()) {
            User user = userService.findByUsername(auth.getName());
            Optional<Prediction> existing = predictionService.findByUserAndMatch(user, match);
            PredictionForm form = new PredictionForm();
            existing.ifPresent(p -> {
                form.setPredictedScoreA(p.getPredictedScoreA());
                form.setPredictedScoreB(p.getPredictedScoreB());
            });
            model.addAttribute("predictionForm", form);
            model.addAttribute("existingPrediction", existing.orElse(null));

            // Team stats per team
            Map<String, PredictionStats> allTeamStats = predictionService.computeAllTeamStats(match, user);
            if (!allTeamStats.isEmpty()) {
                model.addAttribute("allTeamStats", allTeamStats);
                // Achterwaartse compatibiliteit voor bestaande template-checks
                model.addAttribute("teamStats", allTeamStats.values().iterator().next());
            }
        }

        // Bepaal backUrl en prev/next navigatie op basis van from-param
        String backUrl = switch (from != null ? from : "") {
            case "home"          -> "/";
            case "matches"       -> "/matches";
            case "groups"        -> "/groups";
            case "mypredictions" -> "/my-predictions";
            case "team"          -> teamId != null ? "/teams/" + teamId : "/matches";
            default        -> "/matches";
        };
        model.addAttribute("backUrl", backUrl);
        model.addAttribute("fromParam", from);

        if (from != null && !from.isBlank()) {
            if ("groups".equals(from)) {
                List<Match> ordered = groupStageService.getAllMatchesInGroupOrder();
                int idx = -1;
                for (int i = 0; i < ordered.size(); i++) {
                    if (ordered.get(i).getId().equals(match.getId())) { idx = i; break; }
                }
                if (idx > 0) model.addAttribute("prevMatchId", ordered.get(idx - 1).getId());
                if (idx >= 0 && idx < ordered.size() - 1) model.addAttribute("nextMatchId", ordered.get(idx + 1).getId());
            } else {
                matchService.findPrevious(match.getDateTime())
                    .ifPresent(m -> model.addAttribute("prevMatchId", m.getId()));
                matchService.findNext(match.getDateTime())
                    .ifPresent(m -> model.addAttribute("nextMatchId", m.getId()));
            }
            if (teamId != null) model.addAttribute("teamId", teamId);
        }

        if (match.getStadiumCode() != null && !match.getStadiumCode().isBlank()) {
            Map capacity = webClientService.fetchStadiumCapacity(match.getStadiumCode());
            model.addAttribute("stadiumCapacity", capacity.get("capacity"));
        }

        return "match/detail";
    }

    @PostMapping("/{id}/predict")
    public String submitPrediction(@PathVariable Long id,
                                   @Valid @ModelAttribute("predictionForm") PredictionForm form,
                                   BindingResult result, Authentication auth,
                                   @RequestParam(required = false) String from,
                                   @RequestParam(required = false) Long teamId,
                                   RedirectAttributes ra, Model model) {
        Match match = matchService.findById(id);
        if (result.hasErrors()) {
            model.addAttribute("match", match);
            model.addAttribute("canPredict", match.getDateTime().minusHours(1).isAfter(LocalDateTime.now()));
            return "match/detail";
        }
        User user = userService.findByUsername(auth.getName());
        try {
            predictionService.savePrediction(user, match, form);
            ra.addFlashAttribute("successMessage", "prediction.saved.success");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        String redirect = "redirect:/matches/" + id;
        if (from != null && !from.isBlank()) {
            redirect += "?from=" + from;
            if (teamId != null) redirect += "&teamId=" + teamId;
        }
        return redirect;
    }
}

package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.form.MatchForm;
import be.lukaverlaan.ewdj.worldcup.form.ResultForm;
import be.lukaverlaan.ewdj.worldcup.service.CountryEntry;
import be.lukaverlaan.ewdj.worldcup.service.CountryRegistry;
import be.lukaverlaan.ewdj.worldcup.service.MatchService;
import be.lukaverlaan.ewdj.worldcup.service.PredictionService;
import be.lukaverlaan.ewdj.worldcup.service.TeamService;
import java.util.List;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final MatchService matchService;
    private final PredictionService predictionService;
    private final CountryRegistry countryRegistry;
    private final TeamService teamService;

    public AdminController(MatchService matchService, PredictionService predictionService,
                           CountryRegistry countryRegistry, TeamService teamService) {
        this.matchService = matchService;
        this.predictionService = predictionService;
        this.countryRegistry = countryRegistry;
        this.teamService = teamService;
    }

    @ModelAttribute("countries")
    public List<CountryEntry> countries() {
        return countryRegistry.getAllSorted();
    }

    @GetMapping("/matches")
    public String listMatches(@RequestParam(defaultValue = "0") int page, Model model) {
        log.info("GET /admin/matches, page={}", page);
        Page<Match> matchPage = matchService.findAllPaged(page, 15);
        model.addAttribute("matches", matchPage.getContent());
        model.addAttribute("currentPage", matchPage.getNumber());
        model.addAttribute("totalPages", matchPage.getTotalPages());
        return "admin/matches";
    }

    @GetMapping("/matches/new")
    public String newMatchForm(Model model) {
        model.addAttribute("matchForm", new MatchForm());
        model.addAttribute("editMode", false);
        return "admin/match-form";
    }

    @PostMapping("/matches/new")
    public String createMatch(@Valid @ModelAttribute("matchForm") MatchForm form,
                              BindingResult result, Model model, RedirectAttributes ra) {
        log.info("POST /admin/matches/new, teamA: {}, teamB: {}", form.getTeamA(), form.getTeamB());
        if (result.hasErrors()) {
            model.addAttribute("editMode", false);
            return "admin/match-form";
        }
        try {
            matchService.createMatch(form);
            ra.addFlashAttribute("successMessage", "match.added.success");
        } catch (IllegalArgumentException e) {
            result.reject(e.getMessage());
            model.addAttribute("editMode", false);
            return "admin/match-form";
        }
        return "redirect:/admin/matches";
    }

    @GetMapping("/matches/{id}/edit")
    public String editMatchForm(@PathVariable Long id, Model model) {
        Match match = matchService.findById(id);
        model.addAttribute("matchForm", matchService.toForm(match));
        model.addAttribute("matchId", id);
        model.addAttribute("editMode", true);
        return "admin/match-form";
    }

    @PostMapping("/matches/{id}/edit")
    public String updateMatch(@PathVariable Long id,
                              @Valid @ModelAttribute("matchForm") MatchForm form,
                              BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("matchId", id);
            model.addAttribute("editMode", true);
            return "admin/match-form";
        }
        try {
            matchService.updateMatch(id, form);
            ra.addFlashAttribute("successMessage", "match.updated.success");
        } catch (IllegalArgumentException e) {
            result.reject(e.getMessage());
            model.addAttribute("matchId", id);
            model.addAttribute("editMode", true);
            return "admin/match-form";
        }
        return "redirect:/admin/matches";
    }

    // ── Team management ──────────────────────────────────────────────────────

    @GetMapping("/teams")
    public String listTeams(Model model) {
        model.addAttribute("teams", teamService.getAllTeamsWithScores());
        return "admin/teams";
    }

    @PostMapping("/teams/{id}/delete")
    public String deleteTeam(@PathVariable Long id, RedirectAttributes ra) {
        teamService.deleteTeam(id);
        ra.addFlashAttribute("successMessage", "team.deleted.success");
        return "redirect:/admin/teams";
    }

    // ── Match management ──────────────────────────────────────────────────────

    @PostMapping("/matches/{id}/delete")
    public String deleteMatch(@PathVariable Long id, RedirectAttributes ra) {
        log.info("POST /admin/matches/{}/delete", id);
        matchService.deleteMatch(id);
        ra.addFlashAttribute("successMessage", "match.deleted.success");
        return "redirect:/admin/matches";
    }

    @GetMapping("/matches/{id}/result")
    public String resultForm(@PathVariable Long id, Model model) {
        Match match = matchService.findById(id);
        model.addAttribute("match", match);
        ResultForm form = new ResultForm();
        if (match.hasResult()) {
            form.setOfficialScoreA(match.getOfficialScoreA());
            form.setOfficialScoreB(match.getOfficialScoreB());
        }
        model.addAttribute("resultForm", form);
        return "admin/result-form";
    }

    @PostMapping("/matches/{id}/result")
    public String saveResult(@PathVariable Long id,
                             @Valid @ModelAttribute("resultForm") ResultForm form,
                             BindingResult result, Model model, RedirectAttributes ra) {
        Match match = matchService.findById(id);
        if (result.hasErrors()) {
            model.addAttribute("match", match);
            return "admin/result-form";
        }
        Match saved = matchService.saveResult(id, form.getOfficialScoreA(), form.getOfficialScoreB());
        predictionService.calculatePointsForMatch(saved);
        ra.addFlashAttribute("successMessage", "result.saved.success");
        return "redirect:/admin/matches";
    }
}

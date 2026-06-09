package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.Team;
import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.exception.TeamNotFoundException;
import be.lukaverlaan.ewdj.worldcup.form.CreateTeamForm;
import be.lukaverlaan.ewdj.worldcup.form.JoinTeamForm;
import be.lukaverlaan.ewdj.worldcup.repository.PredictionRepository;
import be.lukaverlaan.ewdj.worldcup.service.CountryEntry;
import be.lukaverlaan.ewdj.worldcup.service.CountryRegistry;
import be.lukaverlaan.ewdj.worldcup.service.PredictionService;
import be.lukaverlaan.ewdj.worldcup.service.TeamService;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;
    private final PredictionRepository predictionRepository;
    private final PredictionService predictionService;
    private final CountryRegistry countryRegistry;

    public TeamController(TeamService teamService, UserService userService,
                          PredictionRepository predictionRepository, PredictionService predictionService,
                          CountryRegistry countryRegistry) {
        this.teamService = teamService;
        this.userService = userService;
        this.predictionRepository = predictionRepository;
        this.predictionService = predictionService;
        this.countryRegistry = countryRegistry;
    }

    @ModelAttribute("countries")
    public List<CountryEntry> countries() {
        return countryRegistry.getAllSorted();
    }

    @GetMapping
    public String listTeams(Model model, Authentication auth) {
        User user = userService.findByUsername(auth.getName());
        model.addAttribute("teams", teamService.findTeamsForUser(user));
        model.addAttribute("joinForm", new JoinTeamForm());
        return "team/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("createTeamForm", new CreateTeamForm());
        return "team/create";
    }

    @PostMapping("/create")
    public String createTeam(@Valid @ModelAttribute("createTeamForm") CreateTeamForm form,
                             BindingResult result, Authentication auth, RedirectAttributes ra) {
        if (result.hasErrors()) return "team/create";
        User user = userService.findByUsername(auth.getName());
        try {
            Team team = teamService.createTeam(form, user);
            ra.addFlashAttribute("successMessage", "team.created.success");
            return "redirect:/teams/" + team.getId();
        } catch (IllegalArgumentException e) {
            result.rejectValue("name", e.getMessage());
            return "team/create";
        }
    }

    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("joinTeamForm", new JoinTeamForm());
        return "team/join";
    }

    @PostMapping("/join")
    public String joinTeam(@Valid @ModelAttribute("joinTeamForm") JoinTeamForm form,
                           BindingResult result, Authentication auth, RedirectAttributes ra) {
        if (result.hasErrors()) return "team/join";
        User user = userService.findByUsername(auth.getName());
        try {
            Team team = teamService.joinTeam(form.getInviteCode(), user);
            ra.addFlashAttribute("successMessage", "team.joined.success");
            return "redirect:/teams/" + team.getId();
        } catch (TeamNotFoundException e) {
            result.rejectValue("inviteCode", "team.invitecode.invalid");
            return "team/join";
        } catch (IllegalArgumentException e) {
            result.rejectValue("inviteCode", e.getMessage());
            return "team/join";
        }
    }

    @GetMapping("/{id}")
    public String teamDetail(@PathVariable Long id,
                             @RequestParam(defaultValue = "0") int detailPage,
                             @RequestParam(defaultValue = "upcoming") String detailTab,
                             @RequestParam(required = false) String country,
                             Model model, Authentication auth) {
        User user = userService.findByUsername(auth.getName());
        Team team = teamService.findById(id);
        boolean isAdmin = user.getRoles().contains("ADMIN");
        boolean isMember = team.getMembers().stream()
                .anyMatch(m -> m.getId().equals(user.getId()));
        if (!isMember && !isAdmin) {
            throw new SecurityException("team.access.denied");
        }
        java.util.Map<Long, Integer> pointsMap = predictionRepository.getPointsMapForUsers(team.getMembers());
        Map<User, Integer> memberScores = new LinkedHashMap<>();
        team.getMembers().stream()
            .sorted((a, b) -> pointsMap.getOrDefault(b.getId(), 0) - pointsMap.getOrDefault(a.getId(), 0))
            .forEach(m -> memberScores.put(m, pointsMap.getOrDefault(m.getId(), 0)));

        // Streaks per member
        java.util.Map<Long, Integer> streaks = new java.util.HashMap<>();
        for (User m : team.getMembers()) {
            streaks.put(m.getId(), predictionService.getStreakForUser(m));
        }

        int teamTotal = memberScores.values().stream().mapToInt(Integer::intValue).sum();

        List<Map<String, Object>> matchDetails;
        int detailCurrentPage, detailTotalPages;
        if (country != null && !country.isBlank()) {
            matchDetails = teamService.getMatchDetailsForTeamByCountry(new ArrayList<>(memberScores.keySet()), country, detailTab);
            detailCurrentPage = 0;
            detailTotalPages = 1;
        } else {
            Page<Map<String, Object>> matchDetailsPage =
                teamService.getMatchDetailsForTeamPaged(team, new ArrayList<>(memberScores.keySet()), detailPage, 10, detailTab);
            matchDetails = matchDetailsPage.getContent();
            detailCurrentPage = matchDetailsPage.getNumber();
            detailTotalPages = matchDetailsPage.getTotalPages();
        }

        model.addAttribute("team", team);
        model.addAttribute("memberScores", memberScores);
        model.addAttribute("teamTotal", teamTotal);
        model.addAttribute("matchDetails", matchDetails);
        model.addAttribute("detailCurrentPage", detailCurrentPage);
        model.addAttribute("detailTotalPages", detailTotalPages);
        model.addAttribute("detailTab", detailTab);
        model.addAttribute("selectedCountry", country);
        model.addAttribute("streaks", streaks);
        model.addAttribute("isOwner", team.getOwner().getId().equals(user.getId()));
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentUser", user);

        // Versies voor profielfoto's van alle leden
        Map<String, Long> pictureVersions = new HashMap<>();
        for (User member : memberScores.keySet()) {
            Instant updatedAt = userService.getProfilePictureUpdatedAt(member.getUsername());
            pictureVersions.put(member.getUsername(), updatedAt != null ? updatedAt.toEpochMilli() : 0L);
        }
        model.addAttribute("pictureVersions", pictureVersions);

        return "team/detail";
    }

    @PostMapping("/{id}/regenerate-code")
    public String regenerateCode(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        User user = userService.findByUsername(auth.getName());
        teamService.regenerateInviteCode(id, user);
        ra.addFlashAttribute("successMessage", "team.code.regenerated");
        return "redirect:/teams/" + id;
    }

    @GetMapping("/{id}/preview")
    @ResponseBody
    public java.util.List<java.util.Map<String, Object>> teamPreview(@PathVariable Long id) {
        return teamService.getTeamMemberPreview(id);
    }

    @PostMapping("/{id}/remove/{memberId}")
    public String removeMember(@PathVariable Long id, @PathVariable Long memberId,
                               Authentication auth, RedirectAttributes ra) {
        User user = userService.findByUsername(auth.getName());
        teamService.removeMember(id, memberId, user);
        ra.addFlashAttribute("successMessage", "team.member.removed");
        return "redirect:/teams/" + id;
    }

    @PostMapping("/{id}/leave")
    public String leaveTeam(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        User user = userService.findByUsername(auth.getName());
        Team team = teamService.findById(id);
        if (team.getOwner().getId().equals(user.getId())) {
            ra.addFlashAttribute("errorMessage", "team.leave.owner");
            return "redirect:/teams/" + id;
        }
        teamService.removeMember(id, user.getId(), user);
        ra.addFlashAttribute("successMessage", "team.left.success");
        return "redirect:/teams";
    }
}

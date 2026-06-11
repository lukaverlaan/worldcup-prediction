package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.repository.TeamRepository;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.Instant;

@ControllerAdvice
public class GlobalModelAdvice {

    private final UserService userService;
    private final TeamRepository teamRepository;

    public GlobalModelAdvice(UserService userService, TeamRepository teamRepository) {
        this.userService = userService;
        this.teamRepository = teamRepository;
    }

    @ModelAttribute("currentUserPictureVersion")
    public long currentUserPictureVersion(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return 0;
        Instant updatedAt = userService.getProfilePictureUpdatedAt(userDetails.getUsername());
        return updatedAt != null ? updatedAt.toEpochMilli() : 0;
    }

    @ModelAttribute("userHasNoTeam")
    public boolean userHasNoTeam(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return false;
        User user = userService.findByUsername(userDetails.getUsername());
        return teamRepository.findByMembersContains(user).isEmpty();
    }
}

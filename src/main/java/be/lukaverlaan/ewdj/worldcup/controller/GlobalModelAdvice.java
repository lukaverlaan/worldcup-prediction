package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.Instant;

@ControllerAdvice
public class GlobalModelAdvice {

    private final UserService userService;

    public GlobalModelAdvice(UserService userService) {
        this.userService = userService;
    }

    // Voegt pictureVersion toe aan elk model zodat de sidebar altijd de juiste avatar toont
    @ModelAttribute("currentUserPictureVersion")
    public long currentUserPictureVersion(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return 0;
        Instant updatedAt = userService.getProfilePictureUpdatedAt(userDetails.getUsername());
        return updatedAt != null ? updatedAt.toEpochMilli() : 0;
    }
}

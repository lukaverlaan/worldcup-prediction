package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.form.ChangeEmailForm;
import be.lukaverlaan.ewdj.worldcup.form.ChangePasswordForm;
import be.lukaverlaan.ewdj.worldcup.form.ChangeUsernameForm;
import be.lukaverlaan.ewdj.worldcup.form.RegistrationForm;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        java.time.Instant updatedAt = userService.getProfilePictureUpdatedAt(userDetails.getUsername());
        model.addAttribute("hasPicture", updatedAt != null);
        model.addAttribute("pictureVersion", updatedAt != null ? updatedAt.toEpochMilli() : 0);
        model.addAttribute("changeUsernameForm", new ChangeUsernameForm());
        model.addAttribute("changeEmailForm", new ChangeEmailForm());
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return "profile";
    }

    @PostMapping("/profile/username")
    public String changeUsername(@AuthenticationPrincipal UserDetails userDetails,
                                 @Valid @ModelAttribute("changeUsernameForm") ChangeUsernameForm form,
                                 BindingResult result, Model model,
                                 HttpServletRequest request, RedirectAttributes ra) {
        User currentUser = userService.findByUsername(userDetails.getUsername());

        if (result.hasErrors()) {
            model.addAttribute("user", currentUser);
            return "profile";
        }

        String newUsername = form.getNewUsername().trim();

        if (newUsername.equalsIgnoreCase(currentUser.getUsername())) {
            result.rejectValue("newUsername", "validation.username.same", "Dit is al je huidige gebruikersnaam");
            model.addAttribute("user", currentUser);
            return "profile";
        }

        if (userService.usernameExists(newUsername)) {
            result.rejectValue("newUsername", "validation.username.taken", "Username already taken");
            model.addAttribute("user", currentUser);
            return "profile";
        }

        User updatedUser = userService.changeUsername(currentUser.getUsername(), newUsername);

        // Vernieuw de Spring Security sessie met de nieuwe gebruikersnaam
        UserDetails newDetails = userService.loadUserByUsername(updatedUser.getUsername());
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(newDetails, null, newDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        request.getSession(true).setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext());

        ra.addFlashAttribute("successMessage", "profile.username.success");
        return "redirect:/profile";
    }

    // Serveer profielfoto met ETag-caching (lichte check, BLOB alleen laden als nodig)
    @GetMapping("/profile/picture/{username}")
    @ResponseBody
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String username,
                                                     HttpServletRequest request) {
        java.time.Instant updatedAt = userService.getProfilePictureUpdatedAt(username);

        if (updatedAt != null) {
            String etag = "\"" + updatedAt.toEpochMilli() + "\"";
            String ifNoneMatch = request.getHeader("If-None-Match");
            if (etag.equals(ifNoneMatch)) {
                // Browser heeft al de juiste versie — geen BLOB laden
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }
            // BLOB laden en terugsturen met ETag
            byte[] picture = userService.getProfilePictureBytes(username);
            String type    = userService.getProfilePictureType(username);
            if (picture != null && picture.length > 0 && type != null) {
                return ResponseEntity.ok()
                    .header(HttpHeaders.ETAG, etag)
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .contentType(MediaType.parseMediaType(type))
                    .body(picture);
            }
        }

        // Fallback: SVG-initiaal-avatar (lichte cache)
        String initial = (username != null && !username.isEmpty())
            ? String.valueOf(Character.toUpperCase(username.charAt(0))) : "?";
        String svg = "<svg xmlns='http://www.w3.org/2000/svg' width='80' height='80'>" +
            "<circle cx='40' cy='40' r='40' fill='#15803d'/>" +
            "<text x='40' y='40' font-size='38' font-family='Arial,sans-serif' " +
            "font-weight='bold' fill='white' text-anchor='middle' dominant-baseline='central'>" + initial + "</text></svg>";
        return ResponseEntity.ok()
            .header(HttpHeaders.CACHE_CONTROL, "no-cache")
            .contentType(MediaType.valueOf("image/svg+xml"))
            .body(svg.getBytes());
    }

    @PostMapping("/profile/email")
    public String changeEmail(@AuthenticationPrincipal UserDetails userDetails,
                              @Valid @ModelAttribute("changeEmailForm") ChangeEmailForm form,
                              BindingResult result, Model model, RedirectAttributes ra) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        if (!form.getNewEmail().equals(form.getConfirmEmail())) {
            result.rejectValue("confirmEmail", "validation.email.mismatch", "E-mailadressen komen niet overeen");
        }
        if (!result.hasFieldErrors("newEmail") && form.getNewEmail().equalsIgnoreCase(currentUser.getEmail())) {
            result.rejectValue("newEmail", "validation.email.same", "Dit is al je huidige e-mailadres");
        }
        if (!result.hasFieldErrors("newEmail") && userService.emailExists(form.getNewEmail())) {
            result.rejectValue("newEmail", "validation.email.taken", "Dit e-mailadres is al in gebruik");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", currentUser);
            model.addAttribute("hasPicture", userService.getProfilePictureUpdatedAt(userDetails.getUsername()) != null);
            model.addAttribute("pictureVersion", userService.getProfilePictureUpdatedAt(userDetails.getUsername()) != null ? userService.getProfilePictureUpdatedAt(userDetails.getUsername()).toEpochMilli() : 0);
            model.addAttribute("changeUsernameForm", new ChangeUsernameForm());
            model.addAttribute("changePasswordForm", new ChangePasswordForm());
            model.addAttribute("openCard", "email");
            return "profile";
        }
        userService.changeEmail(userDetails.getUsername(), form.getNewEmail().trim());
        ra.addFlashAttribute("successMessage", "profile.email.success");
        return "redirect:/profile";
    }

    @PostMapping("/profile/password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @Valid @ModelAttribute("changePasswordForm") ChangePasswordForm form,
                                 BindingResult result, Model model, RedirectAttributes ra) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        if (!result.hasFieldErrors("currentPassword") && !userService.checkPassword(userDetails.getUsername(), form.getCurrentPassword())) {
            result.rejectValue("currentPassword", "validation.password.wrong", "Huidig wachtwoord is onjuist");
        }
        if (!result.hasFieldErrors("newPassword") && !result.hasFieldErrors("confirmPassword")
                && !form.getNewPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "validation.password.mismatch", "Wachtwoorden komen niet overeen");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", currentUser);
            model.addAttribute("hasPicture", userService.getProfilePictureUpdatedAt(userDetails.getUsername()) != null);
            model.addAttribute("pictureVersion", userService.getProfilePictureUpdatedAt(userDetails.getUsername()) != null ? userService.getProfilePictureUpdatedAt(userDetails.getUsername()).toEpochMilli() : 0);
            model.addAttribute("changeUsernameForm", new ChangeUsernameForm());
            model.addAttribute("changeEmailForm", new ChangeEmailForm());
            model.addAttribute("openCard", "password");
            return "profile";
        }
        userService.changePassword(userDetails.getUsername(), form.getNewPassword());
        ra.addFlashAttribute("successMessage", "profile.password.success");
        return "redirect:/profile";
    }

    @PostMapping("/profile/picture/reset")
    public String resetProfilePicture(@AuthenticationPrincipal UserDetails userDetails,
                                      RedirectAttributes ra) {
        userService.resetProfilePicture(userDetails.getUsername());
        ra.addFlashAttribute("successMessage", "profile.picture.reset.success");
        return "redirect:/profile";
    }

    @PostMapping("/profile/picture")
    public String uploadProfilePicture(@AuthenticationPrincipal UserDetails userDetails,
                                       @RequestParam("picture") MultipartFile file,
                                       RedirectAttributes ra) throws IOException {
        if (file.isEmpty()) {
            ra.addFlashAttribute("pictureError", "profile.picture.empty");
            return "redirect:/profile";
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            ra.addFlashAttribute("pictureError", "profile.picture.invalid");
            return "redirect:/profile";
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            ra.addFlashAttribute("pictureError", "profile.picture.toobig");
            return "redirect:/profile";
        }
        userService.saveProfilePicture(userDetails.getUsername(), file.getBytes(), contentType);
        ra.addFlashAttribute("successMessage", "profile.picture.success");
        return "redirect:/profile";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                           BindingResult result, Model model, HttpServletRequest request) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "validation.password.mismatch", "Passwords do not match");
        }
        if (!result.hasFieldErrors("username") && userService.usernameExists(form.getUsername())) {
            result.rejectValue("username", "validation.username.taken", "Username already taken");
        }
        if (!result.hasFieldErrors("email") && userService.emailExists(form.getEmail())) {
            result.rejectValue("email", "validation.email.taken", "Email already in use");
        }
        if (result.hasErrors()) {
            return "register";
        }
        User user = userService.registerUser(form);
        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        request.getSession(true).setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext());
        return "redirect:/";
    }
}

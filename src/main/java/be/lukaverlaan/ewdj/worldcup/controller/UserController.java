package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.form.ChangeUsernameForm;
import be.lukaverlaan.ewdj.worldcup.form.RegistrationForm;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("changeUsernameForm", new ChangeUsernameForm());
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

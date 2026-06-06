package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.form.RegistrationForm;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
                           BindingResult result, Model model) {
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
        userService.registerUser(form);
        return "redirect:/login?registered";
    }
}

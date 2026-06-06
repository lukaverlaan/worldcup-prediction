package be.lukaverlaan.ewdj.worldcup.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

    @NotBlank(message = "{validation.username.required}")
    @Size(min = 3, max = 30, message = "{validation.username.size}")
    private String username;

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 6, message = "{validation.password.size}")
    private String password;

    @NotBlank(message = "{validation.password.confirm.required}")
    private String confirmPassword;

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

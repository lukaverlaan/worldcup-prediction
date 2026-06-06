package be.lukaverlaan.ewdj.worldcup.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTeamForm {

    @NotBlank(message = "{validation.teamname.required}")
    @Size(min = 2, max = 50, message = "{validation.teamname.size}")
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

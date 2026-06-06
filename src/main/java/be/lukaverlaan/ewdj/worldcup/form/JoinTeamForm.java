package be.lukaverlaan.ewdj.worldcup.form;

import jakarta.validation.constraints.NotBlank;

public class JoinTeamForm {

    @NotBlank(message = "{validation.invitecode.required}")
    private String inviteCode;

    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
}

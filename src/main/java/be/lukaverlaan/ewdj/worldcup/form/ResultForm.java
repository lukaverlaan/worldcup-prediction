package be.lukaverlaan.ewdj.worldcup.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ResultForm {

    @NotNull(message = "{validation.scoreA.required}")
    @Min(value = 0, message = "{validation.score.min}")
    private Integer officialScoreA;

    @NotNull(message = "{validation.scoreB.required}")
    @Min(value = 0, message = "{validation.score.min}")
    private Integer officialScoreB;

    public Integer getOfficialScoreA() { return officialScoreA; }
    public void setOfficialScoreA(Integer officialScoreA) { this.officialScoreA = officialScoreA; }
    public Integer getOfficialScoreB() { return officialScoreB; }
    public void setOfficialScoreB(Integer officialScoreB) { this.officialScoreB = officialScoreB; }
}

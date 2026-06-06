package be.lukaverlaan.ewdj.worldcup.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PredictionForm {

    @NotNull(message = "{validation.scoreA.required}")
    @Min(value = 0, message = "{validation.score.min}")
    private Integer predictedScoreA;

    @NotNull(message = "{validation.scoreB.required}")
    @Min(value = 0, message = "{validation.score.min}")
    private Integer predictedScoreB;

    public Integer getPredictedScoreA() { return predictedScoreA; }
    public void setPredictedScoreA(Integer predictedScoreA) { this.predictedScoreA = predictedScoreA; }
    public Integer getPredictedScoreB() { return predictedScoreB; }
    public void setPredictedScoreB(Integer predictedScoreB) { this.predictedScoreB = predictedScoreB; }
}

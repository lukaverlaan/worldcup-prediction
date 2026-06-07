package be.lukaverlaan.ewdj.worldcup.dto;

public record PredictionStats(
    int total,
    double winPctA,
    double drawPct,
    double winPctB,
    double avgScoreA,
    double avgScoreB
) {
    public boolean isEmpty() { return total == 0; }

    public String avgScoreAFormatted() { return String.valueOf((int) Math.round(avgScoreA)); }
    public String avgScoreBFormatted() { return String.valueOf((int) Math.round(avgScoreB)); }
    public int winPctAInt()  { return (int) Math.round(winPctA); }
    public int drawPctInt()  { return (int) Math.round(drawPct); }
    public int winPctBInt()  { return (int) Math.round(winPctB); }
}

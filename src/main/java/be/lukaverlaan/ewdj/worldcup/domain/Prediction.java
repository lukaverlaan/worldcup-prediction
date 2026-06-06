package be.lukaverlaan.ewdj.worldcup.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "predictions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "match_id"})
})
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(nullable = false)
    private Integer predictedScoreA;

    @Column(nullable = false)
    private Integer predictedScoreB;

    private Integer points;

    public Prediction() {}

    public Prediction(User user, Match match, int predictedScoreA, int predictedScoreB) {
        this.user = user;
        this.match = match;
        this.predictedScoreA = predictedScoreA;
        this.predictedScoreB = predictedScoreB;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }
    public Integer getPredictedScoreA() { return predictedScoreA; }
    public void setPredictedScoreA(Integer predictedScoreA) { this.predictedScoreA = predictedScoreA; }
    public Integer getPredictedScoreB() { return predictedScoreB; }
    public void setPredictedScoreB(Integer predictedScoreB) { this.predictedScoreB = predictedScoreB; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
}

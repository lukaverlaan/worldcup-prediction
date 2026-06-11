package be.lukaverlaan.ewdj.worldcup.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tournament_votes")
public class TournamentVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = true)
    private String country;

    private String scorer1;
    private String scorer2;
    private String scorer3;
    private String scorer4;
    private String scorer5;

    @Column(nullable = false)
    private LocalDateTime votedAt;

    public TournamentVote() {}

    public TournamentVote(User user) {
        this.user = user;
        this.votedAt = LocalDateTime.now();
    }

    public TournamentVote(User user, String country) {
        this.user = user;
        this.country = country;
        this.votedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getScorer1() { return scorer1; }
    public void setScorer1(String scorer1) { this.scorer1 = scorer1; }
    public String getScorer2() { return scorer2; }
    public void setScorer2(String scorer2) { this.scorer2 = scorer2; }
    public String getScorer3() { return scorer3; }
    public void setScorer3(String scorer3) { this.scorer3 = scorer3; }
    public String getScorer4() { return scorer4; }
    public void setScorer4(String scorer4) { this.scorer4 = scorer4; }
    public String getScorer5() { return scorer5; }
    public void setScorer5(String scorer5) { this.scorer5 = scorer5; }
    public LocalDateTime getVotedAt() { return votedAt; }

    public boolean hasScorers() {
        return scorer1 != null && scorer2 != null && scorer3 != null && scorer4 != null && scorer5 != null;
    }
}

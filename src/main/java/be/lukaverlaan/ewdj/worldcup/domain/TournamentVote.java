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

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private LocalDateTime votedAt;

    public TournamentVote() {}

    public TournamentVote(User user, String country) {
        this.user = user;
        this.country = country;
        this.votedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getCountry() { return country; }
    public LocalDateTime getVotedAt() { return votedAt; }
}

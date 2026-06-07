package be.lukaverlaan.ewdj.worldcup.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String teamA;

    @Column(nullable = false)
    private String teamB;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    private String city;
    private String stadium;

    @Column(length = 4)
    private String stadiumCode;

    private Integer checksum;

    private Integer officialScoreA;
    private Integer officialScoreB;

    @Column(unique = true)
    private Long apiFootballFixtureId;

    private String round;

    public Match() {}

    public Long getId() { return id; }
    public String getTeamA() { return teamA; }
    public void setTeamA(String teamA) { this.teamA = teamA; }
    public String getTeamB() { return teamB; }
    public void setTeamB(String teamB) { this.teamB = teamB; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getStadium() { return stadium; }
    public void setStadium(String stadium) { this.stadium = stadium; }
    public String getStadiumCode() { return stadiumCode; }
    public void setStadiumCode(String stadiumCode) { this.stadiumCode = stadiumCode; }
    public Integer getChecksum() { return checksum; }
    public void setChecksum(Integer checksum) { this.checksum = checksum; }
    public Integer getOfficialScoreA() { return officialScoreA; }
    public void setOfficialScoreA(Integer officialScoreA) { this.officialScoreA = officialScoreA; }
    public Integer getOfficialScoreB() { return officialScoreB; }
    public void setOfficialScoreB(Integer officialScoreB) { this.officialScoreB = officialScoreB; }

    public Long getApiFootballFixtureId() { return apiFootballFixtureId; }
    public void setApiFootballFixtureId(Long apiFootballFixtureId) { this.apiFootballFixtureId = apiFootballFixtureId; }
    public String getRound() { return round; }
    public void setRound(String round) { this.round = round; }

    public boolean hasResult() {
        return officialScoreA != null && officialScoreB != null;
    }
}

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

    private Integer liveScoreA;
    private Integer liveScoreB;
    private Integer liveMinute;
    private String liveStatus;

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

    public Integer getLiveScoreA() { return liveScoreA; }
    public void setLiveScoreA(Integer liveScoreA) { this.liveScoreA = liveScoreA; }
    public Integer getLiveScoreB() { return liveScoreB; }
    public void setLiveScoreB(Integer liveScoreB) { this.liveScoreB = liveScoreB; }
    public Integer getLiveMinute() { return liveMinute; }
    public void setLiveMinute(Integer liveMinute) { this.liveMinute = liveMinute; }
    public String getLiveStatus() { return liveStatus; }
    public void setLiveStatus(String liveStatus) { this.liveStatus = liveStatus; }

    public boolean hasResult() {
        return officialScoreA != null && officialScoreB != null;
    }

    public boolean isLive() {
        return liveStatus != null && !liveStatus.isBlank()
            && !liveStatus.equals("FT") && !liveStatus.equals("AET") && !liveStatus.equals("PEN")
            && !liveStatus.equals("NS") && !liveStatus.equals("TBD");
    }
}

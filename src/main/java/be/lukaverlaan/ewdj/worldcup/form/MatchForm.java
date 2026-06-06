package be.lukaverlaan.ewdj.worldcup.form;

import be.lukaverlaan.ewdj.worldcup.validation.ValidChecksum;
import be.lukaverlaan.ewdj.worldcup.validation.WorldCupDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@ValidChecksum
public class MatchForm {

    @NotBlank(message = "{validation.teamA.required}")
    private String teamA;

    @NotBlank(message = "{validation.teamB.required}")
    private String teamB;

    @NotNull(message = "{validation.datetime.required}")
    @WorldCupDate
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateTime;

    private String city;
    private String stadium;

    @Pattern(regexp = "\\d{4}", message = "{validation.stadiumcode.digits}")
    private String stadiumCode;

    private Integer checksum;

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
}

package be.lukaverlaan.ewdj.worldcup;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.repository.MatchRepository;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DataLoader {

    private final UserService userService;
    private final MatchRepository matchRepository;

    public DataLoader(UserService userService, MatchRepository matchRepository) {
        this.userService = userService;
        this.matchRepository = matchRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        userService.createAdminUser("admin", "Luk4D3v82*", "admin@worldcup.be");

        if (matchRepository.count() == 0) {
            createMatch("Mexico", "Poland", LocalDateTime.of(2026, 6, 12, 18, 0), "Mexico City", "Estadio Azteca", "4702", 4702 % 97);
            createMatch("France", "Brazil", LocalDateTime.of(2026, 6, 14, 21, 0), "New York", "MetLife Stadium", "3142", 3142 % 97);
            createMatch("Germany", "Argentina", LocalDateTime.of(2026, 6, 16, 18, 0), "Los Angeles", "SoFi Stadium", "2891", 2891 % 97);
            createMatch("Netherlands", "Belgium", LocalDateTime.of(2026, 6, 18, 15, 0), "Dallas", "AT&T Stadium", "6074", 6074 % 97);
            createMatch("Spain", "Portugal", LocalDateTime.of(2026, 6, 20, 21, 0), "Miami", "Hard Rock Stadium", "5230", 5230 % 97);
        }
    }

    private void createMatch(String teamA, String teamB, LocalDateTime dt, String city, String stadium, String code, int checksum) {
        Match m = new Match();
        m.setTeamA(teamA);
        m.setTeamB(teamB);
        m.setDateTime(dt);
        m.setCity(city);
        m.setStadium(stadium);
        m.setStadiumCode(code);
        m.setChecksum(checksum);
        matchRepository.save(m);
    }
}

package be.lukaverlaan.ewdj.worldcup;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.domain.Prediction;
import be.lukaverlaan.ewdj.worldcup.domain.Team;
import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.repository.MatchRepository;
import be.lukaverlaan.ewdj.worldcup.repository.PredictionRepository;
import be.lukaverlaan.ewdj.worldcup.repository.TeamRepository;
import be.lukaverlaan.ewdj.worldcup.repository.UserRepository;
import be.lukaverlaan.ewdj.worldcup.service.MatchSyncService;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class DataLoader {

    private final UserService userService;
    private final MatchSyncService matchSyncService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final PredictionRepository predictionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserService userService, MatchSyncService matchSyncService,
                      UserRepository userRepository, TeamRepository teamRepository,
                      MatchRepository matchRepository, PredictionRepository predictionRepository,
                      PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.matchSyncService = matchSyncService;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
        this.predictionRepository = predictionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        userService.createAdminUser("admin", "Luk4D3v82*", "admin@worldcup.be");
        matchSyncService.importFixtures();
        seedTestTeams();
    }

    private void seedTestTeams() {
        // Sla over als testdata al bestaat
        if (teamRepository.existsByName("TestTeam1")) return;

        User admin = userRepository.findByUsername("admin").orElseThrow();
        List<Match> matches = matchRepository.findAllByOrderByDateTimeAsc();
        if (matches.isEmpty()) return;
        Match match = matches.get(0);

        // Maak 10 testgebruikers aan met dalende punten (100, 90, 80, ..., 10)
        // en stop admin in een 11e team (0 punten → buiten top 10)
        for (int i = 1; i <= 10; i++) {
            String username = "testuser" + i;
            User user;
            if (!userRepository.existsByUsername(username)) {
                user = new User(username, passwordEncoder.encode("Test1234!"), username + "@test.be", Set.of("USER"));
                userRepository.save(user);
            } else {
                user = userRepository.findByUsername(username).orElseThrow();
            }

            // Geef punten via een prediction op de eerste wedstrijd
            if (predictionRepository.findByUserAndMatch(user, match).isEmpty()) {
                Prediction pred = new Prediction(user, match, 1, 0);
                pred.setPoints((11 - i) * 10); // user1=100, user2=90, ..., user10=10
                predictionRepository.save(pred);
            }

            // Maak een team aan met deze gebruiker
            String teamName = "TestTeam" + i;
            if (!teamRepository.existsByName(teamName)) {
                Team team = new Team();
                team.setName(teamName);
                team.setInviteCode(UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
                team.setOwner(user);
                team.getMembers().add(user);
                teamRepository.save(team);
            }
        }

        // Team 11: admin erin, 0 punten → buiten top 10
        if (!teamRepository.existsByName("AdminTeam")) {
            Team adminTeam = new Team();
            adminTeam.setName("AdminTeam");
            adminTeam.setInviteCode(UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
            adminTeam.setOwner(admin);
            adminTeam.getMembers().add(admin);
            teamRepository.save(adminTeam);
        }
    }
}

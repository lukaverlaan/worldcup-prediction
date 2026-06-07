package be.lukaverlaan.ewdj.worldcup.service;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.repository.MatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class MatchSyncService {

    private final MatchRepository matchRepository;
    private final ApiFootballService apiFootballService;

    public MatchSyncService(MatchRepository matchRepository, ApiFootballService apiFootballService) {
        this.matchRepository = matchRepository;
        this.apiFootballService = apiFootballService;
    }

    /**
     * Every 5 minutes, check matches that ended but don't have a score yet
     * and pull the final result from api-football.
     */
    @Scheduled(fixedDelayString = "${apifootball.sync.interval-ms:300000}")
    @Transactional
    public void syncFinishedMatchScores() {
        List<Match> unscored = matchRepository.findByOfficialScoreAIsNullAndDateTimeLessThan(
            LocalDateTime.now().minusMinutes(100) // safe buffer after a 90-min match
        );

        if (unscored.isEmpty()) return;

        log.info("Syncing scores for {} unscored matches", unscored.size());

        for (Match match : unscored) {
            if (match.getApiFootballFixtureId() == null) continue;

            ApiFootballService.FixtureData data = apiFootballService.fetchFixture(match.getApiFootballFixtureId());
            if (data == null || !data.finished()) continue;
            if (data.scoreHome() == null || data.scoreAway() == null) continue;

            match.setOfficialScoreA(data.scoreHome());
            match.setOfficialScoreB(data.scoreAway());
            matchRepository.save(match);
            log.info("Synced score for match {} ({} vs {}): {}-{}",
                match.getId(), match.getTeamA(), match.getTeamB(),
                data.scoreHome(), data.scoreAway());
        }
    }

    /**
     * Called once at startup from DataLoader to import all fixtures.
     *
     * For existing matches (no apiFootballFixtureId yet), we reconcile by team names + date
     * so that prediction foreign keys stay intact. New fixtures are inserted normally.
     */
    @Transactional
    public void importFixtures() {
        List<ApiFootballService.FixtureData> fixtures = apiFootballService.fetchAllFixtures();

        if (fixtures.isEmpty()) {
            log.warn("No fixtures returned from api-football — check your API key and league/season config");
            return;
        }

        List<Match> existingUnlinked = matchRepository.findAll().stream()
            .filter(m -> m.getApiFootballFixtureId() == null)
            .toList();

        int linked = 0;
        int created = 0;

        for (ApiFootballService.FixtureData f : fixtures) {
            // Already linked — skip
            if (matchRepository.findByApiFootballFixtureId(f.fixtureId()).isPresent()) continue;

            // Try to find an existing match with the same teams and same day (preserves prediction FK)
            Match existing = existingUnlinked.stream()
                .filter(m -> teamsMatch(m, f) && sameDay(m.getDateTime(), f.dateTime()))
                .findFirst()
                .orElse(null);

            if (existing != null) {
                existing.setApiFootballFixtureId(f.fixtureId());
                existing.setCity(f.city());
                existing.setStadium(f.stadium());
                existing.setDateTime(f.dateTime());
                if (f.finished() && f.scoreHome() != null && f.scoreAway() != null
                        && !existing.hasResult()) {
                    existing.setOfficialScoreA(f.scoreHome());
                    existing.setOfficialScoreB(f.scoreAway());
                }
                matchRepository.save(existing);
                linked++;
                log.info("Linked existing match {} ({} vs {}) to fixture id {}", existing.getId(), existing.getTeamA(), existing.getTeamB(), f.fixtureId());
            } else {
                Match match = new Match();
                match.setApiFootballFixtureId(f.fixtureId());
                match.setTeamA(f.homeTeam());
                match.setTeamB(f.awayTeam());
                match.setDateTime(f.dateTime());
                match.setCity(f.city());
                match.setStadium(f.stadium());
                if (f.finished() && f.scoreHome() != null && f.scoreAway() != null) {
                    match.setOfficialScoreA(f.scoreHome());
                    match.setOfficialScoreB(f.scoreAway());
                }
                matchRepository.save(match);
                created++;
            }
        }

        log.info("Fixture import done: {} linked to existing matches, {} newly created", linked, created);
    }

    private boolean teamsMatch(Match m, ApiFootballService.FixtureData f) {
        return m.getTeamA().equalsIgnoreCase(f.homeTeam()) && m.getTeamB().equalsIgnoreCase(f.awayTeam())
            || m.getTeamA().equalsIgnoreCase(f.awayTeam()) && m.getTeamB().equalsIgnoreCase(f.homeTeam());
    }

    private boolean sameDay(LocalDateTime a, LocalDateTime b) {
        // Allow ±1 day tolerance to handle UTC vs local time (e.g. CEST = UTC+2)
        long diffDays = Math.abs(a.toLocalDate().toEpochDay() - b.toLocalDate().toEpochDay());
        return diffDays <= 1;
    }
}

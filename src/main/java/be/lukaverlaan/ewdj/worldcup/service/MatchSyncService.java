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

    private static final List<String> FINISHED_STATUSES = List.of("FT", "AET", "PEN");

    private final MatchRepository matchRepository;
    private final ApiFootballService apiFootballService;
    private final PredictionService predictionService;

    public MatchSyncService(MatchRepository matchRepository, ApiFootballService apiFootballService,
                            PredictionService predictionService) {
        this.matchRepository = matchRepository;
        this.apiFootballService = apiFootballService;
        this.predictionService = predictionService;
    }

    /**
     * Every minute: only polls when at least one match has started but has no official result yet.
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void syncActiveMatches() {
        List<Match> activeMatches = matchRepository
            .findByOfficialScoreAIsNullAndApiFootballFixtureIdIsNotNullAndDateTimeLessThanEqual(LocalDateTime.now());

        if (activeMatches.isEmpty()) return;

        log.info("Polling live data for {} active match(es)", activeMatches.size());

        for (Match match : activeMatches) {
            ApiFootballService.FixtureData data = apiFootballService.fetchFixture(match.getApiFootballFixtureId());
            if (data == null) continue;

            match.setLiveStatus(data.statusShort());
            match.setLiveMinute(data.elapsed());
            match.setLiveScoreA(data.scoreHome());
            match.setLiveScoreB(data.scoreAway());

            if (FINISHED_STATUSES.contains(data.statusShort()) && data.scoreHome() != null && data.scoreAway() != null) {
                match.setOfficialScoreA(data.scoreHome());
                match.setOfficialScoreB(data.scoreAway());
                matchRepository.save(match);
                predictionService.calculatePointsForMatch(match);
                log.info("Match {} ({} vs {}) finished: {}-{} ({})",
                    match.getId(), match.getTeamA(), match.getTeamB(),
                    data.scoreHome(), data.scoreAway(), data.statusShort());
            } else {
                matchRepository.save(match);
            }
        }
    }

    /**
     * Called once at startup from DataLoader to import all fixtures.
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
            if (matchRepository.findByApiFootballFixtureId(f.fixtureId()).isPresent()) continue;

            Match existing = existingUnlinked.stream()
                .filter(m -> teamsMatch(m, f) && sameDay(m.getDateTime(), f.dateTime()))
                .findFirst()
                .orElse(null);

            if (existing != null) {
                existing.setApiFootballFixtureId(f.fixtureId());
                existing.setCity(f.city());
                existing.setStadium(f.stadium());
                existing.setDateTime(f.dateTime());
                existing.setRound(f.round());
                if (f.finished() && f.scoreHome() != null && f.scoreAway() != null && !existing.hasResult()) {
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
                match.setRound(f.round());
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

    public int resyncAllTimes() {
        List<ApiFootballService.FixtureData> fixtures = apiFootballService.fetchAllFixtures();
        int updated = 0;
        for (ApiFootballService.FixtureData f : fixtures) {
            var opt = matchRepository.findByApiFootballFixtureId(f.fixtureId());
            if (opt.isPresent()) {
                opt.get().setDateTime(f.dateTime());
                matchRepository.save(opt.get());
                updated++;
            }
        }
        log.info("Resynced times for {} matches", updated);
        return updated;
    }

    private boolean teamsMatch(Match m, ApiFootballService.FixtureData f) {
        return m.getTeamA().equalsIgnoreCase(f.homeTeam()) && m.getTeamB().equalsIgnoreCase(f.awayTeam())
            || m.getTeamA().equalsIgnoreCase(f.awayTeam()) && m.getTeamB().equalsIgnoreCase(f.homeTeam());
    }

    private boolean sameDay(java.time.LocalDateTime a, java.time.LocalDateTime b) {
        long diffDays = Math.abs(a.toLocalDate().toEpochDay() - b.toLocalDate().toEpochDay());
        return diffDays <= 1;
    }
}

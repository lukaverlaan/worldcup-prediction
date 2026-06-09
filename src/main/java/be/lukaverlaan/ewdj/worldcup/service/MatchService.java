package be.lukaverlaan.ewdj.worldcup.service;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.exception.MatchNotFoundException;
import be.lukaverlaan.ewdj.worldcup.form.MatchForm;
import be.lukaverlaan.ewdj.worldcup.repository.MatchRepository;
import be.lukaverlaan.ewdj.worldcup.repository.PredictionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final PredictionRepository predictionRepository;

    public MatchService(MatchRepository matchRepository, PredictionRepository predictionRepository) {
        this.matchRepository = matchRepository;
        this.predictionRepository = predictionRepository;
    }

    public Match createMatch(MatchForm form) {
        validateNoConflict(form.getCity(), form.getStadium(), form.getDateTime(), null);
        Match match = new Match();
        populateFromForm(match, form);
        return matchRepository.save(match);
    }

    public Match updateMatch(Long id, MatchForm form) {
        Match match = findById(id);
        validateNoConflict(form.getCity(), form.getStadium(), form.getDateTime(), id);
        populateFromForm(match, form);
        return matchRepository.save(match);
    }

    private void populateFromForm(Match match, MatchForm form) {
        match.setTeamA(form.getTeamA());
        match.setTeamB(form.getTeamB());
        match.setDateTime(form.getDateTime());
        match.setCity(form.getCity());
        match.setStadium(form.getStadium());
        match.setStadiumCode(form.getStadiumCode());
        match.setChecksum(form.getChecksum());
    }

    private void validateNoConflict(String city, String stadium, LocalDateTime dateTime, Long excludeId) {
        if (city == null || stadium == null || dateTime == null) return;
        boolean conflict = excludeId == null
            ? matchRepository.existsByCityAndStadiumAndDateTime(city, stadium, dateTime)
            : matchRepository.existsByCityAndStadiumAndDateTimeAndIdNot(city, stadium, dateTime, excludeId);
        if (conflict) {
            throw new IllegalArgumentException("match.conflict");
        }
    }

    @Transactional(readOnly = true)
    public Match findById(Long id) {
        return matchRepository.findById(id)
            .orElseThrow(() -> new MatchNotFoundException("match.notfound"));
    }

    @Transactional(readOnly = true)
    public List<Match> findAll() {
        return matchRepository.findAllByOrderByDateTimeAsc();
    }

    @Transactional(readOnly = true)
    public java.util.Optional<Match> findPrevious(LocalDateTime dateTime) {
        return matchRepository.findFirstByDateTimeBeforeOrderByDateTimeDesc(dateTime);
    }

    @Transactional(readOnly = true)
    public java.util.Optional<Match> findNext(LocalDateTime dateTime) {
        return matchRepository.findFirstByDateTimeAfterOrderByDateTimeAsc(dateTime);
    }

    @Transactional(readOnly = true)
    public Page<Match> findAllPaged(int page, int size) {
        return matchRepository.findAllByOrderByDateTimeAsc(PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Page<Match> findUpcoming(int page, int size) {
        LocalDateTime now = LocalDateTime.now();
        List<Match> liveMatches = matchRepository.findByLiveStatusNotNullAndOfficialScoreAIsNull();
        List<Match> pendingMatches = matchRepository.findByDateTimeLessThanEqualAndOfficialScoreAIsNullAndLiveStatusIsNull(now);
        Page<Match> upcomingPage = matchRepository.findByDateTimeGreaterThanEqualOrderByDateTimeAsc(
                now, PageRequest.of(page, size));

        if (liveMatches.isEmpty() && pendingMatches.isEmpty() || page > 0) return upcomingPage;

        // Live bovenaan, daarna pending, daarna de rest (zonder duplicaten)
        List<Match> combined = new ArrayList<>(liveMatches);
        pendingMatches.stream()
                .filter(m -> liveMatches.stream().noneMatch(l -> l.getId().equals(m.getId())))
                .forEach(combined::add);
        upcomingPage.getContent().stream()
                .filter(m -> combined.stream().noneMatch(l -> l.getId().equals(m.getId())))
                .forEach(combined::add);

        long total = upcomingPage.getTotalElements() + liveMatches.size() + pendingMatches.size();
        return new PageImpl<>(combined, PageRequest.of(0, size), total);
    }

    @Transactional(readOnly = true)
    public List<Match> findLiveMatches() {
        return matchRepository.findByLiveStatusNotNullAndOfficialScoreAIsNull()
                .stream()
                .filter(m -> m.isLive())
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<Match> findPast(int page, int size) {
        return matchRepository.findByDateTimeLessThanAndOfficialScoreAIsNotNullOrderByDateTimeDesc(
                LocalDateTime.now(), PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public List<Match> findUpcomingByCountry(String country) {
        return matchRepository.findUpcomingByCountry(country, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<Match> findPastByCountry(String country) {
        return matchRepository.findPastByCountry(country, LocalDateTime.now());
    }

    public void deleteMatch(Long id) {
        Match match = findById(id);
        predictionRepository.deleteByMatch(match);
        matchRepository.delete(match);
    }

    @Transactional(readOnly = true)
    public List<Match> findByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        return matchRepository.findByDateTimeBetweenOrderByDateTimeAsc(start, end);
    }

    public Match saveResult(Long id, int scoreA, int scoreB) {
        Match match = findById(id);
        match.setOfficialScoreA(scoreA);
        match.setOfficialScoreB(scoreB);
        return matchRepository.save(match);
    }

    public MatchForm toForm(Match match) {
        MatchForm form = new MatchForm();
        form.setTeamA(match.getTeamA());
        form.setTeamB(match.getTeamB());
        form.setDateTime(match.getDateTime());
        form.setCity(match.getCity());
        form.setStadium(match.getStadium());
        form.setStadiumCode(match.getStadiumCode());
        form.setChecksum(match.getChecksum());
        return form;
    }
}

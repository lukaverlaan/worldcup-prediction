package be.lukaverlaan.ewdj.worldcup.repository;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findAllByOrderByDateTimeAsc();
    Page<Match> findAllByOrderByDateTimeAsc(Pageable pageable);
    Page<Match> findByDateTimeGreaterThanEqualOrderByDateTimeAsc(LocalDateTime from, Pageable pageable);
    Page<Match> findByDateTimeLessThanOrderByDateTimeDesc(LocalDateTime before, Pageable pageable);
    List<Match> findByDateTimeBetweenOrderByDateTimeAsc(LocalDateTime start, LocalDateTime end);
    boolean existsByCityAndStadiumAndDateTime(String city, String stadium, LocalDateTime dateTime);
    boolean existsByCityAndStadiumAndDateTimeAndIdNot(String city, String stadium, LocalDateTime dateTime, Long id);
    Optional<Match> findByApiFootballFixtureId(Long fixtureId);
    Optional<Match> findFirstByDateTimeBeforeOrderByDateTimeDesc(LocalDateTime dateTime);
    Optional<Match> findFirstByDateTimeAfterOrderByDateTimeAsc(LocalDateTime dateTime);
    List<Match> findByOfficialScoreAIsNullAndDateTimeLessThan(LocalDateTime before);
    List<Match> findByOfficialScoreAIsNullAndApiFootballFixtureIdIsNotNullAndDateTimeLessThanEqual(LocalDateTime before);
    List<Match> findByLiveStatusNotNullAndOfficialScoreAIsNull();
}

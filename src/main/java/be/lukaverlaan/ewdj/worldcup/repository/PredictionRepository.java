package be.lukaverlaan.ewdj.worldcup.repository;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.domain.Prediction;
import be.lukaverlaan.ewdj.worldcup.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    Optional<Prediction> findByUserAndMatch(User user, Match match);
    List<Prediction> findByMatch(Match match);
    void deleteByMatch(Match match);
    List<Prediction> findByUser(User user);

    @Query("SELECT COALESCE(SUM(p.points), 0) FROM Prediction p WHERE p.user = :user AND p.points IS NOT NULL")
    int sumPointsByUser(@Param("user") User user);

    @Query("SELECT p.user.id, COALESCE(SUM(p.points), 0) FROM Prediction p WHERE p.user IN :users AND p.points IS NOT NULL GROUP BY p.user.id")
    List<Object[]> sumPointsByUsers(@Param("users") Collection<User> users);

    default Map<Long, Integer> getPointsMapForUsers(Collection<User> users) {
        return sumPointsByUsers(users).stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> ((Number) row[1]).intValue()
            ));
    }
}

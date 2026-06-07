package be.lukaverlaan.ewdj.worldcup.repository;

import be.lukaverlaan.ewdj.worldcup.domain.TournamentVote;
import be.lukaverlaan.ewdj.worldcup.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TournamentVoteRepository extends JpaRepository<TournamentVote, Long> {
    Optional<TournamentVote> findByUser(User user);
    boolean existsByUser(User user);

    @Query("SELECT v.country, COUNT(v) FROM TournamentVote v GROUP BY v.country ORDER BY COUNT(v) DESC")
    List<Object[]> countByCountry();
}

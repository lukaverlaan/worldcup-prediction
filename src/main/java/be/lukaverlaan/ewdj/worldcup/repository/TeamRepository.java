package be.lukaverlaan.ewdj.worldcup.repository;

import be.lukaverlaan.ewdj.worldcup.domain.Team;
import be.lukaverlaan.ewdj.worldcup.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByInviteCode(String inviteCode);
    boolean existsByName(String name);
    List<Team> findByMembersContains(User user);

    @Query("SELECT DISTINCT t FROM Team t JOIN FETCH t.owner LEFT JOIN FETCH t.members WHERE :user MEMBER OF t.members")
    List<Team> findByMembersContainsWithOwner(@Param("user") User user);

    @Override
    @EntityGraph(attributePaths = {"owner", "members"})
    Optional<Team> findById(Long id);

}

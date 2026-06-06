package be.lukaverlaan.ewdj.worldcup.service;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.domain.Prediction;
import be.lukaverlaan.ewdj.worldcup.domain.Team;
import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.exception.TeamNotFoundException;
import be.lukaverlaan.ewdj.worldcup.form.CreateTeamForm;
import be.lukaverlaan.ewdj.worldcup.repository.MatchRepository;
import be.lukaverlaan.ewdj.worldcup.repository.PredictionRepository;
import be.lukaverlaan.ewdj.worldcup.repository.TeamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final PredictionRepository predictionRepository;
    private final MatchRepository matchRepository;

    public TeamService(TeamRepository teamRepository, PredictionRepository predictionRepository,
                       MatchRepository matchRepository) {
        this.teamRepository = teamRepository;
        this.predictionRepository = predictionRepository;
        this.matchRepository = matchRepository;
    }

    public Team createTeam(CreateTeamForm form, User owner) {
        if (teamRepository.existsByName(form.getName())) {
            throw new IllegalArgumentException("team.name.exists");
        }
        Team team = new Team();
        team.setName(form.getName());
        team.setInviteCode(generateInviteCode());
        team.setOwner(owner);
        team.getMembers().add(owner);
        return teamRepository.save(team);
    }

    public Team joinTeam(String inviteCode, User user) {
        Team team = teamRepository.findByInviteCode(inviteCode)
            .orElseThrow(() -> new TeamNotFoundException("team.invitecode.invalid"));
        if (team.getMembers().contains(user)) {
            throw new IllegalArgumentException("team.already.member");
        }
        team.getMembers().add(user);
        return teamRepository.save(team);
    }

    public String regenerateInviteCode(Long teamId, User requestingUser) {
        Team team = findById(teamId);
        if (!team.getOwner().getId().equals(requestingUser.getId())) {
            throw new SecurityException("team.not.owner");
        }
        String newCode = generateInviteCode();
        team.setInviteCode(newCode);
        teamRepository.save(team);
        return newCode;
    }

    public void removeMember(Long teamId, Long memberId, User requestingUser) {
        Team team = findById(teamId);
        boolean isAdmin = requestingUser.getRoles().contains("ADMIN");
        if (!isAdmin && !team.getOwner().getId().equals(requestingUser.getId())) {
            throw new SecurityException("team.not.owner");
        }
        team.getMembers().removeIf(m -> m.getId().equals(memberId));
        teamRepository.save(team);
    }

    public void deleteTeam(Long teamId) {
        Team team = findById(teamId);
        team.getMembers().clear();
        teamRepository.delete(team);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllTeamsWithScores() {
        List<Team> teams = teamRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Team team : teams) {
            int total = team.getMembers().stream()
                .mapToInt(predictionRepository::sumPointsByUser)
                .sum();
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("team", team);
            entry.put("totalScore", total);
            entry.put("memberCount", team.getMembers().size());
            result.add(entry);
        }
        result.sort((a, b) -> (int) b.get("totalScore") - (int) a.get("totalScore"));
        return result;
    }

    @Transactional(readOnly = true)
    public Team findById(Long id) {
        return teamRepository.findById(id)
            .orElseThrow(() -> new TeamNotFoundException("team.notfound"));
    }

    @Transactional(readOnly = true)
    public List<Team> findTeamsForUser(User user) {
        return teamRepository.findByMembersContainsWithOwner(user);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTop10Teams() {
        List<Team> teams = teamRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Team team : teams) {
            int total = team.getMembers().stream()
                .mapToInt(predictionRepository::sumPointsByUser)
                .sum();
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("team", team);
            entry.put("totalScore", total);
            entry.put("memberCount", team.getMembers().size());
            result.add(entry);
        }
        result.sort((a, b) -> (int) b.get("totalScore") - (int) a.get("totalScore"));
        return result.size() > 10 ? result.subList(0, 10) : result;
    }

    @Transactional(readOnly = true)
    public int getTotalScoreForUser(User user) {
        return predictionRepository.sumPointsByUser(user);
    }

    @Transactional(readOnly = true)
    public Page<Map<String, Object>> getMatchDetailsForTeamPaged(Team team, List<User> sortedMembers, int page, int size, String tab) {
        LocalDateTime now = LocalDateTime.now();
        PageRequest pr = PageRequest.of(page, size);
        Page<Match> matchPage = "past".equals(tab)
                ? matchRepository.findByDateTimeLessThanOrderByDateTimeDesc(now, pr)
                : matchRepository.findByDateTimeGreaterThanEqualOrderByDateTimeAsc(now, pr);
        List<Map<String, Object>> entries = buildMatchDetailEntries(matchPage.getContent(), sortedMembers);
        return new PageImpl<>(entries, pr, matchPage.getTotalElements());
    }

    private List<Map<String, Object>> buildMatchDetailEntries(List<Match> matches, List<User> sortedMembers) {
        Set<Long> memberIds = sortedMembers.stream().map(User::getId).collect(Collectors.toSet());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Match match : matches) {
            Map<Long, Prediction> predByUserId = predictionRepository.findByMatch(match).stream()
                .filter(p -> memberIds.contains(p.getUser().getId()))
                .collect(Collectors.toMap(p -> p.getUser().getId(), p -> p));

            List<Map<String, Object>> rows = new ArrayList<>();
            for (User member : sortedMembers) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("user", member);
                row.put("prediction", predByUserId.get(member.getId()));
                rows.add(row);
            }

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("match", match);
            entry.put("rows", rows);
            result.add(entry);
        }
        return result;
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}

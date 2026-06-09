package be.lukaverlaan.ewdj.worldcup.service;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.repository.MatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GroupStageService {

    private static final Map<String, List<String>> GROUPS = new LinkedHashMap<>();

    static {
        GROUPS.put("A", List.of("Mexico", "South Africa", "South Korea", "Czech Republic"));
        GROUPS.put("B", List.of("Canada", "Bosnia and Herzegovina", "Qatar", "Switzerland"));
        GROUPS.put("C", List.of("Brazil", "Morocco", "Haiti", "Scotland"));
        GROUPS.put("D", List.of("USA", "Paraguay", "Australia", "Turkey"));
        GROUPS.put("E", List.of("Germany", "Curaçao", "Ivory Coast", "Ecuador"));
        GROUPS.put("F", List.of("Netherlands", "Japan", "Sweden", "Tunisia"));
        GROUPS.put("G", List.of("Belgium", "Egypt", "Iran", "New Zealand"));
        GROUPS.put("H", List.of("Spain", "Cape Verde", "Saudi Arabia", "Uruguay"));
        GROUPS.put("I", List.of("France", "Senegal", "Iraq", "Norway"));
        GROUPS.put("J", List.of("Argentina", "Algeria", "Austria", "Jordan"));
        GROUPS.put("K", List.of("Portugal", "DR Congo", "Uzbekistan", "Colombia"));
        GROUPS.put("L", List.of("England", "Croatia", "Ghana", "Panama"));
    }

    private final MatchRepository matchRepository;

    public GroupStageService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Transactional(readOnly = true)
    public List<GroupData> getAllGroups() {
        List<Match> allMatches = matchRepository.findAllByOrderByDateTimeAsc();
        List<GroupData> result = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : GROUPS.entrySet()) {
            String letter = entry.getKey();
            List<String> teams = entry.getValue();
            List<Match> groupMatches = allMatches.stream()
                .filter(m -> teams.contains(m.getTeamA()) && teams.contains(m.getTeamB()))
                .toList();

            // Calculate points per team
            Map<String, Integer> points = new LinkedHashMap<>();
            for (String team : teams) points.put(team, 0);

            for (Match m : groupMatches) {
                if (!m.hasResult()) continue;
                int a = m.getOfficialScoreA(), b = m.getOfficialScoreB();
                if (a > b) {
                    points.merge(m.getTeamA(), 3, Integer::sum);
                } else if (b > a) {
                    points.merge(m.getTeamB(), 3, Integer::sum);
                } else {
                    points.merge(m.getTeamA(), 1, Integer::sum);
                    points.merge(m.getTeamB(), 1, Integer::sum);
                }
            }

            // Sort teams by points descending
            List<String> sortedTeams = teams.stream()
                .sorted((t1, t2) -> points.getOrDefault(t2, 0) - points.getOrDefault(t1, 0))
                .toList();

            // Split sorted matches into matchdays of 2
            List<Matchday> matchdays = new ArrayList<>();
            for (int i = 0; i < groupMatches.size(); i += 2) {
                int end = Math.min(i + 2, groupMatches.size());
                matchdays.add(new Matchday(matchdays.size() + 1, groupMatches.subList(i, end)));
            }

            result.add(new GroupData(letter, sortedTeams, groupMatches, points, matchdays));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<Match> getAllMatchesInGroupOrder() {
        return getAllGroups().stream()
            .flatMap(g -> g.matches().stream())
            .toList();
    }

    public record Matchday(int number, List<Match> matches) {}
    public record GroupData(String letter, List<String> teams, List<Match> matches, Map<String, Integer> points, List<Matchday> matchdays) {}
}

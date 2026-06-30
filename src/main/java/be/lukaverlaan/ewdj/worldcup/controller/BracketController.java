package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.repository.MatchRepository;
import be.lukaverlaan.ewdj.worldcup.service.GroupStageService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class BracketController {

    private static final List<String> KNOCKOUT_ROUNDS = List.of(
        "Round of 32", "Round of 16", "Quarter-finals", "Semi-finals", "3rd Place Final", "Final"
    );

    private static final Map<String, Integer> EXPECTED_SLOTS = Map.of(
        "Round of 32", 16,
        "Round of 16", 8,
        "Quarter-finals", 4,
        "Semi-finals", 2,
        "3rd Place Final", 1,
        "Final", 1
    );

    private static final Map<String, String> CODE_MAP = Map.ofEntries(
        Map.entry("Mexico", "MEX"), Map.entry("South Africa", "RSA"),
        Map.entry("South Korea", "KOR"), Map.entry("Czech Republic", "CZE"),
        Map.entry("Canada", "CAN"), Map.entry("Bosnia and Herzegovina", "BIH"),
        Map.entry("Qatar", "QAT"), Map.entry("Switzerland", "SUI"),
        Map.entry("Brazil", "BRA"), Map.entry("Morocco", "MAR"),
        Map.entry("Haiti", "HAI"), Map.entry("Scotland", "SCO"),
        Map.entry("Australia", "AUS"), Map.entry("Turkey", "TUR"),
        Map.entry("Germany", "GER"), Map.entry("Curaçao", "CUR"),
        Map.entry("Ivory Coast", "CIV"), Map.entry("Ecuador", "ECU"),
        Map.entry("Netherlands", "NED"), Map.entry("Japan", "JPN"),
        Map.entry("Sweden", "SWE"), Map.entry("Tunisia", "TUN"),
        Map.entry("Belgium", "BEL"), Map.entry("Egypt", "EGY"),
        Map.entry("Iran", "IRN"), Map.entry("New Zealand", "NZL"),
        Map.entry("Spain", "ESP"), Map.entry("Cape Verde", "CPV"),
        Map.entry("Saudi Arabia", "KSA"), Map.entry("Uruguay", "URU"),
        Map.entry("France", "FRA"), Map.entry("Senegal", "SEN"),
        Map.entry("Iraq", "IRQ"), Map.entry("Norway", "NOR"),
        Map.entry("Argentina", "ARG"), Map.entry("Algeria", "ALG"),
        Map.entry("Austria", "AUT"), Map.entry("Jordan", "JOR"),
        Map.entry("Portugal", "POR"), Map.entry("DR Congo", "COD"),
        Map.entry("Uzbekistan", "UZB"), Map.entry("Colombia", "COL"),
        Map.entry("England", "ENG"), Map.entry("Croatia", "CRO"),
        Map.entry("Ghana", "GHA"), Map.entry("Panama", "PAN"),
        Map.entry("USA", "USA"), Map.entry("Paraguay", "PAR")
    );

    // Maps country name → badge filename (without .png)
    private static final Map<String, String> BADGE_MAP = Map.ofEntries(
        Map.entry("Mexico", "mexico"), Map.entry("South Africa", "south-africa"),
        Map.entry("South Korea", "south-korea"), Map.entry("Czech Republic", "czech-republic"),
        Map.entry("Canada", "canada"), Map.entry("Bosnia and Herzegovina", "bosnia"),
        Map.entry("Qatar", "qatar"), Map.entry("Switzerland", "switzerland"),
        Map.entry("Brazil", "brazil"), Map.entry("Morocco", "morocco"),
        Map.entry("Haiti", "haiti"), Map.entry("Scotland", "scotland"),
        Map.entry("Australia", "australia"), Map.entry("Turkey", "turkey"),
        Map.entry("Germany", "germany"), Map.entry("Curaçao", "curacao"),
        Map.entry("Ivory Coast", "ivory-coast"), Map.entry("Ecuador", "ecuador"),
        Map.entry("Netherlands", "netherlands"), Map.entry("Japan", "japan"),
        Map.entry("Sweden", "sweden"), Map.entry("Tunisia", "tunisia"),
        Map.entry("Belgium", "belgium"), Map.entry("Egypt", "egypt"),
        Map.entry("Iran", "iran"), Map.entry("New Zealand", "new-zealand"),
        Map.entry("Spain", "spain"), Map.entry("Cape Verde", "cape-verde"),
        Map.entry("Saudi Arabia", "saudi-arabia"), Map.entry("Uruguay", "uruguay"),
        Map.entry("France", "france"), Map.entry("Senegal", "senegal"),
        Map.entry("Iraq", "iraq"), Map.entry("Norway", "norway"),
        Map.entry("Argentina", "argentina"), Map.entry("Algeria", "algeria"),
        Map.entry("Austria", "austria"), Map.entry("Jordan", "jordan"),
        Map.entry("Portugal", "portugal"), Map.entry("DR Congo", "dr-congo"),
        Map.entry("Uzbekistan", "uzbekistan"), Map.entry("Colombia", "colombia"),
        Map.entry("England", "england"), Map.entry("Croatia", "croatia"),
        Map.entry("Ghana", "ghana"), Map.entry("Panama", "panama"),
        Map.entry("USA", "united-states"), Map.entry("Paraguay", "paraguay")
    );

    private static final List<String> GROUP_COLORS = List.of(
        "#27ae60", "#e74c3c", "#f39c12", "#2980b9", "#8e44ad", "#16a085",
        "#d35400", "#c0392b", "#1abc9c", "#2c3e50", "#e67e22", "#27ae60"
    );

    private final MatchRepository matchRepository;
    private final GroupStageService groupStageService;
    private final MessageSource messageSource;

    public BracketController(MatchRepository matchRepository, GroupStageService groupStageService, MessageSource messageSource) {
        this.matchRepository = matchRepository;
        this.groupStageService = groupStageService;
        this.messageSource = messageSource;
    }

    @GetMapping("/bracket")
    public String bracket(Model model) {
        List<Match> all = matchRepository.findAllByOrderByDateTimeAsc();

        // Build knockout bracket
        Map<String, List<BracketMatch>> bracketRounds = new LinkedHashMap<>();
        Map<String, List<Match>> byRound = new LinkedHashMap<>();
        for (String r : KNOCKOUT_ROUNDS) byRound.put(r, new ArrayList<>());
        for (Match m : all) {
            String r = m.getRound();
            if (r == null || r.startsWith("Group Stage")) continue;
            byRound.computeIfPresent(r, (k, v) -> { v.add(m); return v; });
        }
        // Sort each knockout round by api-football fixture ID (assigned in official bracket order)
        byRound.values().forEach(list -> list.sort(Comparator.comparing(
            m -> m.getApiFootballFixtureId() != null ? m.getApiFootballFixtureId() : Long.MAX_VALUE)));
        for (String round : KNOCKOUT_ROUNDS) {
            List<Match> matches = byRound.get(round);
            int slots = EXPECTED_SLOTS.get(round);
            List<BracketMatch> list = new ArrayList<>();
            for (int i = 0; i < slots; i++) {
                if (i < matches.size()) {
                    Match m = matches.get(i);
                    list.add(new BracketMatch(
                        m.getTeamA(), m.getTeamB(),
                        badge(m.getTeamA()), badge(m.getTeamB()),
                        code(m.getTeamA()), code(m.getTeamB()),
                        m.getOfficialScoreA(), m.getOfficialScoreB(), m.hasResult()));
                } else {
                    list.add(new BracketMatch("?", "?", null, null, "?", "?", null, null, false));
                }
            }
            bracketRounds.put(round, list);
        }

        // Build group cards
        List<GroupStageService.GroupData> groups = groupStageService.getAllGroups();
        List<GroupCard> groupCards = new ArrayList<>();
        int colorIdx = 0;
        for (GroupStageService.GroupData g : groups) {
            List<TeamBadge> badges = g.teams().stream()
                .map(t -> new TeamBadge(t, badge(t), code(t), localName(t)))
                .toList();
            groupCards.add(new GroupCard(g.letter(), badges, GROUP_COLORS.get(colorIdx % GROUP_COLORS.size())));
            colorIdx++;
        }

        // Auto-refresh when there are live or soon-starting knockout matches
        boolean hasActiveKnockout = all.stream()
            .filter(m -> m.getRound() != null && !m.getRound().startsWith("Group Stage"))
            .anyMatch(m -> !m.hasResult() && m.getDateTime() != null
                && m.getDateTime().isBefore(LocalDateTime.now().plusMinutes(120)));

        model.addAttribute("bracketRounds", bracketRounds);
        model.addAttribute("groupCards", groupCards);
        model.addAttribute("autoRefresh", hasActiveKnockout);
        return "bracket";
    }

    private String badge(String team) { return BADGE_MAP.get(team); }
    private String code(String team) { return CODE_MAP.getOrDefault(team, team); }
    private String localName(String team) {
        return messageSource.getMessage("country." + team,  null, team, LocaleContextHolder.getLocale());
    }

    public record BracketMatch(String teamA, String teamB, String badgeA, String badgeB,
                                String codeA, String codeB,
                                Integer scoreA, Integer scoreB, boolean played) {}
    public record TeamBadge(String name, String badge, String code, String localName) {}
    public record GroupCard(String letter, List<TeamBadge> teams, String color) {}
}

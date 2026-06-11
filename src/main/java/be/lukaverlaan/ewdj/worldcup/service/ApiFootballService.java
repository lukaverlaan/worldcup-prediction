package be.lukaverlaan.ewdj.worldcup.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ApiFootballService {

    // api-football.com returns different spellings than our app uses
    private static final Map<String, String> NAME_MAP = Map.ofEntries(
        Map.entry("United States", "USA"),
        Map.entry("Côte d'Ivoire", "Ivory Coast"),
        Map.entry("Korea Republic", "South Korea"),
        Map.entry("Bosnia", "Bosnia and Herzegovina"),
        Map.entry("Bosnia & Herzegovina", "Bosnia and Herzegovina"),
        Map.entry("Congo DR", "DR Congo"),
        Map.entry("Curaçao", "Curaçao"),
        Map.entry("Türkiye", "Turkey"),
        Map.entry("Cape Verde Islands", "Cape Verde")
    );

    private final WebClient client;

    @Value("${apifootball.league:1}")
    private int leagueId;

    @Value("${apifootball.season:2026}")
    private int season;

    public ApiFootballService(@Qualifier("apiFootballWebClient") WebClient client) {
        this.client = client;
    }

    public List<FixtureData> fetchAllFixtures() {
        log.info("Fetching all fixtures from api-football (league={}, season={})", leagueId, season);
        return client.get()
            .uri(b -> b.path("/fixtures")
                .queryParam("league", leagueId)
                .queryParam("season", season)
                .build())
            .retrieve()
            .bodyToMono(Map.class)
            .map(this::parseFixtures)
            .doOnError(e -> log.error("Failed to fetch fixtures: {}", e.getMessage()))
            .onErrorResume(e -> Mono.just(List.of()))
            .block();
    }

    public FixtureData fetchFixture(long fixtureId) {
        return client.get()
            .uri(b -> b.path("/fixtures")
                .queryParam("id", fixtureId)
                .build())
            .retrieve()
            .bodyToMono(Map.class)
            .map(body -> {
                List<FixtureData> list = parseFixtures(body);
                return list.isEmpty() ? null : list.get(0);
            })
            .doOnError(e -> log.error("Failed to fetch fixture {}: {}", fixtureId, e.getMessage()))
            .onErrorResume(e -> Mono.empty())
            .block();
    }

    @SuppressWarnings("unchecked")
    private List<FixtureData> parseFixtures(Map<?, ?> body) {
        Object responseObj = body.get("response");
        if (!(responseObj instanceof List<?> items)) return List.of();

        return items.stream()
            .map(item -> {
                try {
                    Map<String, Object> entry = (Map<String, Object>) item;
                    Map<String, Object> fixture = (Map<String, Object>) entry.get("fixture");
                    Map<String, Object> teams = (Map<String, Object>) entry.get("teams");
                    Map<String, Object> goals = (Map<String, Object>) entry.get("goals");
                    Map<String, Object> venue = (Map<String, Object>) fixture.get("venue");
                    Map<String, Object> status = (Map<String, Object>) fixture.get("status");

                    long fixtureId = ((Number) fixture.get("id")).longValue();
                    String dateStr = (String) fixture.get("date");
                    LocalDateTime dateTime = OffsetDateTime.parse(dateStr)
                        .atZoneSameInstant(ZoneId.of("Europe/Brussels"))
                        .toLocalDateTime();

                    String homeTeam = normalize((String) ((Map<?, ?>) teams.get("home")).get("name"));
                    String awayTeam = normalize((String) ((Map<?, ?>) teams.get("away")).get("name"));

                    String city = venue != null ? (String) venue.get("city") : null;
                    String stadiumName = venue != null ? (String) venue.get("name") : null;

                    String statusShort = status != null ? (String) status.get("short") : "";
                    Integer elapsed = (status != null && status.get("elapsed") instanceof Number n) ? n.intValue() : null;
                    boolean finished = "FT".equals(statusShort) || "AET".equals(statusShort) || "PEN".equals(statusShort);

                    Integer scoreHome = goals != null && goals.get("home") instanceof Number n ? n.intValue() : null;
                    Integer scoreAway = goals != null && goals.get("away") instanceof Number n ? n.intValue() : null;

                    Map<String, Object> league = (Map<String, Object>) entry.get("league");
                    String round = league != null ? (String) league.get("round") : null;

                    return new FixtureData(fixtureId, homeTeam, awayTeam, dateTime, city, stadiumName, finished, scoreHome, scoreAway, round, statusShort, elapsed);
                } catch (Exception e) {
                    log.warn("Could not parse fixture entry: {}", e.getMessage());
                    return null;
                }
            })
            .filter(f -> f != null)
            .toList();
    }

    private String normalize(String name) {
        return NAME_MAP.getOrDefault(name, name);
    }

    public record FixtureData(
        long fixtureId,
        String homeTeam,
        String awayTeam,
        LocalDateTime dateTime,
        String city,
        String stadium,
        boolean finished,
        Integer scoreHome,
        Integer scoreAway,
        String round,
        String statusShort,
        Integer elapsed
    ) {}
}

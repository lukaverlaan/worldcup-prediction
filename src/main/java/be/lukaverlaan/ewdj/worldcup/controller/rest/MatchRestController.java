package be.lukaverlaan.ewdj.worldcup.controller.rest;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.service.MatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/matches")
public class MatchRestController {

    private final MatchService matchService;
    private final MessageSource messageSource;

    public MatchRestController(MatchService matchService, MessageSource messageSource) {
        this.matchService = matchService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public List<Map<String, Object>> getMatches(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("GET /api/matches, date: {}", date);
        List<Match> matches = (date != null) ? matchService.findByDate(date) : matchService.findAll();
        return matches.stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Map<String, Object> getMatch(@PathVariable Long id) {
        log.info("GET /api/matches/{}", id);
        return toDto(matchService.findById(id));
    }

    @GetMapping("/{id}/live")
    public Map<String, Object> getLiveData(@PathVariable Long id, Locale locale) {
        Match m = matchService.findById(id);
        Map<String, Object> dto = new java.util.LinkedHashMap<>();
        dto.put("id", m.getId());
        dto.put("hasResult", m.hasResult());
        dto.put("officialScoreA", m.getOfficialScoreA());
        dto.put("officialScoreB", m.getOfficialScoreB());
        dto.put("isLive", m.isLive());
        dto.put("liveScoreA", m.getLiveScoreA());
        dto.put("liveScoreB", m.getLiveScoreB());
        dto.put("liveMinute", m.getLiveMinute());
        dto.put("liveExtra", m.getLiveExtra());
        dto.put("liveStatus", m.getLiveStatus());
        if (m.getLiveStatus() != null) {
            String key = "match.status." + m.getLiveStatus();
            dto.put("liveStatusLabel", messageSource.getMessage(key, null, m.getLiveStatus(), locale));
        }
        return dto;
    }

    private Map<String, Object> toDto(Match m) {
        Map<String, Object> dto = new java.util.LinkedHashMap<>();
        dto.put("id", m.getId());
        dto.put("teamA", m.getTeamA());
        dto.put("teamB", m.getTeamB());
        dto.put("dateTime", m.getDateTime().toString());
        dto.put("city", m.getCity() != null ? m.getCity() : "");
        dto.put("stadium", m.getStadium() != null ? m.getStadium() : "");
        dto.put("hasResult", m.hasResult());
        return dto;
    }
}

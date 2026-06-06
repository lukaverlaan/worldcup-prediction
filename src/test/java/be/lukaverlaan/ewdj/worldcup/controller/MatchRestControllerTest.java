package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.controller.rest.MatchRestController;
import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.exception.MatchNotFoundException;
import be.lukaverlaan.ewdj.worldcup.exception.RestExceptionHandler;
import be.lukaverlaan.ewdj.worldcup.service.MatchService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    value = MatchRestController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
@Import(RestExceptionHandler.class)
class MatchRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MatchService matchService;

    @Test
    void getAllMatchesReturnsJsonList() throws Exception {
        Match m = new Match();
        m.setTeamA("France");
        m.setTeamB("Brazil");
        m.setDateTime(LocalDateTime.of(2026, 6, 14, 21, 0));
        m.setCity("New York");
        m.setStadium("MetLife Stadium");
        Mockito.when(matchService.findAll()).thenReturn(List.of(m));

        mockMvc.perform(get("/api/matches"))
               .andExpect(status().isOk())
               .andExpect(content().contentType("application/json"))
               .andExpect(jsonPath("$[0].teamA").value("France"))
               .andExpect(jsonPath("$[0].teamB").value("Brazil"))
               .andExpect(jsonPath("$[0].city").value("New York"));

        Mockito.verify(matchService).findAll();
    }

    @Test
    void getMatchesByDateReturnsFilteredMatches() throws Exception {
        Match m = new Match();
        m.setTeamA("Spain");
        m.setTeamB("Portugal");
        m.setDateTime(LocalDateTime.of(2026, 6, 20, 21, 0));
        m.setCity("Miami");
        m.setStadium("Hard Rock Stadium");
        Mockito.when(matchService.findByDate(LocalDate.of(2026, 6, 20))).thenReturn(List.of(m));

        mockMvc.perform(get("/api/matches?date=2026-06-20"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].teamA").value("Spain"))
               .andExpect(jsonPath("$[0].teamB").value("Portugal"));

        Mockito.verify(matchService).findByDate(LocalDate.of(2026, 6, 20));
    }

    @Test
    void getMatchesByDateReturnsEmptyListWhenNoMatches() throws Exception {
        Mockito.when(matchService.findByDate(any(LocalDate.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/matches?date=2026-07-01"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isEmpty());

        Mockito.verify(matchService).findByDate(LocalDate.of(2026, 7, 1));
    }

    @Test
    void getAllMatchesWithNullValuesHandledGracefully() throws Exception {
        Match m = new Match();
        m.setTeamA("Germany");
        m.setTeamB("Argentina");
        m.setDateTime(LocalDateTime.of(2026, 6, 16, 18, 0));
        Mockito.when(matchService.findAll()).thenReturn(List.of(m));

        mockMvc.perform(get("/api/matches"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].teamA").value("Germany"));

        Mockito.verify(matchService).findAll();
    }

    @Test
    void getMatchByIdNotFoundReturnsErrorResponse() throws Exception {
        Mockito.when(matchService.findById(9999L)).thenThrow(new MatchNotFoundException("match.notfound"));

        mockMvc.perform(get("/api/matches/9999"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.status").value(404))
               .andExpect(jsonPath("$.message").exists())
               .andExpect(jsonPath("$.timestamp").exists());

        Mockito.verify(matchService).findById(9999L);
    }
}

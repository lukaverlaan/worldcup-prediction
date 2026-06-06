package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.config.SecurityConfig;
import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.service.CountryRegistry;
import be.lukaverlaan.ewdj.worldcup.service.MatchService;
import be.lukaverlaan.ewdj.worldcup.service.PredictionService;
import be.lukaverlaan.ewdj.worldcup.service.TeamService;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MatchService matchService;

    @MockitoBean
    private PredictionService predictionService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CountryRegistry countryRegistry;

    @MockitoBean
    private TeamService teamService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminMatchListAccessibleForAdmin() throws Exception {
        when(matchService.findAllPaged(anyInt(), anyInt())).thenReturn(Page.empty());
        when(countryRegistry.getAllSorted()).thenReturn(List.of());
        mockMvc.perform(get("/admin/matches"))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/matches"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminMatchListForbiddenForUser() throws Exception {
        mockMvc.perform(get("/admin/matches"))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void newMatchFormRendered() throws Exception { // formulier laadt correct, leeg matchForm
        mockMvc.perform(get("/admin/matches/new"))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/match-form"))
               .andExpect(model().attributeExists("matchForm"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMatchWithValidDataRedirects() throws Exception {
        Match saved = new Match();
        saved.setTeamA("France");
        saved.setTeamB("Germany");
        saved.setDateTime(LocalDateTime.of(2026, 6, 15, 18, 0));
        when(matchService.createMatch(any())).thenReturn(saved);

        mockMvc.perform(post("/admin/matches/new").with(csrf())
               .param("teamA", "France")
               .param("teamB", "Germany")
               .param("dateTime", "2026-06-15T18:00")
               .param("city", "Paris")
               .param("stadium", "Stade de France")
               .param("stadiumCode", "1234")
               .param("checksum", "70"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/admin/matches"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMatchWithSameTeamsShowsValidationError() throws Exception {
        mockMvc.perform(post("/admin/matches/new").with(csrf())
               .param("teamA", "France")
               .param("teamB", "France")
               .param("dateTime", "2026-06-15T18:00")
               .param("stadiumCode", "1234")
               .param("checksum", "70"))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/match-form"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMatchWithWrongChecksumShowsValidationError() throws Exception {
        mockMvc.perform(post("/admin/matches/new").with(csrf())
               .param("teamA", "France")
               .param("teamB", "Germany")
               .param("dateTime", "2026-06-15T18:00")
               .param("stadiumCode", "1234")
               .param("checksum", "99"))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/match-form"));
    }
}

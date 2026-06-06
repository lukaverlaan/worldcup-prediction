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
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class SecurityTest {

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
    @WithAnonymousUser
    void unauthenticatedUserCannotAccessAdminPage() throws Exception {
        int status = mockMvc.perform(get("/admin/matches"))
               .andReturn().getResponse().getStatus();
        org.assertj.core.api.Assertions.assertThat(status)
               .as("Anonymous user must not get 200 on admin page")
               .isNotEqualTo(200);
    }

    @Test
    @WithMockUser(roles = "USER")
    void userWithUserRoleIsForbiddenFromAdminPage() throws Exception {
        mockMvc.perform(get("/admin/matches"))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanAccessAdminMatchList() throws Exception {
        when(matchService.findAllPaged(anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of()));
        when(countryRegistry.getAllSorted()).thenReturn(List.of());
        mockMvc.perform(get("/admin/matches"))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/matches"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanAccessNewMatchForm() throws Exception {
        mockMvc.perform(get("/admin/matches/new"))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/match-form"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void userWithUserRoleIsForbiddenFromNewMatchForm() throws Exception {
        mockMvc.perform(get("/admin/matches/new"))
               .andExpect(status().isForbidden());
    }
}

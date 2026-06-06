package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.service.CountryRegistry;
import be.lukaverlaan.ewdj.worldcup.service.MatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    value = HomeController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
@Import(CountryRegistry.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MatchService matchService;

    @Test
    void homePageReturnsIndexView() throws Exception {
        when(matchService.findUpcoming(anyInt(), anyInt())).thenReturn(Page.empty());
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(view().name("index"))
               .andExpect(model().attributeExists("matches"));
    }

    @Test
    void homePagePassesMatchListToModel() throws Exception {
        Match m = new Match();
        m.setTeamA("France");
        m.setTeamB("Brazil");
        m.setDateTime(LocalDateTime.of(2026, 6, 14, 21, 0));
        when(matchService.findUpcoming(anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of(m)));

        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(model().attribute("matches", List.of(m)));
    }

    @Test
    void accessDeniedPageReturnsCorrectView() throws Exception {
        mockMvc.perform(get("/access-denied"))
               .andExpect(status().isOk())
               .andExpect(view().name("error/access-denied"));
    }
}

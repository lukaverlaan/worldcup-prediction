package be.lukaverlaan.ewdj.worldcup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${apifootball.key:}")
    private String apiFootballKey;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:" + serverPort)
                .build();
    }

    @Bean("apiFootballWebClient")
    public WebClient apiFootballWebClient() {
        return WebClient.builder()
                .baseUrl("https://v3.football.api-sports.io")
                .defaultHeader("x-apisports-key", apiFootballKey)
                .build();
    }
}

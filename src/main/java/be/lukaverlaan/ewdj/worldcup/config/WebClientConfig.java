package be.lukaverlaan.ewdj.worldcup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${server.port:8080}")
    private int serverPort;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:" + serverPort)
                .build();
    }
}

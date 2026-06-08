package be.lukaverlaan.ewdj.worldcup.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;

@Slf4j
@Service
public class WebClientService {

    private final WebClient webClient;

    public WebClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Map fetchStadiumCapacity(String stadiumCode) {
        return webClient.get()
            .uri("/api/stadiums/{code}/capacity", stadiumCode)
            .retrieve()
            .bodyToMono(Map.class)
            .doOnNext(m -> log.info("Fetched stadium capacity: {}", m))
            .doOnError(e -> log.error("Error fetching stadium capacity: {}", e.getMessage()))
            .onErrorResume(e -> Mono.just(Map.of("capacity", "N/A")))
            .block();
    }
}

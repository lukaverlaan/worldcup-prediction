package be.lukaverlaan.ewdj.worldcup.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WebClientService {

    private final WebClient webClient;

    public WebClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<Map> fetchMatchesByDate(LocalDate date) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/matches")
                .queryParam("date", date.toString())
                .build())
            .retrieve()
            .bodyToFlux(Map.class)
            .doOnNext(m -> log.info("Fetched match: {}", m))
            .doOnError(e -> log.error("Error fetching matches: {}", e.getMessage()))
            .onErrorResume(e -> Mono.empty())
            .collectList()
            .block();
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

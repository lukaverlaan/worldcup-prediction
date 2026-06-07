package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.TournamentVote;
import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.repository.TournamentVoteRepository;
import be.lukaverlaan.ewdj.worldcup.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/vote")
public class VoteController {

    private final TournamentVoteRepository voteRepository;
    private final UserRepository userRepository;

    public VoteController(TournamentVoteRepository voteRepository, UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(Map.of("loggedIn", false, "voted", false));
        }
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(Map.of("loggedIn", false, "voted", false));
        }
        boolean voted = voteRepository.existsByUser(user);
        return ResponseEntity.ok(Map.of("loggedIn", true, "voted", voted));
    }

    @PostMapping("/winner")
    public ResponseEntity<Map<String, String>> vote(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not found"));
        }
        if (voteRepository.existsByUser(user)) {
            return ResponseEntity.status(409).body(Map.of("error", "Already voted"));
        }
        String country = body.get("country");
        if (country == null || country.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No country provided"));
        }
        voteRepository.save(new TournamentVote(user, country));
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}

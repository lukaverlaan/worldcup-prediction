package be.lukaverlaan.ewdj.worldcup.controller;

import be.lukaverlaan.ewdj.worldcup.domain.TournamentVote;
import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.repository.TournamentVoteRepository;
import be.lukaverlaan.ewdj.worldcup.service.CountryEntry;
import be.lukaverlaan.ewdj.worldcup.service.CountryRegistry;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/vote/tournament")
public class TournamentVoteController {

    static final List<String> TOP_SCORERS = List.of(
        "Kylian Mbappé (France)",
        "Harry Kane (England)",
        "Vinicius Jr (Brazil)",
        "Lautaro Martínez (Argentina)",
        "Lionel Messi (Argentina)",
        "Julián Álvarez (Argentina)",
        "Cristiano Ronaldo (Portugal)",
        "Bukayo Saka (England)",
        "Phil Foden (England)",
        "Jude Bellingham (England)",
        "Antoine Griezmann (France)",
        "Randal Kolo Muani (France)",
        "Ousmane Dembélé (France)",
        "Bradley Barcola (France)",
        "Álvaro Morata (Spain)",
        "Dani Olmo (Spain)",
        "Mikel Oyarzabal (Spain)",
        "Pedri (Spain)",
        "Ferran Torres (Spain)",
        "Jamal Musiala (Germany)",
        "Florian Wirtz (Germany)",
        "Kai Havertz (Germany)",
        "Leroy Sané (Germany)",
        "Niclas Füllkrug (Germany)",
        "Robert Lewandowski (Poland)",
        "Rodrygo (Brazil)",
        "Richarlison (Brazil)",
        "Gabriel Jesus (Brazil)",
        "Endrick (Brazil)",
        "Darwin Núñez (Uruguay)",
        "Dusan Vlahovic (Serbia)",
        "Aleksandar Mitrovic (Serbia)",
        "Memphis Depay (Netherlands)",
        "Cody Gakpo (Netherlands)",
        "Xavi Simons (Netherlands)",
        "Wout Weghorst (Netherlands)",
        "Son Heung-min (South Korea)",
        "Romelu Lukaku (Belgium)",
        "Lois Openda (Belgium)",
        "Paulo Dybala (Argentina)",
        "Rafael Leão (Portugal)",
        "Diogo Jota (Portugal)",
        "Bernardo Silva (Portugal)",
        "Youssef En-Nesyri (Morocco)",
        "Hakim Ziyech (Morocco)",
        "Sadio Mané (Senegal)",
        "Hirving Lozano (Mexico)",
        "Raúl Jiménez (Mexico)",
        "Christian Pulisic (USA)",
        "Ciro Immobile (Italy)"
    );

    private final TournamentVoteRepository voteRepository;
    private final UserService userService;
    private final CountryRegistry countryRegistry;

    public TournamentVoteController(TournamentVoteRepository voteRepository,
                                    UserService userService,
                                    CountryRegistry countryRegistry) {
        this.voteRepository = voteRepository;
        this.userService = userService;
        this.countryRegistry = countryRegistry;
    }

    @GetMapping
    public String page(Model model, Authentication auth) {
        model.addAttribute("scorers", TOP_SCORERS);
        model.addAttribute("countries", countryRegistry.getAllSorted());

        if (auth != null && auth.isAuthenticated()) {
            User user = userService.findByUsername(auth.getName());
            voteRepository.findByUser(user).ifPresent(v -> model.addAttribute("vote", v));
        }
        return "vote/tournament";
    }

    @PostMapping("/country")
    public String submitCountry(@RequestParam(required = false) String country,
                                Authentication auth,
                                RedirectAttributes ra) {

        if (auth == null || !auth.isAuthenticated()) return "redirect:/login";

        if (country == null || country.isBlank()) {
            ra.addFlashAttribute("error", "vote.error.country");
            return "redirect:/vote/tournament";
        }

        User user = userService.findByUsername(auth.getName());
        TournamentVote vote = voteRepository.findByUser(user).orElseGet(() -> new TournamentVote(user, country));

        if (vote.getCountry() == null || vote.getCountry().isBlank()) {
            vote.setCountry(country);
        }

        voteRepository.save(vote);
        ra.addFlashAttribute("success", "vote.saved.country");
        return "redirect:/vote/tournament";
    }

    @PostMapping("/scorers")
    public String submitScorers(@RequestParam(required = false) String scorer1,
                                @RequestParam(required = false) String scorer2,
                                @RequestParam(required = false) String scorer3,
                                @RequestParam(required = false) String scorer4,
                                @RequestParam(required = false) String scorer5,
                                Authentication auth,
                                RedirectAttributes ra) {

        if (auth == null || !auth.isAuthenticated()) return "redirect:/login";

        User user = userService.findByUsername(auth.getName());
        TournamentVote vote = voteRepository.findByUser(user).orElseGet(() -> new TournamentVote(user));

        if (vote.hasScorers()) return "redirect:/vote/tournament";

        if (scorer1 == null || scorer2 == null || scorer3 == null || scorer4 == null || scorer5 == null
                || scorer1.isBlank() || scorer2.isBlank() || scorer3.isBlank() || scorer4.isBlank() || scorer5.isBlank()) {
            ra.addFlashAttribute("error", "vote.error.scorers");
            return "redirect:/vote/tournament";
        }

        vote.setScorer1(scorer1);
        vote.setScorer2(scorer2);
        vote.setScorer3(scorer3);
        vote.setScorer4(scorer4);
        vote.setScorer5(scorer5);

        voteRepository.save(vote);
        ra.addFlashAttribute("success", "vote.saved.scorers");
        return "redirect:/vote/tournament";
    }
}

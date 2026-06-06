package be.lukaverlaan.ewdj.worldcup.service;

import be.lukaverlaan.ewdj.worldcup.domain.Match;
import be.lukaverlaan.ewdj.worldcup.domain.Prediction;
import be.lukaverlaan.ewdj.worldcup.domain.Team;
import be.lukaverlaan.ewdj.worldcup.domain.User;
import be.lukaverlaan.ewdj.worldcup.form.PredictionForm;
import be.lukaverlaan.ewdj.worldcup.repository.PredictionRepository;
import be.lukaverlaan.ewdj.worldcup.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final TeamRepository teamRepository;

    @Value("${score.exact:3}")
    private int scoreExact;

    @Value("${score.winner:1}")
    private int scoreWinner;

    @Value("${score.bonus.exact:2}")
    private int scoreBonusExact;

    @Value("${score.bonus.winner:1}")
    private int scoreBonusWinner;

    public PredictionService(PredictionRepository predictionRepository, TeamRepository teamRepository) {
        this.predictionRepository = predictionRepository;
        this.teamRepository = teamRepository;
    }

    public Prediction savePrediction(User user, Match match, PredictionForm form) {
        if (match.getDateTime().minusHours(1).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("prediction.closed");
        }
        Optional<Prediction> existing = predictionRepository.findByUserAndMatch(user, match);
        Prediction prediction = existing.orElseGet(() -> new Prediction(user, match, 0, 0));
        prediction.setPredictedScoreA(form.getPredictedScoreA());
        prediction.setPredictedScoreB(form.getPredictedScoreB());
        prediction.setPoints(null);
        return predictionRepository.save(prediction);
    }

    public void calculatePointsForMatch(Match match) {
        List<Prediction> predictions = predictionRepository.findByMatch(match);
        int officialA = match.getOfficialScoreA();
        int officialB = match.getOfficialScoreB();
        String officialWinner = getWinner(officialA, officialB);

        for (Prediction p : predictions) {
            int points = 0;
            boolean exactCorrect = p.getPredictedScoreA() == officialA && p.getPredictedScoreB() == officialB;
            boolean winnerCorrect = getWinner(p.getPredictedScoreA(), p.getPredictedScoreB()).equals(officialWinner);

            if (exactCorrect) {
                points += scoreExact;
                if (isUniqueExactInAnyTeam(p, predictions)) {
                    points += scoreBonusExact;
                }
            } else if (winnerCorrect) {
                points += scoreWinner;
                if (isUniqueWinnerInAnyTeam(p, predictions, officialWinner)) {
                    points += scoreBonusWinner;
                }
            }
            p.setPoints(points);
            predictionRepository.save(p);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Prediction> findByUserAndMatch(User user, Match match) {
        return predictionRepository.findByUserAndMatch(user, match);
    }

    @Transactional(readOnly = true)
    public List<Prediction> findByUser(User user) {
        return predictionRepository.findByUser(user);
    }

    private boolean isUniqueExactInAnyTeam(Prediction target, List<Prediction> all) {
        List<Team> userTeams = teamRepository.findByMembersContains(target.getUser());
        int officialA = target.getMatch().getOfficialScoreA();
        int officialB = target.getMatch().getOfficialScoreB();
        for (Team team : userTeams) {
            long count = all.stream()
                .filter(p -> team.getMembers().contains(p.getUser()))
                .filter(p -> p.getPredictedScoreA() == officialA && p.getPredictedScoreB() == officialB)
                .count();
            if (count == 1) return true;
        }
        return false;
    }

    private boolean isUniqueWinnerInAnyTeam(Prediction target, List<Prediction> all, String officialWinner) {
        List<Team> userTeams = teamRepository.findByMembersContains(target.getUser());
        for (Team team : userTeams) {
            long count = all.stream()
                .filter(p -> team.getMembers().contains(p.getUser()))
                .filter(p -> getWinner(p.getPredictedScoreA(), p.getPredictedScoreB()).equals(officialWinner))
                .count();
            if (count == 1) return true;
        }
        return false;
    }

    private String getWinner(int scoreA, int scoreB) {
        if (scoreA > scoreB) return "A";
        if (scoreB > scoreA) return "B";
        return "DRAW";
    }
}

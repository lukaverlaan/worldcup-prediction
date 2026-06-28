package be.lukaverlaan.ewdj.worldcup.service;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component("roundLabels")
public class RoundLabels {

    private final MessageSource messageSource;

    public RoundLabels(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String translate(String round) {
        if (round == null) return "";
        var locale = LocaleContextHolder.getLocale();
        String key = switch (round) {
            case "Round of 32"    -> "bracket.round32";
            case "Round of 16"    -> "bracket.round16";
            case "Quarter-finals" -> "bracket.qf";
            case "Semi-finals"    -> "bracket.sf";
            case "3rd Place Final"-> "bracket.third";
            case "Final"          -> "bracket.final";
            default -> round.startsWith("Group Stage") ? "round.group" : null;
        };
        if (key == null) return round;
        return messageSource.getMessage(key, null, round, locale);
    }
}

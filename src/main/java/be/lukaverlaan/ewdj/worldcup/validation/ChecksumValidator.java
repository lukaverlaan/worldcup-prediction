package be.lukaverlaan.ewdj.worldcup.validation;

import be.lukaverlaan.ewdj.worldcup.form.MatchForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ChecksumValidator implements ConstraintValidator<ValidChecksum, MatchForm> {

    @Override
    public boolean isValid(MatchForm form, ConstraintValidatorContext context) {
        if (form == null) return true;

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        // Controleer voor verschillende teams
        String teamA = form.getTeamA();
        String teamB = form.getTeamB();
        if (teamA != null && teamB != null && teamA.trim().equalsIgnoreCase(teamB.trim())) {
            context.buildConstraintViolationWithTemplate("{validation.teams.different}")
                   .addPropertyNode("teamB")
                   .addConstraintViolation();
            valid = false;
        }

        // Validatie checksum = stadiumCode % 97
        String code = form.getStadiumCode();
        Integer checksum = form.getChecksum();
        if (code != null && !code.isBlank() && checksum != null) {
            try {
                int codeInt = Integer.parseInt(code.trim());
                if (codeInt % 97 != checksum) {
                    context.buildConstraintViolationWithTemplate("{validation.checksum.invalid}")
                           .addPropertyNode("checksum")
                           .addConstraintViolation();
                    valid = false;
                }
            } catch (NumberFormatException e) {
                context.buildConstraintViolationWithTemplate("{validation.stadiumcode.digits}")
                       .addPropertyNode("stadiumCode")
                       .addConstraintViolation();
                valid = false;
            }
        }

        return valid;
    }
}

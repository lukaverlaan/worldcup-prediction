package be.lukaverlaan.ewdj.worldcup.validation;

import be.lukaverlaan.ewdj.worldcup.form.MatchForm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ChecksumValidatorTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private MatchForm validForm() {
        MatchForm form = new MatchForm();
        form.setTeamA("France");
        form.setTeamB("Brazil");
        form.setDateTime(LocalDateTime.of(2026, 6, 14, 21, 0));
        form.setStadiumCode("1234");
        form.setChecksum(1234 % 97);
        return form;
    }

    @Test
    void validFormPassesValidation() {
        Set<ConstraintViolation<MatchForm>> violations = validator.validate(validForm());
        assertThat(violations).isEmpty();
    }

    @Test
    void wrongChecksumFailsValidation() {
        MatchForm form = validForm();
        form.setChecksum(99);
        Set<ConstraintViolation<MatchForm>> violations = validator.validate(form);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("checksum"));
    }

    @Test
    void sameTeamsFailsValidation() {
        MatchForm form = validForm();
        form.setTeamB("France");
        Set<ConstraintViolation<MatchForm>> violations = validator.validate(form);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("teamB"));
    }

    @Test
    void dateOutsideWorldCupRangeFails() {
        MatchForm form = validForm();
        form.setDateTime(LocalDateTime.of(2026, 5, 1, 12, 0)); // before WC
        Set<ConstraintViolation<MatchForm>> violations = validator.validate(form);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("dateTime"));
    }

    @Test
    void dateWithinWorldCupRangePasses() {
        MatchForm form = validForm();
        form.setDateTime(LocalDateTime.of(2026, 7, 10, 15, 0));
        Set<ConstraintViolation<MatchForm>> violations = validator.validate(form);
        assertThat(violations).isEmpty();
    }

    @Test
    void nullChecksumSkipsChecksumValidation() {
        MatchForm form = validForm();
        form.setChecksum(null);
        form.setStadiumCode(null);
        Set<ConstraintViolation<MatchForm>> violations = validator.validate(form);
        assertThat(violations).isEmpty();
    }
}

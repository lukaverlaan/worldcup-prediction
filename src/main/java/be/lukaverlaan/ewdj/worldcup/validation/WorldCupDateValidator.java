package be.lukaverlaan.ewdj.worldcup.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class WorldCupDateValidator implements ConstraintValidator<WorldCupDate, LocalDateTime> {

    private static final LocalDate MIN_DATE = LocalDate.of(2026, 6, 11);
    private static final LocalDate MAX_DATE = LocalDate.of(2026, 7, 19);

    @Override
    public boolean isValid(LocalDateTime dateTime, ConstraintValidatorContext context) {
        if (dateTime == null) return true;
        LocalDate date = dateTime.toLocalDate();
        return !date.isBefore(MIN_DATE) && !date.isAfter(MAX_DATE);
    }
}

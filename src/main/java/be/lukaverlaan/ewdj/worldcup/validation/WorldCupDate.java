package be.lukaverlaan.ewdj.worldcup.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = WorldCupDateValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WorldCupDate {
    String message() default "{validation.date.worldcup}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

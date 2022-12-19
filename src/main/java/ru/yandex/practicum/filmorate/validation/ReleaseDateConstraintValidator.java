package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReleaseDateConstraintValidator implements ConstraintValidator<ReleaseDate, LocalDate> {

    private LocalDate date;
    DateTimeFormatter dp = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(ReleaseDate constraintAnnotation) {
        this.date = LocalDate.parse(constraintAnnotation.value(), dp);
    }

    @Override
    public boolean isValid(LocalDate target, ConstraintValidatorContext constraintValidatorContext) {
        return !target.isBefore(date);
    }
}

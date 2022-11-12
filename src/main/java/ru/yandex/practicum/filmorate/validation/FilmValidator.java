package ru.yandex.practicum.filmorate.validation;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Component
public class FilmValidator {
    private final static LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    public void validate(Film film) {
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            throw new ValidationException("Дата релиза не указана или не может быть раньше 28 декабря 1895 года");
        }
    }
}

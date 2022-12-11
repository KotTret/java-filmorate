package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {

    private Integer id;
    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;
    @NotBlank
    @Size(max = 200, message = "Максимальная длина описания фильма — 200 символов.")
    private String description;
    @NotNull
    @ReleaseDate("1895-12-28")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private Integer duration;

    @NotNull(message = "MPA rating не может быть пустым.")
    private Mpa mpa;

    private List<Genre> genres = new ArrayList<>();
    private int numberOfLikes = 0;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration_in_minutes", duration);
        values.put("mpa_id", mpa.getId());
        values.put("number_of_likes", numberOfLikes);
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(id, film.id) && Objects.equals(name, film.name)
                && Objects.equals(description, film.description)
                && Objects.equals(releaseDate, film.releaseDate)
                && Objects.equals(duration, film.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, releaseDate, duration);
    }
}
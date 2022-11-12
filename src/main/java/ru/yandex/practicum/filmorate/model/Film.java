package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
public class Film {

    private Integer id;
    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;
    @NotBlank
    @Size(max = 200, message = "Максимальная длина описания фильма — 200 символов.")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private Integer duration;

}

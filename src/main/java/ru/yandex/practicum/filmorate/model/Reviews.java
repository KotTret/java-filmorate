package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reviews {
    private Integer reviewId;
    @NotBlank(message = "Отзыв не может быть пустым.")
    private String content;
    @NotNull
    private Boolean isPositive;
    @NotNull(message = "id пользователя не может быть пустым.")
    private Integer userId;
    @NotNull(message = "id фильма не может быть пустым.")
    private Integer filmId;
    private Integer useful;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("content", content);
        values.put("is_positive", isPositive);
        values.put("user_id", userId);
        values.put("film_id", filmId);
        values.put("useful", useful);
        return values;
    }

}

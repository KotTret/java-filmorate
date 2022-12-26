package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reviews {
    @NotNull(groups = Update.class)
    private Integer reviewId;
    @NotNull(groups = Create.class)
    @NotNull(groups = Update.class)
    private String content;
    @NotNull(groups = Create.class)
    @NotNull(groups = Update.class)
    private Boolean isPositive;
    @NotNull(groups = Create.class)
    private Integer userId;
    @NotNull(groups = Create.class)
    private Integer filmId;
    private int useful;

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

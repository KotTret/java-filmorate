package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;


@Data
@Builder
public class Film {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    public static Integer idCounter = 1;

    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

}

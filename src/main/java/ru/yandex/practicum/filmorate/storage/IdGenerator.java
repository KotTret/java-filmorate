package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class IdGenerator {

    private Integer id = 0;

    public int getId() {
        id++;
        return id;

    }
}

package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;

import java.util.List;

public interface FeedStorage {

    void add(Integer id, Integer userId, EventType type, Operation operation);
    List<Event> get(Integer id);
}

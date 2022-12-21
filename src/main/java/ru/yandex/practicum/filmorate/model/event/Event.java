package ru.yandex.practicum.filmorate.model.event;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    private int eventId;
    private Long timestamp;
    private int userId;
    private EventType eventType;
    private Operation operation;
    private int entityId;

}

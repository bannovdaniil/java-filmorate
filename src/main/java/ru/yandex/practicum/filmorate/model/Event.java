package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @JsonProperty("timestamp")
    private long addedAt;
    private long userId;
    private EventType eventType;
    @JsonProperty("operation")
    private EventOperation eventOperation;
    @JsonProperty("eventId")
    private long id;
    private long entityId;
}

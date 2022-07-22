package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

public enum EventType {
    LIKE(1),
    REVIEW(2),
    FRIEND(3);

    @Getter
    private final int id;

    EventType(int id) {
        this.id = id;
    }

}

package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

public enum EventOperation {
    ADD(1),
    UPDATE(2),
    REMOVE(3);

    @Getter
    private final int id;

    EventOperation(int id) {
        this.id = id;
    }
}

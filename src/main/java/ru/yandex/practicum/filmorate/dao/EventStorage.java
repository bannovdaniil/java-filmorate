package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    void addEvent(long userId, int eventTypeId, int eventOperationId, long entityId);

    List<Event> findAllByUserId(Long userId);
}

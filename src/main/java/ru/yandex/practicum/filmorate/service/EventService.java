package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventStorage eventStorage;

    public void addEvent(final long userId, EventType eventType, EventOperation eventOperation, final long entityId) {
        eventStorage.addEvent(userId, eventType.getId(), eventOperation.getId(), entityId);
    }

    public List<Event> findAllByUserId(Long userId) {
        return eventStorage.findAllByUserId(userId);
    }
}

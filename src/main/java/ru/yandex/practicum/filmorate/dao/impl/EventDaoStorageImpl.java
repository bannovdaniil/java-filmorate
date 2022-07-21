package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EventDaoStorageImpl implements EventStorage {

    private final JdbcTemplate jdbcTemplate;
    @Override
    public void addEvent(long userId, int eventTypeId, int eventOperationId, long entityId) {
        String sql = "INSERT INTO USER_EVENTS (ADDED_AT, USER_ID, EVENT_TYPE_ID, EVENT_OPERATION_ID, ENTITY_ID) " +
                " VALUES(? , ? , ? , ? , ?)";

        jdbcTemplate.update(sql,
                new Timestamp(System.currentTimeMillis()).getTime(),
                userId,
                eventTypeId,
                eventOperationId,
                entityId
                );
        log.info("User event created");
    }

    @Override
    public List<Event> findAllByUserId(Long userId) {
        String sql =
                "SELECT " +
                "   UE.EVENT_ID, " +
                "   UE.ADDED_AT, " +
                "   UE.USER_ID, " +
                "   UE.ENTITY_ID, " +
                "   ET.NAME AS EVENT_TYPE_NAME, " +
                "   EO.NAME AS EVENT_OPERATION_NAME " +
                "FROM USER_EVENTS AS UE " +
                "LEFT JOIN EVENT_TYPES AS ET " +
                "   ON UE.EVENT_TYPE_ID = ET.EVENT_TYPE_ID " +
                "LEFT JOIN EVENT_OPERATIONS AS EO " +
                "   ON UE.EVENT_OPERATION_ID = EO.EVENT_OPERATION_ID " +
                "WHERE UE.USER_ID=?";
        return jdbcTemplate.query(sql, this::makeEvent, userId);
    }

    private Event makeEvent(ResultSet rs, int i) throws SQLException {
        Event event = new Event();
        event.setId(rs.getLong("event_id"));
        event.setAddedAt(rs.getLong("added_at"));
        event.setUserId(rs.getLong("user_id"));
        event.setEventType(EventType.valueOf(rs.getString("event_type_name")));
        event.setEventOperation(EventOperation.valueOf(rs.getString("event_operation_name")));
        event.setEntityId(rs.getLong("entity_id"));
        return event;
    }
}

package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.exceptions.MpaRatingNotFound;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class MpaDaoStorageImpl implements MpaStorage {
    final Map<Integer, MpaRating> ratings;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDaoStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratings = loadValueFromDb();
    }

    private Map<Integer, MpaRating> loadValueFromDb() {
        Map<Integer, MpaRating> ratings = new HashMap<>();

        String sql = "SELECT * FROM RATINGS;";
        jdbcTemplate.query(sql, (ResultSet rs) -> {
            do {
                int ratingId = rs.getInt("RATING_ID");
                ratings.put(ratingId
                        , new MpaRating(ratingId, rs.getString("NAME")));
            } while (rs.next());
        });
        log.info("Rating list size: {}", ratings.size());

        return ratings;
    }

    @Override
    public int indexOfValue(String rating) throws MpaRatingNotFound {
        Optional<Integer> index = ratings.entrySet()
                .stream()
                .filter((e) -> e.getValue().getName().equals(rating))
                .map(Map.Entry::getKey)
                .findFirst();
        if (index.isPresent()) {
            return index.get();
        }
        throw new MpaRatingNotFound("Rating Value not found.");
    }

    @Override
    public MpaRating getRatingMpaById(int ratingId) throws MpaRatingNotFound {
        if (ratings.containsKey(ratingId)) {
            return ratings.get(ratingId);
        }
        throw new MpaRatingNotFound("Rating Index not found.");
    }

    @Override
    public Collection<MpaRating> findAll() {
        return ratings.values();
    }
}

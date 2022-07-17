package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.exceptions.MpaRatingNotFound;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@Repository
@Slf4j
public class MpaDaoStorageImpl implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDaoStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Cacheable("indexMpaRating")
    public int indexOfValue(String ratingName) throws MpaRatingNotFound {
        String sql = "SELECT RATING_ID FROM RATINGS WHERE NAME = ? ;";
        List<Integer> ratingIndex = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getInt("RATING_ID")
                , ratingName);
        if (ratingIndex.size() > 0) {
            return ratingIndex.get(0);
        }

        throw new MpaRatingNotFound("Rating Value not found.");
    }

    @Override
    @Cacheable("mpaRating")
    public MpaRating getRatingMpaById(int ratingId) throws MpaRatingNotFound {
        String sql = "SELECT * FROM RATINGS WHERE RATING_ID = ? ;";
        List<MpaRating> ratingsList = jdbcTemplate.query(sql,
                (rs, rowNum) -> new MpaRating(rs.getInt("RATING_ID"), rs.getString("NAME"))
                , ratingId);
        if (ratingsList.size() > 0) {
            return ratingsList.get(0);
        }
        throw new MpaRatingNotFound("MPA Rating Index not found.");
    }

    @Override
    @Cacheable("mpaRatingList")
    public List<MpaRating> findAll() {
        String sql = "SELECT * FROM RATINGS ORDER BY RATING_ID;";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new MpaRating(rs.getInt("RATING_ID"), rs.getString("NAME")));
    }
}

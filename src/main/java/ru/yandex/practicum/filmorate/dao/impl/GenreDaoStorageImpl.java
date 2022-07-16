package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFound;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.util.*;

@Repository
@Slf4j
public class GenreDaoStorageImpl implements GenreStorage {
    final Map<Integer, Genre> genres;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.genres = loadValueFromDb();
    }

    private Map<Integer, Genre> loadValueFromDb() {
        Map<Integer, Genre> genres = new HashMap<>();

        String sql = "SELECT * FROM GENRES ORDER BY GENRE_ID;";
        jdbcTemplate.query(sql, (ResultSet rs) -> {
            do {
                int genreId = rs.getInt("GENRE_ID");
                genres.put(genreId
                        , new Genre(genreId, rs.getString("NAME")));
            } while (rs.next());
        });
        log.info("Load genres list size: {}", genres.size());
        return genres;
    }

    @Override
    public int indexOfValue(String genre) throws GenreNotFound {
        Optional<Integer> index = genres.entrySet()
                .stream()
                .filter((e) -> e.getValue().getName().equals(genre))
                .map(Map.Entry::getKey)
                .findFirst();
        if (index.isPresent()) {
            return index.get();
        }
        throw new GenreNotFound("Genre Value not found.");
    }

    @Override
    public Genre getGenreById(int genreId) throws GenreNotFound {
        if (genres.containsKey(genreId)) {
            return genres.get(genreId);
        }
        throw new GenreNotFound("Genre Index not exist.");
    }

    public List<Genre> getFilmGenres(long filmId) {
        String sql = "SELECT * FROM GENRES WHERE GENRE_ID IN (SELECT GENRE_ID FROM FILM_GENRES WHERE FILM_ID = ?) " +
                " ORDER BY GENRE_ID;";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Genre(rs.getInt("GENRE_ID"), rs.getString("NAME"))
                , filmId);
    }

    @Override
    public Collection<Genre> findAll() {
        return genres.values();
    }
}

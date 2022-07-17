package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFound;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
@Slf4j
public class GenreDaoStorageImpl implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Cacheable("indexGenres")
    public int indexOfValue(String genreName) throws GenreNotFound {
        String sql = "SELECT GENRE_ID FROM GENRES WHERE NAME = ? ;";
        List<Integer> genresIndex = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getInt("GENRE_ID")
                , genreName);
        if (genresIndex.size() > 0) {
            return genresIndex.get(0);
        }

        throw new GenreNotFound("Genre Value not found.");
    }

    @Override
    @Cacheable("genres")
    public Genre getGenreById(int genreId) throws GenreNotFound {
        String sql = "SELECT * FROM GENRES WHERE GENRE_ID = ? ;";
        List<Genre> genres = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Genre(rs.getInt("GENRE_ID"), rs.getString("NAME"))
                , genreId);
        if (genres.size() > 0) {
            return genres.get(0);
        }
        throw new GenreNotFound("Genre Index not found.");
    }

    public List<Genre> getFilmGenres(long filmId) {
        String sql = "SELECT * FROM GENRES WHERE GENRE_ID IN (SELECT GENRE_ID FROM FILM_GENRES WHERE FILM_ID = ?) " +
                " ORDER BY GENRE_ID;";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Genre(rs.getInt("GENRE_ID"), rs.getString("NAME"))
                , filmId);
    }

    @Override
    @Cacheable("genresList")
    public List<Genre> findAll() {
        String sql = "SELECT * FROM GENRES ORDER BY GENRE_ID;";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Genre(rs.getInt("GENRE_ID"), rs.getString("NAME")));
    }
}

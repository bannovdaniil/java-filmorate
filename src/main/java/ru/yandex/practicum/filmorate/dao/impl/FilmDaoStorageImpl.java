package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.mapper.DtoMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmDaoStorageImpl implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DtoMapper dtoMapper;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Override
    public List<Film> findAll() throws MpaRatingNotFound {
        String sql = "SELECT * FROM FILMS;";
        log.info("Get Film list from DB.");

        List<Film> films = jdbcTemplate.query(sql, this::makeFilm);
        for (Film film : films) {
            film.setMpa(mpaStorage.getRatingMpaById(film.getMpa().getId()));
        }

        return films;
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getLong("duration"));
        film.setRate(rs.getLong("rate"));
        film.setMpa(new MpaRating(rs.getInt("rating_ID")));
        return film;
    }

    @Override
    public Film create(DtoFilm dtoFilm) throws MpaRatingNotFound, GenreNotFound, MpaRatingNotValid {
        String sql = "INSERT INTO FILMS (NAME, DESCRIPTION, DURATION, RELEASE_DATE, RATE, RATING_ID) " +
                " VALUES(? , ? , ? , ? , ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement prSt = connection.prepareStatement(
                            sql
                            , new String[]{"film_id"});
                    prSt.setString(1, dtoFilm.getName());
                    prSt.setString(2, dtoFilm.getDescription());
                    prSt.setLong(3, dtoFilm.getDuration());
                    prSt.setDate(4, Date.valueOf(dtoFilm.getReleaseDate()));
                    prSt.setLong(5, dtoFilm.getRate());
                    prSt.setLong(6, dtoFilm.getMpa().getId());
                    return prSt;
                }
                , keyHolder);

        updateMpaRating(dtoFilm);
        updateGenresNameById(dtoFilm);

        Film film = dtoMapper.dtoToFilm(dtoFilm);
        film.setId(keyHolder.getKey().longValue());
        saveGenreToDb(film);

        log.info("Film create: {}.", film.getRate());

        return film;
    }

    private void saveGenreToDb(Film film) {
        long filmId = film.getId();
        if (getFilmGenres(filmId).size() > 0) {
            String sql = "DELETE FROM FILM_GENRES WHERE FILM_ID = ? ;";
            jdbcTemplate.update(sql, filmId);
        }
        if (film.getGenres() != null) {
            final Set<Genre> filmGenres = new HashSet<>(film.getGenres());
            String sql = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
            for (Genre genre : filmGenres) {
                jdbcTemplate.update(sql, filmId, genre.getId());
            }
        }
    }

    @Override
    public Film update(DtoFilm dtoFilm) throws FilmNotFoundException, MpaRatingNotFound, MpaRatingNotValid, GenreNotFound {
        if (isFilmExist(dtoFilm.getId())) {
            String sql = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, DURATION = ?, RELEASE_DATE = ?, RATING_ID = ? " +
                    " WHERE FILM_ID = ? ";

            updateMpaRating(dtoFilm);
            jdbcTemplate.update(sql
                    , dtoFilm.getName()
                    , dtoFilm.getDescription()
                    , dtoFilm.getDuration()
                    , Date.valueOf(dtoFilm.getReleaseDate())
                    , dtoFilm.getMpa().getId()
                    , dtoFilm.getId()
            );
        } else {
            throw new FilmNotFoundException("Update failed, film not found.");
        }

        updateMpaRating(dtoFilm);
        updateGenresNameById(dtoFilm);

        Film film = dtoMapper.dtoToFilm(dtoFilm);
        saveGenreToDb(film);

        return dtoMapper.dtoToFilm(dtoFilm);
    }

    @Override
    public void remove(DtoFilm dtoFilm) throws InvalidFilmRemoveException {
        if (isFilmExist(dtoFilm.getId())) {
            String sql = "DELETE FROM FILM_GENRES WHERE FILM_ID = ? ;";
            jdbcTemplate.update(sql, dtoFilm.getId());

            sql = "DELETE FROM LIKES WHERE FILM_ID = ? ;";
            jdbcTemplate.update(sql, dtoFilm.getId());

            sql = "DELETE FROM FILMS WHERE FILM_ID = ? ;";
            jdbcTemplate.update(sql, dtoFilm.getId());
        } else {
            throw new InvalidFilmRemoveException("Film for delete not found.");
        }
    }

    @Override
    public Film getFilmById(Long filmId) throws FilmNotFoundException, MpaRatingNotFound {
        if (isFilmExist(filmId)) {
            String sql = "SELECT * FROM FILMS " +
                    " WHERE FILM_ID = ?" +
                    " LIMIT 1;";

            List<Film> films = jdbcTemplate.query(sql, this::makeFilm, filmId);
            Film film = films.get(0);

            film.setMpa(mpaStorage.getRatingMpaById(film.getMpa().getId()));
            film.setGenres(getFilmGenres(film.getId()));

            return film;
        } else {
            throw new FilmNotFoundException("Film ID not found.");
        }
    }

    @Override
    public List<Film> getFilmTop(Long count) throws MpaRatingNotFound {
        log.info("Get Top Film list from DB.");

        String sql = "SELECT * FROM FILMS " +
                " ORDER BY LIKES DESC" +
                " LIMIT ?;";

        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, count);
        for (Film film : films) {
            film.setMpa(mpaStorage.getRatingMpaById(film.getMpa().getId()));
        }

        return films;
    }

    @Override
    public boolean isFilmExist(long filmId) {
        String sql = "SELECT COUNT(*) FROM FILMS WHERE FILM_ID = ? ;";
        int filmCount = jdbcTemplate.queryForObject(sql, Integer.class, filmId);

        return filmCount > 0;
    }

    @Override
    public void addLike(long filmId) {
        String sql = "UPDATE FILMS SET LIKES = LIKES + 1 WHERE FILM_ID = ?; ";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public void removeLike(long filmId) {
        String sql = "UPDATE FILMS SET LIKES = LIKES - 1 WHERE FILM_ID = ?; ";
        jdbcTemplate.update(sql, filmId);
    }

    private List<Genre> getFilmGenres(long filmId) {
        String sql = "SELECT * FROM FILM_GENRES WHERE FILM_ID = ? ORDER BY GENRE_ID;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
                    int genreId = rs.getInt("GENRE_ID");
                    try {
                        return genreStorage.getGenreById(genreId);
                    } catch (GenreNotFound e) {
                        throw new RuntimeException(e);
                    }
                }
                , filmId);
    }

    private void updateMpaRating(DtoFilm dtoFilm) throws MpaRatingNotFound, MpaRatingNotValid {
        if (dtoFilm.getMpa() == null) {
            throw new MpaRatingNotValid("Не задано значение MPA");
        }
        dtoFilm.setMpa(mpaStorage.getRatingMpaById(dtoFilm.getMpa().getId()));
    }

    private void updateGenresNameById(DtoFilm dtoFilm) throws GenreNotFound {
        if (dtoFilm.getGenres() == null) {
            return;
        }
        Set<Genre> genresWithName = new HashSet<>();
        for (Genre genre : dtoFilm.getGenres()) {
            genresWithName.add(genreStorage.getGenreById(genre.getId()));
        }
        dtoFilm.setGenres(new ArrayList<>(genresWithName));
    }
}

package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DirectorDaoStorageImpl implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director create(DirectorDto directorDto) throws DirectorNotFoundException {
        String sql = "INSERT INTO `DIRECTORS` (NAME) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql, new String[]{"director_id"});

            ps.setString(1, directorDto.getName());
            return ps;
        }, keyHolder);
        return getById(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    @Override
    public Director update(DirectorDto directorDto) throws DirectorNotFoundException {
        if (isExistsDirector(directorDto.getId())) {
            String sql = "UPDATE `DIRECTORS` SET NAME = ? " +
                    " WHERE DIRECTOR_ID = ? ;";
            jdbcTemplate.update(sql, directorDto.getName(), directorDto.getId());
        } else {
            throw new DirectorNotFoundException("Director ID not found.");
        }
        return getById(directorDto.getId());
    }

    @Override
    public void delete(int id) throws DirectorNotFoundException {
        if (isExistsDirector(id)) {
            jdbcTemplate.update("DELETE FROM FILM_DIRECTORS WHERE DIRECTOR_ID = ?;", id);
            jdbcTemplate.update("DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?;", id);
        } else {
            throw new DirectorNotFoundException("Director ID not found.");
        }
    }

    @Override
    public Director getById(int id) throws DirectorNotFoundException {
        String sql = "SELECT DIRECTOR_ID, NAME FROM `DIRECTORS` WHERE DIRECTOR_ID = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (rowSet.next()) {
            return new Director(rowSet.getInt("director_id"), rowSet.getString("name"));
        } else {
            throw new DirectorNotFoundException("Director ID= " + id + " not found.");
        }
    }

    @Override
    public List<Director> getAll() {
        String sql = "SELECT DIRECTOR_ID, NAME FROM `DIRECTORS`;";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Director(rs.getInt("director_id"), rs.getString("name")));
    }

    @Override
    public List<Director> saveDirectorsOfFilm(Film film) throws DirectorNotFoundException {
        String sql = "INSERT INTO FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES ( ?, ? );";

        for (Director director : film.getDirectors()) {
            if (isExistsDirector(director.getId())) {
                jdbcTemplate.update(sql, film.getId(), director.getId());
            } else {
                throw new DirectorNotFoundException("Director with id= " + director.getId() + " not found!");
            }
        }

        return film.getDirectors().isEmpty() ? null : getDirectorsByFilm(film.getId());
    }

    @Override
    public List<Director> updateDirectorsOfFilm(Film film) throws DirectorNotFoundException {
        deleteDirectorsOfFilm(film.getId());
        return saveDirectorsOfFilm(film);
    }

    @Override
    public void deleteDirectorsOfFilm(Long film_id) {
        String sql = "DELETE FROM FILM_DIRECTORS WHERE FILM_ID = ?;";
        jdbcTemplate.update(sql, film_id);
    }

    @Override
    public List<Director> getDirectorsByFilm(Long film_id) {
        String sql = "SELECT DIRECTOR_ID, NAME FROM DIRECTORS WHERE DIRECTOR_ID IN " +
                "(SELECT DIRECTOR_ID FROM FILM_DIRECTORS WHERE FILM_ID = ?)";

        return jdbcTemplate.query(sql, (rs, numRow) ->
                new Director(rs.getInt("director_id"), rs.getString("name")), film_id);
    }

    private boolean isExistsDirector(int id) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM DIRECTORS WHERE DIRECTOR_ID = ? ;",
                Integer.class, id) > 0;
    }

    @Override
    public void validateDirector(int id) throws DirectorNotFoundException {
        if (!isExistsDirector(id)) throw new DirectorNotFoundException("Director with id= " + id + " not found!");
    }
}

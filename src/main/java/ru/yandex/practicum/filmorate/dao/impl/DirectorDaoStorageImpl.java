package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dto.DtoDirector;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.util.List;

@Component
@Slf4j
public class DirectorDaoStorageImpl implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDaoStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director create(DtoDirector dtoDirector) {
        String sql = "INSERT INTO `DIRECTORS` (NAME) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql, new String[]{"id"});

            ps.setString(1, dtoDirector.getName());
            return ps;
        }, keyHolder);
        return getById(keyHolder.getKey().intValue());
    }

    @Override
    public Director update(DtoDirector dtoDirector) {
        String sql = "MERGE INTO `DIRECTORS` (ID, NAME) VALUES (?, ?);";
        if (isExists(dtoDirector.getId())) {
            jdbcTemplate.update(sql, dtoDirector.getId(), dtoDirector.getName());
        } else {
            throw new DirectorNotFoundException("Director ID not found.");
        }
        return getById(dtoDirector.getId());
    }

    @Override
    public void delete(int id) {
        if (isExists(id)) {
            jdbcTemplate.update("DELETE FROM FILM_DIRECTORS WHERE DIRECTOR_ID = ?;", id);
            jdbcTemplate.update("DELETE FROM DIRECTORS WHERE ID = ?;", id);
        } else {
            throw new DirectorNotFoundException("Director ID not found.");
        }
    }

    @Override
    public Director getById(int id) {
        String sql = "SELECT ID, NAME FROM `DIRECTORS` WHERE ID = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (rowSet.next()) {
            return new Director(rowSet.getInt("id"), rowSet.getString("name"));
        } else {
            throw new DirectorNotFoundException("Director ID= "+ id + " not found.");
        }
    }

    @Override
    public List<Director> getAll() {
        String sql = "SELECT ID, NAME FROM `DIRECTORS`;";
        return jdbcTemplate.query(sql,
                (rs, rowNum)->new Director(rs.getInt("id"), rs.getString("name")));
    }

    @Override
    public List<Director> saveDirectorsOfFilm(Long film_id, List<Director> directors) {
        String sql = "MERGE INTO FILM_DIRECTORS (film_id, director_id) values ( ?, ? );";

        for (Director director : directors) {
            if (isExists(director.getId())) {
                jdbcTemplate.update(sql, film_id, director.getId());
            } else {
                throw new DirectorNotFoundException("Director with id= " + director.getId() + " not found!");
            }
        }

        return directors.isEmpty() ? null : getDirectorsByFilm(film_id);
    }

    @Override
    public List<Director> updateDirectorsOfFilm(Long film_id, List<Director> directors) {
        deleteDirectorsOfFilm(film_id);
        return saveDirectorsOfFilm(film_id, directors);
    }

    @Override
    public void deleteDirectorsOfFilm(Long film_id) {
        String sql = "DELETE FROM FILM_DIRECTORS WHERE FILM_ID = ?;";
        jdbcTemplate.update(sql, film_id);
    }

    @Override
    public List<Director> getDirectorsByFilm(Long film_id) {
        String sql = "SELECT ID, NAME FROM DIRECTORS WHERE ID IN " +
                    "(SELECT DIRECTOR_ID FROM FILM_DIRECTORS WHERE FILM_ID = ?)";

        return jdbcTemplate.query(sql, (rs, numRow) ->
                new Director(rs.getInt("id"), rs.getString("name")), film_id);
    }

    @Override
    public boolean isExists(int id) {
        return jdbcTemplate.queryForObject( "SELECT COUNT(*) FROM DIRECTORS WHERE ID = ? ;", Integer.class, id) > 0;
    }

    @Override
    public List<Long> getFilmsByDirectorOrderByLikes(int id) {
        validateDirector(id);
        String sql = "SELECT F.FILM_ID, COUNT(*) C FROM FILMS F LEFT JOIN LIKES L ON F.FILM_ID = L.FILM_ID" +
                " WHERE F.FILM_ID IN (SELECT FD.FILM_ID FROM FILM_DIRECTORS FD WHERE FD.DIRECTOR_ID = ?)" +
                " GROUP BY F.FILM_ID ORDER BY C DESC, F.FILM_ID ASC";

        return jdbcTemplate.query(sql, (rs, numRow) -> rs.getLong("film_id"), id);
    }

    @Override
    public List<Long> getFilmsByDirectorOrderByDate(int id) {
        validateDirector(id);
        String sql = "SELECT FILM_ID, RELEASE_DATE FROM FILMS WHERE FILM_ID IN " +
                "(SELECT FD.FILM_ID FROM FILM_DIRECTORS FD WHERE FD.DIRECTOR_ID = ?) " +
                "ORDER BY RELEASE_DATE ASC";
        return jdbcTemplate.query(sql, (rs, numRow) -> rs.getLong("film_id"), id);
    }

    private void validateDirector(int id){
        if (!isExists(id))  throw new DirectorNotFoundException("Director with id= " + id + " not found!");
    }
}

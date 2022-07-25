package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.mapper.DtoMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("ALL")
@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmDaoStorageImpl implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DtoMapper dtoMapper;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final UserStorage userStorage;

    private final DirectorStorage directorStorage;

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
        film.setDirectors(directorStorage.getDirectorsByFilm(film.getId()));
        return film;
    }

    @Override
    public Film create(DtoFilm dtoFilm) throws MpaRatingNotFound, GenreNotFound, DirectorNotFoundException {
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
                    prSt.setLong(5, dtoFilm.getRate() == null ? 0 : dtoFilm.getRate());
                    prSt.setLong(6, dtoFilm.getMpa().getId());
                    return prSt;
                }
                , keyHolder);

        updateMpaRating(dtoFilm);
        updateGenresNameById(dtoFilm);

        Film film = dtoMapper.dtoToFilm(dtoFilm);
        film.setId(keyHolder.getKey().longValue());
        saveGenreToDb(film);

        List<Director> directorsOfFilm = directorStorage.saveDirectorsOfFilm(film);
        film.setDirectors(directorsOfFilm);

        log.info("Film create: {}.", film.getRate());
        return film;
    }

    private void saveGenreToDb(Film film) {
        long filmId = film.getId();
        if (genreStorage.getFilmGenres(filmId).size() > 0) {
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
    public Film update(DtoFilm dtoFilm) throws FilmNotFoundException, MpaRatingNotFound, GenreNotFound, DirectorNotFoundException {
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

        List<Director> directors = directorStorage.updateDirectorsOfFilm(film);
        film.setDirectors(directors);

        return film;
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

            directorStorage.deleteDirectorsOfFilm(dtoFilm.getId());
        } else {
            throw new InvalidFilmRemoveException("Film for delete not found.");
        }
    }

    @Override
    public Film getFilmById(Long filmId) throws FilmNotFoundException, MpaRatingNotFound {
        if (isFilmExist(filmId)) {
            String sql = "SELECT * FROM FILMS WHERE FILM_ID = ?;";

            List<Film> films = jdbcTemplate.query(sql, this::makeFilm, filmId);
            Film film = films.get(0);

            film.setMpa(mpaStorage.getRatingMpaById(film.getMpa().getId()));
            film.setGenres(genreStorage.getFilmGenres(film.getId()));

            return film;
        } else {
            throw new FilmNotFoundException("Film ID not found.");
        }
    }

    @Override
    public List<Film> getFilmTop(Long count, Integer genreId, Integer year) throws MpaRatingNotFound {
        log.info("Get Top Film list from DB.");

        List<Film> films;
        if (genreId == null && year == null) {
            films = getTopFilmByCount(count);
        } else if (genreId == null) {
            films = getTopFilmByCountYear(count, year);
        } else if (year == null) {
            films = getTopFilmByCountGenre(count, genreId);
        } else {
            films = getTopFilmByCountGenreYear(count, genreId, year);
        }

        for (Film film : films) {
            film.setMpa(mpaStorage.getRatingMpaById(film.getMpa().getId()));
            film.setGenres(genreStorage.getFilmGenres(film.getId()));
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

    private void updateMpaRating(DtoFilm dtoFilm) throws MpaRatingNotFound {
        dtoFilm.setMpa(mpaStorage.getRatingMpaById(dtoFilm.getMpa().getId()));
    }

    private void updateGenresNameById(DtoFilm dtoFilm) throws GenreNotFound {
        if (dtoFilm.getGenres() == null) {
            return;
        }
        List<Genre> genresWithName = new ArrayList<>();
        Set<Integer> doubleId = new HashSet<>();
        for (Genre genre : dtoFilm.getGenres()) {
            if (!doubleId.contains(genre.getId())) {
                doubleId.add(genre.getId());
                genresWithName.add(genreStorage.getGenreById(genre.getId()));
            }
        }
        dtoFilm.setGenres(genresWithName);
    }

    private List<Film> getTopFilmByCountGenreYear(Long count, Integer genreId, Integer year) {
        String sql = "SELECT * FROM FILMS WHERE " +
                "FILM_ID IN (SELECT FILM_ID FROM FILM_GENRES WHERE GENRE_ID = ?) " +
                "AND YEAR(RELEASE_DATE) = ? " +
                "ORDER BY LIKES DESC " + "" +
                "LIMIT ?;";

        return jdbcTemplate.query(sql, this::makeFilm, genreId, year, count);
    }

    private List<Film> getTopFilmByCountGenre(Long count, Integer genreId) {
        String sql = "SELECT * FROM FILMS WHERE " +
                "FILM_ID IN (SELECT FILM_ID FROM FILM_GENRES WHERE GENRE_ID = ?) " +
                "ORDER BY LIKES DESC " +
                "LIMIT ?;";

        return jdbcTemplate.query(sql, this::makeFilm, genreId, count);
    }

    private List<Film> getTopFilmByCountYear(Long count, Integer year) {
        String sql = "SELECT * FROM FILMS WHERE " +
                "YEAR(RELEASE_DATE) = ? " +
                "ORDER BY LIKES DESC " +
                "LIMIT ?;";

        return jdbcTemplate.query(sql, this::makeFilm, year, count);
    }

    private List<Film> getTopFilmByCount(Long count) {
        String sql = "SELECT * FROM FILMS " +
                "ORDER BY LIKES DESC " +
                "LIMIT ?;";

        return jdbcTemplate.query(sql, this::makeFilm, count);
    }

    @Override
    public List<Film> getFilmsByDirectorOrderByLikes(int id) throws MpaRatingNotFound, DirectorNotFoundException {
        directorStorage.validateDirector(id);
        String sql = "SELECT * FROM FILMS F" +
                " WHERE F.FILM_ID IN (SELECT FD.FILM_ID FROM FILM_DIRECTORS FD WHERE FD.DIRECTOR_ID = ?)" +
                " ORDER BY F.LIKES DESC, F.FILM_ID ASC";

        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, id);
        for (Film film : films) {
            film.setMpa(mpaStorage.getRatingMpaById(film.getMpa().getId()));
            film.setGenres(genreStorage.getFilmGenres(film.getId()));
        }
        return films;
    }

    @Override
    public List<Film> getFilmsByDirectorOrderByDate(int id) throws MpaRatingNotFound, DirectorNotFoundException {
        directorStorage.validateDirector(id);
        String sql = "SELECT * FROM FILMS F WHERE F.FILM_ID IN " +
                "(SELECT FD.FILM_ID FROM FILM_DIRECTORS FD WHERE FD.DIRECTOR_ID = ?) " +
                "ORDER BY F.RELEASE_DATE";
        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, id);
        for (Film film : films) {
            film.setMpa(mpaStorage.getRatingMpaById(film.getMpa().getId()));
            film.setGenres(genreStorage.getFilmGenres(film.getId()));
        }
        return films;
    }

    @Override
    public List<Film> getRecommendations(int userId) throws MpaRatingNotFound {
        String sql = "SELECT f.* FROM FILMS f JOIN LIKES l ON f.FILM_ID = l.FILM_ID JOIN " +
                "           (SELECT FILM_ID FROM LIKES l " +
                "           WHERE USER_ID IN (?, " +
                "               SELECT USER_ID FROM LIKES " +
                "               WHERE USER_ID  <> ? AND FILM_ID  IN " +
                "                   (SELECT FILM_ID FROM LIKES " +
                "                   WHERE USER_ID = ?) " +
                "               GROUP BY USER_ID " +
                "               ORDER BY sum(1) DESC " +
                "               LIMIT 1) " +
                "           GROUP BY FILM_ID " +
                "           HAVING COUNT(USER_ID) = 1) f2 ON l.FILM_ID = f2.FILM_ID" +
                "      WHERE l.USER_ID <> ?;";

        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, userId, userId, userId, userId);
        for (Film film : films) {
            film.setMpa(mpaStorage.getRatingMpaById(film.getMpa().getId()));
            film.setGenres(genreStorage.getFilmGenres(film.getId()));
        }

        return films;
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) throws MpaRatingNotFound, UserNotFoundException {
        if (!userStorage.isUserExist(userId)) {
            throw new UserNotFoundException(String.format("User not found by id = %d", userId));
        }
        if (!userStorage.isUserExist(friendId)) {
            throw new UserNotFoundException(String.format("User not found by id = %d", friendId));
        }

        String sql = "SELECT * " +
                "FROM FILMS " +
                "WHERE FILM_ID IN (" +
                "SELECT first_user_likes.FILM_ID " +
                "FROM (" +
                "SELECT FILM_ID " +
                "FROM LIKES " +
                "WHERE USER_ID = ?) AS first_user_likes " +
                "JOIN (" +
                "SELECT FILM_ID " +
                "FROM LIKES " +
                "WHERE USER_ID = ?) AS second_user_likes " +
                "ON first_user_likes.FILM_ID = second_user_likes.FILM_ID) " +
                "ORDER BY LIKES DESC";

        List<Film> commonFilms = jdbcTemplate.query(sql, this::makeFilm, userId, friendId);
        for (Film film : commonFilms) {
            film.setMpa(mpaStorage.getRatingMpaById(film.getMpa().getId()));
            film.setGenres(genreStorage.getFilmGenres(film.getId()));
        }
        return commonFilms;
    }

    public List<Film> searchFilms(String query, List<String> searchByParams) throws MpaRatingNotFound {
        List<Film> films;

        if (searchByParams.contains("title") && searchByParams.contains("director")) {
            films = searchFilmsByTitleAndDirector(query);
        } else if (searchByParams.contains("director")) {
            films = searchFilmsByDirector(query);
        } else {
            films = searchFilmsByTitle(query);
        }

        for (Film film : films) {
            film.setMpa(mpaStorage.getRatingMpaById(film.getMpa().getId()));
            film.setGenres(genreStorage.getFilmGenres(film.getId()));
        }

        return films;
    }

    @Override
    public void removeFilmById(Long filmId) throws FilmNotFoundException {
        if (isFilmExist(filmId)) {
            int paramsCount = 5;
            Long[] params = new Long[paramsCount];
            Arrays.fill(params, filmId);

            String sql =
                    "DELETE FROM likes WHERE film_id=?; " +
                            "DELETE FROM review_likes " +
                            "WHERE review_id IN (SELECT review_id FROM reviews WHERE film_id=?); " +
                            "DELETE FROM reviews WHERE film_id=?; " +
                            "DELETE FROM film_genres WHERE film_id=?; " +
                            "DELETE FROM films WHERE film_id=?";
            jdbcTemplate.update(sql, params);
        } else {
            throw new FilmNotFoundException("Film ID not found.");
        }
    }

    private List<Film> searchFilmsByTitle(String query) {
        log.info(String.format("Search films by title = %s", query));

        String searchQuery = "SELECT * " +
                "FROM FILMS " +
                "WHERE NAME ILIKE ? " +
                "ORDER BY LIKES DESC";

        return jdbcTemplate.query(searchQuery, this::makeFilm, "%" + query + "%");
    }

    private List<Film> searchFilmsByDirector(String query) {
        log.info(String.format("Search films by director = %s", query));

        String searchQuery = "SELECT F.FILM_ID, " +
                "   F.NAME, " +
                "   F.DESCRIPTION," +
                "   F.DURATION, " +
                "   F.LIKES, " +
                "   F.RATE, " +
                "   F.RELEASE_DATE, " +
                "   F.RATING_ID " +
                "FROM FILMS F " +
                "JOIN FILM_DIRECTORS  FD ON F.FILM_ID = FD.FILM_ID " +
                "JOIN DIRECTORS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "WHERE D.NAME ILIKE ? " +
                "ORDER BY LIKES DESC";

        return jdbcTemplate.query(searchQuery, this::makeFilm, "%" + query + "%");
    }

    private List<Film> searchFilmsByTitleAndDirector(String query) {
        log.info(String.format("Search films by title = %s and director = %s", query, query));

        String searchQuery = "SELECT * " +
                "FROM FILMS F1 " +
                "WHERE F1.NAME ILIKE ? " +
                "UNION " +
                "SELECT F2.FILM_ID, " +
                "   F2.NAME, " +
                "   F2.DESCRIPTION," +
                "   F2.DURATION, " +
                "   F2.LIKES, " +
                "   F2.RATE, " +
                "   F2.RELEASE_DATE, " +
                "   F2.RATING_ID " +
                "FROM FILMS F2 " +
                "JOIN FILM_DIRECTORS  FD ON F2.FILM_ID = FD.FILM_ID " +
                "JOIN DIRECTORS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "WHERE D.NAME ILIKE ? " +
                "ORDER BY LIKES DESC";

        return jdbcTemplate.query(searchQuery, this::makeFilm, "%" + query + "%", "%" + query + "%");
    }
}

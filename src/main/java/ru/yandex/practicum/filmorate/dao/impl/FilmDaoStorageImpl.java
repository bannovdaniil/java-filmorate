package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
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

        return jdbcTemplate.query(sql, this::makeFilm);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getLong("duration"));
        film.setRate(rs.getLong("rate"));
        film.setAverageRate(rs.getFloat("average_rate"));
        film.setMpa(new MpaRating(rs.getInt("rating_ID")));
        film.setLikes(rs.getLong("likes"));
        film.setDirectors(directorStorage.getDirectorsByFilm(film.getId()));
        film.setGenres(genreStorage.getFilmGenres(film.getId()));
        try {
            film.setMpa(mpaStorage.getRatingMpaById(film.getMpa().getId()));
        } catch (MpaRatingNotFound err) {
            throw new SQLException("MPA Rating Index not found in makeFilm");
        }

        return film;
    }

    @Override
    public Film create(FilmDto filmDto) throws MpaRatingNotFound, GenreNotFound, DirectorNotFoundException {
        String sql = "INSERT INTO FILMS (NAME, DESCRIPTION, DURATION, RELEASE_DATE, RATE, RATING_ID) " +
                " VALUES(? , ? , ? , ? , ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement prSt = connection.prepareStatement(
                            sql
                            , new String[]{"film_id"});
                    prSt.setString(1, filmDto.getName());
                    prSt.setString(2, filmDto.getDescription());
                    prSt.setLong(3, filmDto.getDuration());
                    prSt.setDate(4, Date.valueOf(filmDto.getReleaseDate()));
                    prSt.setLong(5, filmDto.getRate() == null ? 0 : filmDto.getRate());
                    prSt.setLong(6, filmDto.getMpa().getId());
                    return prSt;
                }
                , keyHolder);

        updateMpaRating(filmDto);
        updateGenresNameById(filmDto);

        Film film = dtoMapper.dtoToFilm(filmDto);
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
    public Film update(FilmDto filmDto) throws FilmNotFoundException, MpaRatingNotFound, GenreNotFound, DirectorNotFoundException {
        if (isFilmExist(filmDto.getId())) {
            String sql = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, DURATION = ?, RELEASE_DATE = ?, RATING_ID = ? " +
                    " WHERE FILM_ID = ? ";

            updateMpaRating(filmDto);
            jdbcTemplate.update(sql
                    , filmDto.getName()
                    , filmDto.getDescription()
                    , filmDto.getDuration()
                    , Date.valueOf(filmDto.getReleaseDate())
                    , filmDto.getMpa().getId()
                    , filmDto.getId()
            );
        } else {
            throw new FilmNotFoundException("Update failed, film not found.");
        }

        updateMpaRating(filmDto);
        updateGenresNameById(filmDto);

        Film film = dtoMapper.dtoToFilm(filmDto);
        saveGenreToDb(film);

        List<Director> directors = directorStorage.updateDirectorsOfFilm(film);
        film.setDirectors(directors);

        return film;
    }

    @Override
    public void remove(FilmDto filmDto) throws InvalidFilmRemoveException {
        if (isFilmExist(filmDto.getId())) {
            String sql = "DELETE FROM FILM_GENRES WHERE FILM_ID = ? ;";
            jdbcTemplate.update(sql, filmDto.getId());

            sql = "DELETE FROM LIKES WHERE FILM_ID = ? ;";
            jdbcTemplate.update(sql, filmDto.getId());

            sql = "DELETE FROM FILMS WHERE FILM_ID = ? ;";
            jdbcTemplate.update(sql, filmDto.getId());

            directorStorage.deleteDirectorsOfFilm(filmDto.getId());
        } else {
            throw new InvalidFilmRemoveException("Film for delete not found.");
        }
    }

    @Override
    public Film getFilmById(Long filmId) throws FilmNotFoundException, MpaRatingNotFound {
        if (isFilmExist(filmId)) {
            String sql = "SELECT * FROM FILMS WHERE FILM_ID = ?;";

            List<Film> films = jdbcTemplate.query(sql, this::makeFilm, filmId);

            return films.get(0);
        } else {
            throw new FilmNotFoundException("Film ID not found.");
        }
    }

    @Override
    public List<Film> getFilmTop(Long count, Integer genreId, Integer year) {
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

        return films;
    }

    @Override
    public boolean isFilmExist(long filmId) {
        String sql = "SELECT COUNT(*) FROM FILMS WHERE FILM_ID = ? ;";
        int filmCount = jdbcTemplate.queryForObject(sql, Integer.class, filmId);

        return filmCount > 0;
    }

    public void addLikeRate(long filmId, int rate) {
        String sql = "UPDATE FILMS " +
                " SET LIKES = LIKES + 1 , " +
                " RATE_SCORE = RATE_SCORE + ? ," +
                " AVERAGE_RATE = CAST(RATE_SCORE + ? AS FLOAT) / (LIKES + 1)" +
                " WHERE FILM_ID = ?; ";

        jdbcTemplate.update(sql, rate, rate, filmId);
    }

    public void reduceFilmLikeRate(long filmId, int rate) {
        String sql = "UPDATE FILMS " +
                " SET LIKES = LIKES - 1 , " +
                " RATE_SCORE = RATE_SCORE - ? ," +
                " AVERAGE_RATE = CASE " +
                "                    WHEN (LIKES - 1) = 0 THEN 0" +
                "                    ELSE CAST((RATE_SCORE - ?) AS FLOAT) / (LIKES - 1) " +
                "                END" +
                " WHERE FILM_ID = ?; ";

        jdbcTemplate.update(sql, rate, rate, filmId);
    }

    private void updateMpaRating(FilmDto filmDto) throws MpaRatingNotFound {
        filmDto.setMpa(mpaStorage.getRatingMpaById(filmDto.getMpa().getId()));
    }

    private void updateGenresNameById(FilmDto filmDto) throws GenreNotFound {
        if (filmDto.getGenres() == null) {
            return;
        }
        List<Genre> genresWithName = new ArrayList<>();
        Set<Integer> doubleId = new HashSet<>();
        for (Genre genre : filmDto.getGenres()) {
            if (!doubleId.contains(genre.getId())) {
                doubleId.add(genre.getId());
                genresWithName.add(genreStorage.getGenreById(genre.getId()));
            }
        }
        filmDto.setGenres(genresWithName);
    }

    private List<Film> getTopFilmByCountGenreYear(Long count, Integer genreId, Integer year) {
        String sql = "SELECT * FROM FILMS WHERE " +
                "FILM_ID IN (SELECT FILM_ID FROM FILM_GENRES WHERE GENRE_ID = ?) " +
                "AND YEAR(RELEASE_DATE) = ? " +
                "ORDER BY AVERAGE_RATE DESC " +
                "LIMIT ?;";

        return jdbcTemplate.query(sql, this::makeFilm, genreId, year, count);
    }

    private List<Film> getTopFilmByCountGenre(Long count, Integer genreId) {
        String sql = "SELECT * FROM FILMS WHERE " +
                "FILM_ID IN (SELECT FILM_ID FROM FILM_GENRES WHERE GENRE_ID = ?) " +
                "ORDER BY AVERAGE_RATE DESC " +
                "LIMIT ?;";

        return jdbcTemplate.query(sql, this::makeFilm, genreId, count);
    }

    private List<Film> getTopFilmByCountYear(Long count, Integer year) {
        String sql = "SELECT * FROM FILMS WHERE " +
                "YEAR(RELEASE_DATE) = ? " +
                "ORDER BY AVERAGE_RATE DESC " +
                "LIMIT ?;";

        return jdbcTemplate.query(sql, this::makeFilm, year, count);
    }

    private List<Film> getTopFilmByCount(Long count) {
        String sql = "SELECT * FROM FILMS " +
                "ORDER BY AVERAGE_RATE DESC " +
                "LIMIT ?;";

        return jdbcTemplate.query(sql, this::makeFilm, count);
    }

    @Override
    public List<Film> getFilmsByDirectorOrderByLikes(int id) throws DirectorNotFoundException {
        directorStorage.validateDirector(id);
        String sql = "SELECT * FROM FILMS F" +
                " WHERE F.FILM_ID IN (SELECT FD.FILM_ID FROM FILM_DIRECTORS FD WHERE FD.DIRECTOR_ID = ?)" +
                " ORDER BY F.AVERAGE_RATE DESC, F.FILM_ID";

        return jdbcTemplate.query(sql, this::makeFilm, id);
    }

    @Override
    public List<Film> getFilmsByDirectorOrderByDate(int id) throws DirectorNotFoundException {
        directorStorage.validateDirector(id);
        String sql = "SELECT * FROM FILMS F WHERE F.FILM_ID IN " +
                "(SELECT FD.FILM_ID FROM FILM_DIRECTORS FD WHERE FD.DIRECTOR_ID = ?) " +
                "ORDER BY F.RELEASE_DATE";

        return jdbcTemplate.query(sql, this::makeFilm, id);
    }

    /**
     * Map of FilmId => Rate by user
     */
    @Override
    public Map<Long, Integer> getUserFilmsRateFromLikes(long userId) {
        String sql = "SELECT FILM_ID, RATE FROM LIKES WHERE USER_ID = ? ;";

        Map<Long, Integer> filmsRate = jdbcTemplate.query(sql
                , (rs) -> {
                    Map<Long, Integer> result = new HashMap<>();
                    while (rs.next()) {
                        result.put(rs.getLong("FILM_ID"), rs.getInt("RATE"));
                    }
                    return result;
                }, userId);

        return filmsRate;
    }

    /**
     * List of userId
     */
    @Override
    public List<Long> getCrossFilmsUserFromLike(long userId) {
        String sql = "SELECT USER_ID FROM LIKES WHERE USER_ID <> ? " +
                " AND FILM_ID IN ( " +
                "  SELECT FILM_ID FROM LIKES WHERE USER_ID = ? " +
                " );";

        List<Long> crossUserIdList = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getLong("USER_ID"),
                userId, userId);

        return crossUserIdList;
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) throws UserNotFoundException {
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
                "ORDER BY AVERAGE_RATE DESC";

        List<Film> commonFilms = jdbcTemplate.query(sql, this::makeFilm, userId, friendId);

        return commonFilms;
    }

    public List<Film> searchFilms(String query, List<String> searchByParams) {
        List<Film> films;

        if (searchByParams.contains("title") && searchByParams.contains("director")) {
            films = searchFilmsByTitleAndDirector(query);
        } else if (searchByParams.contains("director")) {
            films = searchFilmsByDirector(query);
        } else {
            films = searchFilmsByTitle(query);
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
                "ORDER BY AVERAGE_RATE DESC";

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
                "ORDER BY AVERAGE_RATE DESC";

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
                "ORDER BY AVERAGE_RATE DESC";

        return jdbcTemplate.query(searchQuery, this::makeFilm, "%" + query + "%", "%" + query + "%");
    }
}

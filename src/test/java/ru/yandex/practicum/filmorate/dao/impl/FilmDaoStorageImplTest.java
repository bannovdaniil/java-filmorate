package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.mapper.DtoMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDaoStorageImplTest {
    private final FilmStorage filmStorage;
    private final DtoMapper dtoMapper;
    private final JdbcTemplate jdbcTemplate;

    private DtoFilm dtoFilm1;
    private DtoFilm dtoFilm2;
    private Film film1;
    private Film film2;

    private void clearDbFilms() {
        String sql = "DELETE FROM FILMS;";
        jdbcTemplate.update(sql);
    }

    @BeforeEach
    void beforeEach() {
        clearDbFilms();

        dtoFilm1 = new DtoFilm();
        dtoFilm1.setId(1L);
        dtoFilm1.setName("testFilm1");
        dtoFilm1.setDescription("description Of test film 1");
        dtoFilm1.setDuration(100L);
        dtoFilm1.setReleaseDate(LocalDate.of(2022, Month.JULY, 15));
        dtoFilm1.setRate(1L);
        dtoFilm1.setMpa(new MpaRating(1, "G"));

        film1 = dtoMapper.dtoToFilm(dtoFilm1);

        dtoFilm2 = new DtoFilm();
        dtoFilm2.setId(2L);
        dtoFilm2.setName("testFilm2");
        dtoFilm2.setDescription("description Of test film 2");
        dtoFilm2.setDuration(100L);
        dtoFilm2.setReleaseDate(LocalDate.of(2022, Month.JULY, 10));
        dtoFilm2.setRate(1L);
        dtoFilm2.setMpa(new MpaRating(2, "PG"));

        film2 = dtoMapper.dtoToFilm(dtoFilm2);

    }

    @AfterEach
    void afterEach() {
        clearDbFilms();
    }

    @DisplayName("Create Film")
    @Test
    public void createFilm() throws GenreNotFound, MpaRatingNotFound, MpaRatingNotValid, DirectorNotFoundException {
        Film filmResult = filmStorage.create(dtoFilm1);

        assertThat(filmResult).hasFieldOrPropertyWithValue("name", "testFilm1");
    }

    @DisplayName("Find Film by Id")
    @Test
    public void testFindFilmById() throws FilmNotFoundException, GenreNotFound, MpaRatingNotFound, MpaRatingNotValid, DirectorNotFoundException {
        Film film = filmStorage.create(dtoFilm1);

        long filmId = film.getId();

        Film filmResult = filmStorage.getFilmById(filmId);

        assertThat(film)
                .hasFieldOrPropertyWithValue("id", filmId)
                .hasFieldOrPropertyWithValue("name", "testFilm1");
    }

    @DisplayName("Update Film")
    @Test
    public void updateFilm() throws FilmNotFoundException, GenreNotFound, MpaRatingNotFound, MpaRatingNotValid, DirectorNotFoundException {
        Film filmResult = filmStorage.create(dtoFilm1);

        long filmId = filmResult.getId();
        dtoFilm1.setId(filmId);
        dtoFilm1.setName("EDITtestFilm1");

        filmStorage.update(dtoFilm1);

        filmResult = filmStorage.getFilmById(filmId);

        assertThat(filmResult).hasFieldOrPropertyWithValue("name", "EDITtestFilm1");
    }

    @DisplayName("Remove Film")
    @Test
    public void removeFilm() throws GenreNotFound, MpaRatingNotFound, MpaRatingNotValid, InvalidFilmRemoveException, DirectorNotFoundException {
        Film filmResult = filmStorage.create(dtoFilm1);

        long filmId = filmResult.getId();
        dtoFilm1.setId(filmId);

        filmStorage.remove(dtoFilm1);

        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class,
                () -> filmStorage.getFilmById(filmId)
        );

        Assertions.assertEquals("Film ID not found.", exception.getMessage());
    }

    @DisplayName("Find All Films")
    @Test
    public void findAllFilm() throws GenreNotFound, MpaRatingNotFound, MpaRatingNotValid, DirectorNotFoundException {
        Film filmResult1 = filmStorage.create(dtoFilm1);
        Film filmResult2 = filmStorage.create(dtoFilm2);

        List<Film> filmListExpected = List.of(film1, film2);
        List<Film> filmListResult = filmStorage.findAll();

        Assertions.assertEquals(filmListExpected, filmListResult);
    }

}
package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dto.FilmDto;
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

    private FilmDto filmDto1;
    private FilmDto filmDto2;
    private Film film1;
    private Film film2;

    private void clearDbFilms() {
        String sql = "DELETE FROM FILMS;";
        jdbcTemplate.update(sql);
    }

    @BeforeEach
    void beforeEach() {
        clearDbFilms();

        filmDto1 = new FilmDto();
        filmDto1.setId(1L);
        filmDto1.setName("testFilm1");
        filmDto1.setDescription("description Of test film 1");
        filmDto1.setDuration(100L);
        filmDto1.setReleaseDate(LocalDate.of(2022, Month.JULY, 15));
        filmDto1.setRate(1L);
        filmDto1.setMpa(new MpaRating(1, "G"));

        film1 = dtoMapper.dtoToFilm(filmDto1);

        filmDto2 = new FilmDto();
        filmDto2.setId(2L);
        filmDto2.setName("testFilm2");
        filmDto2.setDescription("description Of test film 2");
        filmDto2.setDuration(100L);
        filmDto2.setReleaseDate(LocalDate.of(2022, Month.JULY, 10));
        filmDto2.setRate(1L);
        filmDto2.setMpa(new MpaRating(2, "PG"));

        film2 = dtoMapper.dtoToFilm(filmDto2);

    }

    @AfterEach
    void afterEach() {
        clearDbFilms();
    }

    @DisplayName("Create Film")
    @Test
    public void createFilm() throws GenreNotFound, MpaRatingNotFound, MpaRatingNotValid, DirectorNotFoundException {
        Film filmResult = filmStorage.create(filmDto1);

        assertThat(filmResult).hasFieldOrPropertyWithValue("name", "testFilm1");
    }

    @DisplayName("Find Film by Id")
    @Test
    public void testFindFilmById() throws FilmNotFoundException, GenreNotFound, MpaRatingNotFound, MpaRatingNotValid, DirectorNotFoundException {
        Film film = filmStorage.create(filmDto1);

        long filmId = film.getId();

        Film filmResult = filmStorage.getFilmById(filmId);

        assertThat(film)
                .hasFieldOrPropertyWithValue("id", filmId)
                .hasFieldOrPropertyWithValue("name", "testFilm1");
    }

    @DisplayName("Update Film")
    @Test
    public void updateFilm() throws FilmNotFoundException, GenreNotFound, MpaRatingNotFound, MpaRatingNotValid, DirectorNotFoundException {
        Film filmResult = filmStorage.create(filmDto1);

        long filmId = filmResult.getId();
        filmDto1.setId(filmId);
        filmDto1.setName("EDITtestFilm1");

        filmStorage.update(filmDto1);

        filmResult = filmStorage.getFilmById(filmId);

        assertThat(filmResult).hasFieldOrPropertyWithValue("name", "EDITtestFilm1");
    }

    @DisplayName("Remove Film")
    @Test
    public void removeFilm() throws GenreNotFound, MpaRatingNotFound, MpaRatingNotValid, InvalidFilmRemoveException, DirectorNotFoundException {
        Film filmResult = filmStorage.create(filmDto1);

        long filmId = filmResult.getId();
        filmDto1.setId(filmId);

        filmStorage.remove(filmDto1);

        FilmNotFoundException exception = Assertions.assertThrows(FilmNotFoundException.class,
                () -> filmStorage.getFilmById(filmId)
        );

        Assertions.assertEquals("Film ID not found.", exception.getMessage());
    }

    @DisplayName("Find All Films")
    @Test
    public void findAllFilm() throws GenreNotFound, MpaRatingNotFound, MpaRatingNotValid, DirectorNotFoundException {
        Film filmResult1 = filmStorage.create(filmDto1);
        Film filmResult2 = filmStorage.create(filmDto2);

        List<Film> filmListExpected = List.of(film1, film2);
        List<Film> filmListResult = filmStorage.findAll();

        Assertions.assertEquals(filmListExpected, filmListResult);
    }

}
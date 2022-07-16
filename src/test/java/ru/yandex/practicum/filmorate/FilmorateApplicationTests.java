package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.mapper.DtoMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final DtoMapper dtoMapper;
    private final JdbcTemplate jdbcTemplate;

    private DtoUser dtoUser1;
    private DtoUser dtoUser2;
    private User user1;
    private User user2;
    private DtoFilm dtoFilm1;
    private DtoFilm dtoFilm2;
    private Film film1;
    private Film film2;

    private void clearDb() {
        String sql = "DELETE FROM USERS; " +
                " DELETE FROM FILMS;";
        jdbcTemplate.update(sql);
    }

    @BeforeEach
    void beforeEach() {
        clearDb();
        dtoUser1 = new DtoUser();
        dtoUser1.setId(1);
        dtoUser1.setEmail("testemail1@mail.ru");
        dtoUser1.setLogin("testlogin1");
        dtoUser1.setName("testname1");
        dtoUser1.setBirthday(LocalDate.of(1990, Month.FEBRUARY, 10));

        user1 = dtoMapper.dtoToUser(dtoUser1);

        dtoUser2 = new DtoUser();
        dtoUser2.setId(2);
        dtoUser2.setEmail("testemail2@mail.ru");
        dtoUser2.setLogin("testlogin2");
        dtoUser2.setName("testname2");
        dtoUser2.setBirthday(LocalDate.of(1991, Month.FEBRUARY, 10));

        user2 = dtoMapper.dtoToUser(dtoUser2);

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
        clearDb();
    }

    @DisplayName("Create User")
    @Test
    public void createUser() throws UserAlreadyExistException, InvalidEmailException {
        User userResult = userStorage.create(dtoUser1);

        assertThat(userResult).hasFieldOrPropertyWithValue("email", "testemail1@mail.ru");
    }

    @DisplayName("Find User by Id")
    @Test
    public void testFindUserById() throws UserNotFoundException, UserAlreadyExistException, InvalidEmailException {
        User user = userStorage.create(dtoUser1);

        long userId = user.getId();

        User userResult = userStorage.getUserById(userId);

        assertThat(user)
                .hasFieldOrPropertyWithValue("id", userId)
                .hasFieldOrPropertyWithValue("email", "testemail1@mail.ru");
    }

    @DisplayName("Update User")
    @Test
    public void updateUser() throws UserNotFoundException, UserAlreadyExistException, InvalidEmailException {
        User userResult = userStorage.create(dtoUser1);

        long userId = userResult.getId();
        dtoUser1.setId(userId);
        dtoUser1.setEmail("editemail@mail.ru");

        userStorage.update(dtoUser1);

        userResult = userStorage.getUserById(userId);

        assertThat(userResult).hasFieldOrPropertyWithValue("email", "editemail@mail.ru");
    }

    @DisplayName("Remove User")
    @Test
    public void removeUser() throws UserAlreadyExistException, InvalidEmailException, UserRemoveException {
        User userResult = userStorage.create(dtoUser1);

        long userId = userResult.getId();
        dtoUser1.setId(userId);

        userStorage.remove(dtoUser1);

        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> userStorage.getUserById(userId)
        );

        Assertions.assertEquals("User Not Found By Id", exception.getMessage());
    }

    @DisplayName("Find All Users")
    @Test
    public void findAllUser() throws UserAlreadyExistException, InvalidEmailException {
        User userResult1 = userStorage.create(dtoUser1);
        User userResult2 = userStorage.create(dtoUser2);

        List<User> userListExpected = List.of(user1, user2);
        List<User> userListResult = userStorage.findAll();

        Assertions.assertEquals(userListExpected, userListResult);
    }


    @DisplayName("Create Film")
    @Test
    public void createFilm() throws InvalidFilmException, UserAlreadyExistException, GenreNotFound, MpaRatingNotFound, MpaRatingNotValid {
        Film filmResult = filmStorage.create(dtoFilm1);

        assertThat(filmResult).hasFieldOrPropertyWithValue("name", "testFilm1");
    }

    @DisplayName("Find Film by Id")
    @Test
    public void testFindFilmById() throws FilmNotFoundException, InvalidFilmException, UserAlreadyExistException, GenreNotFound, MpaRatingNotFound, MpaRatingNotValid {
        Film film = filmStorage.create(dtoFilm1);

        long filmId = film.getId();

        Film filmResult = filmStorage.getFilmById(filmId);

        assertThat(film)
                .hasFieldOrPropertyWithValue("id", filmId)
                .hasFieldOrPropertyWithValue("name", "testFilm1");
    }

    @DisplayName("Update Film")
    @Test
    public void updateFilm() throws FilmNotFoundException, InvalidFilmException, UserAlreadyExistException, GenreNotFound, MpaRatingNotFound, MpaRatingNotValid {
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
    public void removeFilm() throws InvalidFilmException, UserAlreadyExistException, GenreNotFound, MpaRatingNotFound, MpaRatingNotValid, InvalidFilmRemoveException {
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
    public void findAllFilm() throws InvalidFilmException, UserAlreadyExistException, GenreNotFound, MpaRatingNotFound, MpaRatingNotValid {
        Film filmResult1 = filmStorage.create(dtoFilm1);
        Film filmResult2 = filmStorage.create(dtoFilm2);

        List<Film> filmListExpected = List.of(film1, film2);
        List<Film> filmListResult = filmStorage.findAll();

        Assertions.assertEquals(filmListExpected, filmListResult);
    }

    @Test
    void contextLoads() {
    }

}

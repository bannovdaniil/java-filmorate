package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserRemoveException;
import ru.yandex.practicum.filmorate.mapper.DtoMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDaoStorageImplTest {
    private final UserStorage userStorage;
    private final DtoMapper dtoMapper;
    private final JdbcTemplate jdbcTemplate;

    private DtoUser dtoUser1;
    private DtoUser dtoUser2;
    private User user1;
    private User user2;

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
    }

    @AfterEach
    void afterEach() {
        clearDb();
    }

    @DisplayName("Create User")
    @Test
    public void createUser() {
        User userResult = userStorage.create(dtoUser1);

        assertThat(userResult).hasFieldOrPropertyWithValue("email", "testemail1@mail.ru");
    }

    @DisplayName("Find User by Id")
    @Test
    public void testFindUserById() throws UserNotFoundException {
        User user = userStorage.create(dtoUser1);

        long userId = user.getId();

        User userResult = userStorage.getUserById(userId);

        assertThat(user)
                .hasFieldOrPropertyWithValue("id", userId)
                .hasFieldOrPropertyWithValue("email", "testemail1@mail.ru");
    }

    @DisplayName("Update User")
    @Test
    public void updateUser() throws UserNotFoundException {
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
    public void removeUser() throws UserRemoveException {
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
    public void findAllUser() {
        User userResult1 = userStorage.create(dtoUser1);
        User userResult2 = userStorage.create(dtoUser2);

        List<User> userListExpected = List.of(user1, user2);
        List<User> userListResult = userStorage.findAll();

        Assertions.assertEquals(userListExpected, userListResult);
    }

}
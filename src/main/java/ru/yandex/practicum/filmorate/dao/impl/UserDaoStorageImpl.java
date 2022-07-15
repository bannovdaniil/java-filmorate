package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.exceptions.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserRemoveException;
import ru.yandex.practicum.filmorate.mapper.DtoMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDaoStorageImpl implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DtoMapper dtoMapper;

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("USER_ID"));
        user.setEmail(rs.getString("EMAIL"));
        user.setLogin(rs.getString("LOGIN"));
        user.setName(rs.getString("NAME"));
        user.setBirthday(rs.getDate("BIRTHDAY").toLocalDate());
        return user;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM USERS;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User create(DtoUser dtoUser) throws InvalidEmailException, UserAlreadyExistException {
        String sql = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) " +
                " VALUES(? , ? , ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement prSt = connection.prepareStatement(
                            sql
                            , new String[]{"user_id"});
                    prSt.setString(1, dtoUser.getEmail());
                    prSt.setString(2, dtoUser.getLogin());
                    prSt.setString(3, dtoUser.getName());
                    prSt.setDate(4, Date.valueOf(dtoUser.getBirthday()));
                    return prSt;
                }
                , keyHolder);
        dtoUser.setId(keyHolder.getKey().longValue());

        return dtoMapper.dtoToUser(dtoUser);
    }

    @Override
    public User update(DtoUser dtoUser) throws UserNotFoundException {
        if (isUserExist(dtoUser.getId())) {
            String sql = "UPDATE USERS SET EMAIL = ? , LOGIN = ? , NAME = ? , BIRTHDAY = ? " +
                    " WHERE USER_ID = ? ;";

            jdbcTemplate.update(sql
                    , dtoUser.getEmail()
                    , dtoUser.getLogin()
                    , dtoUser.getName()
                    , Date.valueOf(dtoUser.getBirthday())
                    , dtoUser.getId()
            );

            return dtoMapper.dtoToUser(dtoUser);
        } else {
            throw new UserNotFoundException("Update failed, user not found.");
        }
    }

    private boolean isUserExist(long userId) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE USER_ID = ? ;";
        Integer countUser = jdbcTemplate.queryForObject(sql, Integer.class, userId);

        return countUser > 0;
    }

    @Override
    public void remove(DtoUser dtoUser) throws UserRemoveException {
        if (isUserExist(dtoUser.getId())) {
            String sql = "DELETE FROM users WHERE user_id = ?";
            jdbcTemplate.update(sql, dtoUser.getId());
        } else {
            throw new UserRemoveException("User for delete not found.");
        }
    }

    @Override
    public User getUserById(Long userId) throws UserNotFoundException {
        String sql = "SELECT * FROM USERS WHERE USER_ID = ? ;";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId);
        if (users.size() < 1) {
            throw new UserNotFoundException("User Not Found By Id");
        }
        return users.get(0);
    }

    /**
     * Возможно, когда-нибудь в следующей жизни логика будет чуть сложнее
     * и у друга будет появляться запрос на дружбу.
     * jdbcTemplate.update(sql, friendId, userId, "REQUEST");
     */
    @Override
    public void addFriend(Long userId, Long friendId) throws UserNotFoundException {
        if (!isUserExist(userId) || !isUserExist(friendId)) {
            throw new UserNotFoundException("One of users ID not found.");
        }

        String sql = "INSERT INTO FRIENDS(USER_ID, FRIEND_ID, STATUS) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, "CONFIRMED");
    }

    @Override
    public void removeFriend(Long userId, Long friendId) throws UserNotFoundException, UserRemoveException {
        if (!isUserExist(userId) || !isUserExist(friendId)) {
            throw new UserNotFoundException("One of users ID not found.");
        }

        String sql = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?;";

        boolean isDelete = jdbcTemplate.update(sql, userId, friendId) < 1;

        if (isDelete) {
            throw new UserRemoveException("Can't delete friend.");
        }
    }

    @Override
    public List<User> getFriendList(Long userId) throws UserNotFoundException {
        if (!isUserExist(userId)) {
            throw new UserNotFoundException("Users ID not found.");
        }

        String sql = "SELECT * FROM USERS " +
                " WHERE USER_ID IN " +
                " (SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ? AND STATUS = 'CONFIRMED');";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId);
    }

    @Override
    public List<User> getCrossFriendList(Long userId, Long otherId) throws UserNotFoundException {
        if (!isUserExist(userId) || !isUserExist(otherId)) {
            throw new UserNotFoundException("One of users ID not found.");
        }

        String sql = "SELECT * FROM USERS WHERE USER_ID IN( " +
                "SELECT DISTINCT(FRIEND_ID) FROM FRIENDS WHERE USER_ID = ? AND STATUS = 'CONFIRMED' " +
                " AND FRIEND_ID IN (SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ? AND STATUS = 'CONFIRMED')" +
                " );";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId, otherId);
    }
}
package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserRemoveException;
import ru.yandex.practicum.filmorate.mapper.DtoMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    public User create(UserDto userDto) {
        String sql = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) " +
                " VALUES(? , ? , ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement prSt = connection.prepareStatement(
                            sql
                            , new String[]{"user_id"});
                    prSt.setString(1, userDto.getEmail());
                    prSt.setString(2, userDto.getLogin());
                    prSt.setString(3, userDto.getName());
                    prSt.setDate(4, Date.valueOf(userDto.getBirthday()));
                    return prSt;
                }
                , keyHolder);
        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return dtoMapper.dtoToUser(userDto);
    }

    @Override
    public User update(UserDto userDto) throws UserNotFoundException {
        if (isUserExist(userDto.getId())) {
            String sql = "UPDATE USERS SET EMAIL = ? , LOGIN = ? , NAME = ? , BIRTHDAY = ? " +
                    " WHERE USER_ID = ? ;";

            jdbcTemplate.update(sql
                    , userDto.getEmail()
                    , userDto.getLogin()
                    , userDto.getName()
                    , Date.valueOf(userDto.getBirthday())
                    , userDto.getId()
            );

            return dtoMapper.dtoToUser(userDto);
        } else {
            throw new UserNotFoundException("Update failed, user not found.");
        }
    }

    @Override
    public boolean isUserExist(long userId) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE USER_ID = ? ;";
        int countUser = jdbcTemplate.queryForObject(sql, Integer.class, userId);

        return countUser > 0;
    }

    @Override
    public void remove(UserDto userDto) throws UserRemoveException {
        if (isUserExist(userDto.getId())) {
            String sql = "DELETE FROM users WHERE user_id = ?";
            jdbcTemplate.update(sql, userDto.getId());
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

    @Override
    public void removeUserById(Long userId) throws UserRemoveException {
        if (isUserExist(userId)) {
            int paramsCount = 7;
            Long[] params = new Long[paramsCount];
            Arrays.fill(params, userId);

            String sql = "DELETE FROM friends WHERE user_id=?; " +
                    "DELETE FROM friends WHERE friend_id=?; " +
                    "DELETE FROM user_events WHERE user_id=?; " +
                    "DELETE FROM reviews WHERE user_id=?; " +
                    "DELETE FROM review_likes WHERE user_id=?; " +
                    "DELETE FROM likes WHERE user_id=?; " +
                    "DELETE FROM users WHERE user_id=?;";
            jdbcTemplate.update(sql, params);
        } else {
            throw new UserRemoveException("User for delete not found.");
        }
    }
}

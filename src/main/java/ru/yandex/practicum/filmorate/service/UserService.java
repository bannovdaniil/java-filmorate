package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.exceptions.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserRemoveException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(DtoUser dtoUser) throws InvalidEmailException, UserAlreadyExistException {
        return userStorage.create(dtoUser);
    }

    public User update(DtoUser dtoUser) throws InvalidEmailException, UserAlreadyExistException {
        return userStorage.update(dtoUser);
    }

    public void remove(DtoUser dtoUser) throws UserRemoveException {
        userStorage.remove(dtoUser);
    }

    public User getUserById(Long userId) throws UserNotFoundException {
        return userStorage.getUserById(userId);
    }

    public void addFriend(Long userId, Long friendId) throws UserNotFoundException {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
    }

    public void removeFriend(Long userId, Long friendId) throws UserNotFoundException, UserRemoveException {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user.removeFriend(friendId)) {
            throw new UserRemoveException("Can't delete friend.");
        }
        if (friend.removeFriend(userId)) {
            throw new UserRemoveException("Can't delete friend.");
        }
    }

    private Set<Long> getFriendIdList(User user) {
        return user.getFriends();
    }

    public List<User> getFriendList(Long userId) throws UserNotFoundException {
        User user = userStorage.getUserById(userId);
        List<User> resultFriendsList = new ArrayList<>();
        Set<Long> friendsIds = getFriendIdList(user);

        for (Long friendId : friendsIds) {
            resultFriendsList.add(userStorage.getUserById(friendId));
        }

        return resultFriendsList;
    }

    public List<User> getCrossFriendList(Long userId, Long otherId) throws UserNotFoundException {
        User user = userStorage.getUserById(userId);
        User other = userStorage.getUserById(otherId);

        Set<Long> friendsIdsUnion = new HashSet<>(getFriendIdList(user));
        friendsIdsUnion.retainAll(getFriendIdList(other));

        List<User> resultFriendsList = new ArrayList<>();

        for (Long friendId : friendsIdsUnion) {
            resultFriendsList.add(userStorage.getUserById(friendId));
        }

        return resultFriendsList;
    }
}

package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserRemoveException;
import ru.yandex.practicum.filmorate.exceptions.UserGetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) throws UserGetException {
        User user = userStorage.getUserById(userId);
        if (userStorage.getUserById(friendId) != null) {
            user.addFriend(friendId);
        }
    }

    public void removeFriend(Long userId, Long friendId) throws UserGetException, UserRemoveException {
        User user = userStorage.getUserById(userId);
        if (userStorage.getUserById(friendId) != null) {
            if (!user.removeFriend(friendId)) {
                throw new UserRemoveException("Can't delete friend.");
            }
        }
    }

    private Set<Long> getFriendIdList(User user) {
        return user.getFriends();
    }

    public List<User> getFriendList(Long userId) throws UserGetException {
        User user = userStorage.getUserById(userId);
        List<User> resultFriendsList = new ArrayList<>();
        Set<Long> friendsIds = getFriendIdList(user);

        for (Long friendId : friendsIds) {
            resultFriendsList.add(userStorage.getUserById(friendId));
        }

        return resultFriendsList;
    }

    public List<User> getCrossFriendList(Long userId, Long otherId) throws UserGetException {
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

package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

@Service
public class UserService {
    public void addFriend(User user, long id) {
        user.addFriend(id);
    }

    public void removeFriend(User user, long id) {
        user.removeFriend(id);
    }

    public Set<Long> getAllFriendList(User user) {
        return user.getFriends();
    }
}

package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * целочисленный идентификатор — id;
 * электронная почта — email;
 * логин пользователя — login;
 * имя для отображения — name;
 * дата рождения — birthday.
 */

@Getter
@Setter
@ToString
public class User {
    private long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();

    public void addFriend(long id) {
        friends.add(id);
    }

    public boolean isFriend(long id) {
        return friends.contains(id);
    }

    public void removeFriend(long id) {
        if (isFriend(id)) {
            friends.remove(id);
        }
    }

    public Set<Long> getFriends() {
        return friends;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return email != null ? email.equals(user.email) : user.email == null;
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }
}

package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * целочисленный идентификатор — id;
 * название — name;
 * описание — description;
 * дата релиза — releaseDate;
 * продолжительность фильма — duration.
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private Long filmId;
    private String name;
    private String description;
    private Long duration;
    private LocalDate releaseDate;
    private Long likes;
    private String rating;
    private List<String> genres;
    private Set<Long> userLikes = new HashSet<>();

    public Long getRate() {
        return likes;
    }

    public void addLike(long userId) {
        userLikes.add(userId);
        likes++;
    }

    public boolean isLike(long userId) {
        return userLikes.contains(userId);
    }

    public boolean removeLike(long userId) {
        boolean isRemove = isLike(userId);
        if (isRemove) {
            userLikes.remove(userId);
            likes--;
            isRemove = isLike(userId);
        }
        return isRemove;
    }


    public Set<Long> getLikes() {
        return userLikes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Film film = (Film) o;

        if (filmId != null ? !filmId.equals(film.filmId) : film.filmId != null) return false;
        if (name != null ? !name.equals(film.name) : film.name != null) return false;
        if (description != null ? !description.equals(film.description) : film.description != null) return false;
        if (duration != null ? !duration.equals(film.duration) : film.duration != null) return false;
        return releaseDate != null ? releaseDate.equals(film.releaseDate) : film.releaseDate == null;
    }

    @Override
    public int hashCode() {
        int result = filmId != null ? filmId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (releaseDate != null ? releaseDate.hashCode() : 0);
        return result;
    }
}

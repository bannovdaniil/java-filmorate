package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

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

    public Long getRate() {
        return likes;
    }

    public void addLike() {
        likes++;
    }

    public boolean removeLike() {
        boolean isRemove = false;
        if (likes > 0) {
            likes--;
            isRemove = true;
        }
        return isRemove;
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

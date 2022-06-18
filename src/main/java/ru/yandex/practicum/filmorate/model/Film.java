package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

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
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Film film = (Film) o;

        if (duration != film.duration) return false;
        if (name != null ? !name.equals(film.name) : film.name != null) return false;
        if (description != null ? !description.equals(film.description) : film.description != null) return false;
        return releaseDate != null ? releaseDate.equals(film.releaseDate) : film.releaseDate == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (releaseDate != null ? releaseDate.hashCode() : 0);
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        return result;
    }
}

package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.dto.DtoDirector;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director create(DtoDirector dtoDirector);
    Director update(DtoDirector dtoDirector);
    void delete(int id);
    Director getById(int id);
    List<Director> getAll();

    List<Director> getDirectorsByFilm(Long film_id);

    List<Director> saveDirectorsOfFilm(Long film_id, List<Director> directors);

    List<Director> updateDirectorsOfFilm(Long film_id, List<Director> directors);

    void deleteDirectorsOfFilm(Long film_id);

    boolean isExists(int id);

    List<Long> getFilmsByDirectorOrderByLikes(int id);

    List<Long> getFilmsByDirectorOrderByDate(int id);
}

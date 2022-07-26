package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {
    Director create(DirectorDto directorDto) throws DirectorNotFoundException;

    Director update(DirectorDto directorDto) throws DirectorNotFoundException;

    void delete(int id) throws DirectorNotFoundException;

    Director getById(int id) throws DirectorNotFoundException;

    List<Director> getAll();

    List<Director> getDirectorsByFilm(Long film_id);

    List<Director> saveDirectorsOfFilm(Film film) throws DirectorNotFoundException;

    List<Director> updateDirectorsOfFilm(Film film) throws DirectorNotFoundException;

    void deleteDirectorsOfFilm(Long film_id);

    void validateDirector(int id) throws DirectorNotFoundException;
}

package ru.yandex.practicum.filmorate.dao;


import ru.yandex.practicum.filmorate.exceptions.GenreNotFound;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreStorage {
    int indexOfValue(String genre) throws GenreNotFound;

    Genre getGenreById(int genreId) throws GenreNotFound;

    Collection<Genre> findAll();
}

package ru.yandex.practicum.filmorate.dao;


import ru.yandex.practicum.filmorate.exceptions.RatingNotFound;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface MpaStorage {
    int indexOfValue(String rating) throws RatingNotFound;

    MpaRating getRatingMpaById(int ratingId) throws RatingNotFound;

    Collection<MpaRating> findAll();
}

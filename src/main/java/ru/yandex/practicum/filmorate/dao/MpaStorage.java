package ru.yandex.practicum.filmorate.dao;


import ru.yandex.practicum.filmorate.exceptions.MpaRatingNotFound;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface MpaStorage {
    int indexOfValue(String rating) throws MpaRatingNotFound;

    MpaRating getRatingMpaById(int ratingId) throws MpaRatingNotFound;

    Collection<MpaRating> findAll();
}

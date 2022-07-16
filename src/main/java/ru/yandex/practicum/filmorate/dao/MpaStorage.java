package ru.yandex.practicum.filmorate.dao;


import ru.yandex.practicum.filmorate.exceptions.MpaRatingNotFound;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaStorage {
    int indexOfValue(String rating) throws MpaRatingNotFound;

    MpaRating getRatingMpaById(int ratingId) throws MpaRatingNotFound;

    List<MpaRating> findAll();
}

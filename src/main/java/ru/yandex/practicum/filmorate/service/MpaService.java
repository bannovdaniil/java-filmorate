package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.exceptions.RatingNotFound;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public long indexOfValue(String rating) throws RatingNotFound {
        return mpaStorage.indexOfValue(rating);
    }

    public MpaRating getRatingMpaById(int ratingId) throws RatingNotFound {
        return mpaStorage.getRatingMpaById(ratingId);
    }

    public Collection<MpaRating> findAll() {
        return mpaStorage.findAll();
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.exceptions.MpaRatingNotFound;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public long indexOfValue(String rating) throws MpaRatingNotFound {
        return mpaStorage.indexOfValue(rating);
    }

    public MpaRating getRatingMpaById(int ratingId) throws MpaRatingNotFound {
        return mpaStorage.getRatingMpaById(ratingId);
    }

    public Collection<MpaRating> findAll() {
        return mpaStorage.findAll();
    }
}

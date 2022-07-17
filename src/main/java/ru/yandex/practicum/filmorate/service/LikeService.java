package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.LikeStorage;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeStorage likeStorage;

    public void addLike(final long film_id, final long user_id) throws FilmNotFoundException, UserNotFoundException {
        likeStorage.addLike(film_id, user_id);
    }

    public void removeLike(final long film_id, final long user_id) throws FilmNotFoundException, UserNotFoundException {
        likeStorage.removeLike(film_id, user_id);
    }

}

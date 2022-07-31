package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constant.FilmRate;
import ru.yandex.practicum.filmorate.dao.FilmLikeStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmLikeStorage filmLikeStorage;
    private final EventService eventService;

    public List<Film> findAll() throws MpaRatingNotFound {
        return filmStorage.findAll();
    }

    public Film create(FilmDto filmDto) throws MpaRatingNotFound, GenreNotFound, DirectorNotFoundException {
        return filmStorage.create(filmDto);
    }

    public Film update(FilmDto filmDto) throws FilmNotFoundException, MpaRatingNotFound, GenreNotFound, DirectorNotFoundException {
        return filmStorage.update(filmDto);
    }

    public void remove(FilmDto filmDto) throws InvalidFilmRemoveException {
        filmStorage.remove(filmDto);

    }

    public Film getFilmById(Long filmId) throws FilmNotFoundException, MpaRatingNotFound {
        return filmStorage.getFilmById(filmId);
    }

    /**
     * при добавлении лайка, надо проверить, нет ли его уже в базе (будет ошибка дубликата PK)
     * если есть то делаем update
     */
    public void addLike(Long filmId, Long userId, Integer rate) throws FilmNotFoundException, UserNotFoundException, RequestParamNotValid {
        if (filmLikeStorage.getUserLikeCount(filmId, userId) == 0) {
            filmLikeStorage.addLike(filmId, userId, rate);
            filmStorage.addLikeRate(filmId, rate);
            eventService.addEvent(userId, EventType.LIKE, EventOperation.ADD, filmId);
        } else {
            int oldRate = filmLikeStorage.getUserLikeRate(filmId, userId);
            filmStorage.reduceFilmLikeRate(filmId, oldRate);
            filmLikeStorage.removeLike(filmId, userId);

            filmLikeStorage.addLike(filmId, userId, rate);
            filmStorage.addLikeRate(filmId, rate);
        }
    }

    public void removeLike(Long filmId, Long userId) throws FilmNotFoundException, UserNotFoundException {
        int rate = filmLikeStorage.getUserLikeRate(filmId, userId);
        filmStorage.reduceFilmLikeRate(filmId, rate);
        filmLikeStorage.removeLike(filmId, userId);

        eventService.addEvent(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
    }

    public List<Film> getFilmTop(Long count, Integer genreId, Integer year) throws MpaRatingNotFound {
        return filmStorage.getFilmTop(count, genreId, year);
    }

    public List<Film> getFilmsByDirectorsSorted(int id, String sortBy) throws MpaRatingNotFound,
            RequestParamNotValid, DirectorNotFoundException {
        if (sortBy.equals("year")) {
            return filmStorage.getFilmsByDirectorOrderByDate(id);
        } else if (sortBy.equals("likes")) {
            return filmStorage.getFilmsByDirectorOrderByLikes(id);
        } else {
            throw new RequestParamNotValid("Параметр запроса неправильный: " + sortBy);
        }
    }


    /**
     * Ищем пересечения пользователей
     * положительные рекомендации то что больше 5, значит все что меньше не рекомендуем
     */
    public List<Film> getRecommendations(int userId) throws MpaRatingNotFound, FilmNotFoundException {
        Map<Long, Integer> userFilmsRate = filmStorage.getUserFilmsRateFromLikes(userId);
        List<Long> crossFilmsUserFromLike = filmStorage.getCrossFilmsUserFromLike(userId);

        List<Film> recommendationFilms = new ArrayList<>();

        for (Long crossUserId : crossFilmsUserFromLike) {
            Map<Long, Integer> crossFilmRate = filmStorage.getUserFilmsRateFromLikes(crossUserId);
            if (countUserCrossFilm(crossFilmRate, userFilmsRate) == 0) {
                continue;
            }

            for (Map.Entry<Long, Integer> filmRate : crossFilmRate.entrySet()) {
                long filmId = filmRate.getKey();
                int rate = filmRate.getValue();

                if (rate >= FilmRate.FILM_RATE_AV && !userFilmsRate.containsKey(filmId)) {
                    recommendationFilms.add(getFilmById(filmId));
                }
            }
        }
        return recommendationFilms;
    }

    /**
     * Считаем фильмы, которые совпали по рейтингам
     * 1-5 - не понравился
     * 6-10 понравился
     * delta... дельта в оценках delta = +-FILM_RATE_DELTA;
     */
    private int countUserCrossFilm(Map<Long, Integer> crossFilmRate, Map<Long, Integer> userFilmsRate) {
        int countCrossFilm = 0;

        for (Map.Entry<Long, Integer> filmRateEntry : userFilmsRate.entrySet()) {
            long filmId = filmRateEntry.getKey();
            if (crossFilmRate.containsKey(filmId)) {
                int originRateLo = Math.max(filmRateEntry.getValue() - FilmRate.FILM_RATE_DELTA, FilmRate.FILM_RATE_LO);
                originRateLo = Math.min(originRateLo, FilmRate.FILM_RATE_AV - 1);

                int originRateHi = Math.min(filmRateEntry.getValue() + FilmRate.FILM_RATE_DELTA, FilmRate.FILM_RATE_HI);
                originRateHi = Math.max(originRateHi, FilmRate.FILM_RATE_AV);

                int crossRate = crossFilmRate.get(filmId);

                if (originRateLo >= crossRate && crossRate <= originRateHi) {
                    countCrossFilm++;
                }
            }
        }
        return countCrossFilm;
    }

    public void removeFilmById(Long filmId) throws FilmNotFoundException {
        filmStorage.removeFilmById(filmId);
    }

    public List<Film> getCommonFilms(long userId, long friendId) throws UserNotFoundException, MpaRatingNotFound {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> searchFilms(String query, List<String> searchByParams) throws MpaRatingNotFound {
        return filmStorage.searchFilms(query, searchByParams);
    }
}

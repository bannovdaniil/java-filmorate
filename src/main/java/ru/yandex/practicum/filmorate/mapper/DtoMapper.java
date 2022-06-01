package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

public class DtoMapper {
    public static Film dtoToFilm(DtoFilm dtoFilm) {
        Film film = new Film();
        film.setId(dtoFilm.getId());
        film.setName(dtoFilm.getName());
        film.setDescription(dtoFilm.getDescription());
        film.setReleaseDate(dtoFilm.getReleaseDate());
        film.setDuration(dtoFilm.getDuration());

        return film;
    }

    public static User dtoToUser(DtoUser dtoUser) {
        User user = new User();
        user.setId(dtoUser.getId());
        user.setEmail(dtoUser.getEmail());
        user.setLogin(dtoUser.getLogin());
        user.setName(dtoUser.getName());
        user.setBirthday(dtoUser.getBirthday());

        return user;
    }


}

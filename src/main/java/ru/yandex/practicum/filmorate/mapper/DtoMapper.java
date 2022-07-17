package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

@Component
public class DtoMapper {
    public Film dtoToFilm(DtoFilm dtoFilm) {
        Film film = new Film();
        film.setId(dtoFilm.getId());
        film.setName(dtoFilm.getName());
        film.setDescription(dtoFilm.getDescription());
        film.setDuration(dtoFilm.getDuration());
        film.setReleaseDate(dtoFilm.getReleaseDate());
        film.setRate(dtoFilm.getRate());
        film.setMpa(dtoFilm.getMpa());
        film.setGenres(dtoFilm.getGenres());

        return film;
    }

    public User dtoToUser(DtoUser dtoUser) {
        User user = new User();
        user.setId(dtoUser.getId());
        user.setEmail(dtoUser.getEmail());
        user.setLogin(dtoUser.getLogin());
        user.setName(dtoUser.getName());
        user.setBirthday(dtoUser.getBirthday());

        return user;
    }


}

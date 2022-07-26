package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;

@Component
public class DtoMapper {
    public Film dtoToFilm(FilmDto filmDto) {
        Film film = new Film();
        film.setId(filmDto.getId());
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setDuration(filmDto.getDuration());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setRate(filmDto.getRate());
        film.setMpa(filmDto.getMpa());
        film.setGenres(filmDto.getGenres());
        film.setDirectors(filmDto.getDirectors() == null ? new ArrayList<>() : filmDto.getDirectors());

        return film;
    }

    public User dtoToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setLogin(userDto.getLogin());
        user.setName(userDto.getName());
        user.setBirthday(userDto.getBirthday());

        return user;
    }


}

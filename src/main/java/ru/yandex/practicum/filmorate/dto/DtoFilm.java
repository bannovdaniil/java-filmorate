package ru.yandex.practicum.filmorate.dto;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.validation.FilmDataChecker;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class DtoFilm {
    private Long id;
    @NotBlank(message = "name should not be blank")
    private String name;
    @FilmDataChecker
    private LocalDate releaseDate;
    @Size(max = 200, message = "Description length must be less then 200")
    private String description;
    @Min(value = 0, message = "Duration must be greater then 0")
    private Long duration;
    private Long rate;
    @NotNull(message = "Не задано значение MPA.")
    private MpaRating mpa;
    private List<Genre> genres;
    private List<Director> directors;
}

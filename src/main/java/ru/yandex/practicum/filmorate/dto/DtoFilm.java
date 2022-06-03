package ru.yandex.practicum.filmorate.dto;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.constant.FilmCheckDate;
import ru.yandex.practicum.filmorate.validation.FilmDataChecker;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
public class DtoFilm {
    private int id;
    @NotBlank(message = "name should not be blank")
    private String name;
    @Size(max = 200, message = "Description length must be less then 200")
    private String description;
    @FilmDataChecker
    private LocalDate releaseDate;
    @Min(value = 0, message = "Duration must be greater then 0")
    private long duration;
}

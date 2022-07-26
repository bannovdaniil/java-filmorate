package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DirectorDto {
    private int id;
    @NotNull
    @NotBlank(message = "name should not be blank")
    private String name;
}

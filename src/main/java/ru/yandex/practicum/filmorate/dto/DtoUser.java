package ru.yandex.practicum.filmorate.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.validation.NotSpaceChecker;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Getter
@Setter
@Validated
public class DtoUser {
    private int id;
    @NotBlank
    @Email
    @Valid
    private String email;
    @NotBlank
    @NotSpaceChecker
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;

}

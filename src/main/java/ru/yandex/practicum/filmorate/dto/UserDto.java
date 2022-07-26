package ru.yandex.practicum.filmorate.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.validation.NotSpaceChecker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Getter
@Setter
@Validated
public class UserDto {
    private long id;
    @NotBlank(message = "Can not be blank")
    @Email(message = "Is not correct email")
    private String email;
    @NotSpaceChecker
    @NotBlank(message = "Can not be blank")
    private String login;
    private String name;
    @PastOrPresent(message = "Not yet born")
    private LocalDate birthday;

    public void setName(String name) {
        this.name = name == null || name.isBlank() ? login : name;
    }
}

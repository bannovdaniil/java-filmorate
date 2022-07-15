package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.MpaRatingNotFound;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public Collection<MpaRating> getGenreList() {
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public MpaRating getGenreById(@PathVariable("id") Integer ratingMpaId) throws MpaRatingNotFound {
        return mpaService.getRatingMpaById(ratingMpaId);
    }
}

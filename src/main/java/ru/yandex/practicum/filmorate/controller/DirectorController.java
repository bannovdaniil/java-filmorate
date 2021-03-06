package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public List<Director> getAll() {
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public Director getById(@PathVariable("id") int id) throws DirectorNotFoundException {
        return directorService.getById(id);
    }

    @PostMapping
    public Director create(@Valid @RequestBody DirectorDto directorDto) throws DirectorNotFoundException {
        return directorService.create(directorDto);
    }

    @PutMapping
    public Director update(@Valid @RequestBody DirectorDto directorDto) throws DirectorNotFoundException {
        return directorService.update(directorDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) throws DirectorNotFoundException {
        directorService.delete(id);
    }
}

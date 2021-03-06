package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> getAll() {
        return directorStorage.getAll();
    }

    public Director create(DirectorDto directorDto) throws DirectorNotFoundException {
        return directorStorage.create(directorDto);
    }

    public Director update(DirectorDto directorDto) throws DirectorNotFoundException {
        return directorStorage.update(directorDto);
    }

    public void delete(int id) throws DirectorNotFoundException {
        directorStorage.delete(id);
    }

    public Director getById(int id) throws DirectorNotFoundException {
        return directorStorage.getById(id);
    }
}

package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dto.DtoDirector;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> getAll(){
        return directorStorage.getAll();
    }

    public Director create(DtoDirector dtoDirector) {
        return directorStorage.create(dtoDirector);
    }

    public Director update(DtoDirector dtoDirector) {
        return directorStorage.update(dtoDirector);
    }

    public void delete(int id) {
        directorStorage.delete(id);
    }

    public Director getById(int id) {
        return directorStorage.getById(id);
    }
}

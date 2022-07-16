package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFound;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDaoStorageImplTest {
    private final GenreStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;

    @Test
    void indexOfValue() throws GenreNotFound {
        int result = genreStorage.indexOfValue("Драма");
        Assertions.assertEquals(2, result);
    }

    @Test
    void getGenreById() throws GenreNotFound {
        Genre result = genreStorage.getGenreById(1);
        Assertions.assertEquals("Комедия", result.getName());
    }

    @Test
    void notFoundGenre() {
        GenreNotFound exception = Assertions.assertThrows(GenreNotFound.class,
                () -> genreStorage.getGenreById(-1)
        );

        Assertions.assertEquals("Genre Index not found.", exception.getMessage());
    }

    @Test
    void findAll() {
        List<Genre> genreResult = genreStorage.findAll();

        Assertions.assertEquals(6, genreResult.size());
        Assertions.assertEquals("Комедия", genreResult.get(0).getName());
    }
}
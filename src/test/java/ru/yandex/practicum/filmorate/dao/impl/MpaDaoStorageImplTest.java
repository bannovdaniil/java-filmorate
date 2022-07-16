package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.exceptions.MpaRatingNotFound;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDaoStorageImplTest {
    private final MpaStorage mpaStorage;

    @Test
    void indexOfValue() throws MpaRatingNotFound {
        int result = mpaStorage.indexOfValue("PG");
        Assertions.assertEquals(2, result);
    }

    @Test
    void getGenreById() throws MpaRatingNotFound {
        MpaRating result = mpaStorage.getRatingMpaById(3);
        Assertions.assertEquals("PG-13", result.getName());
    }

    @Test
    void notFoundRating() {
        MpaRatingNotFound exception = Assertions.assertThrows(MpaRatingNotFound.class,
                () -> mpaStorage.getRatingMpaById(-1)
        );

        Assertions.assertEquals("MPA Rating Index not found.", exception.getMessage());
    }

    @Test
    void findAll() {
        List<MpaRating> genreResult = mpaStorage.findAll();

        Assertions.assertEquals(5, genreResult.size());
        Assertions.assertEquals("G", genreResult.get(0).getName());
    }
}
package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreControllerTests {
    private final GenreStorage genreDAO;

    @Test
    void testFindGenreById() {
        assertEquals(Genre.builder().id(1).name("Комедия").build(), genreDAO.findById(1));
    }

    @Test
    void testFindUnknownGenre() {
        assertThrows(ObjectNotFoundException.class, () -> genreDAO.findById(9999),
                "Genre с id " + 9999 + " не найден.");
    }

    @Test
    void testFindAllGenres() {
        assertEquals(Genre.builder().id(1).name("Комедия").build(), genreDAO.findAll().get(0));
        assertEquals(6, genreDAO.findAll().size());
    }
}

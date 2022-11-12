package ru.yandex.practicum.filmorate.storage.film;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UpdateDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.IdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements  FilmStorage{

    private final Map<Integer, Film> films = new HashMap<>();

    private final IdGenerator idGenerator;

    @Override
    public void addFilm(Film film) {
        film.setId(idGenerator.getId());
        films.put(film.getId(), film);
    }

    @Override
    public void upDateFilm(Film film) {
        if (film.getId() == null) {
            throw  new UpdateDataException("Идентификатор фильма отсутствует, невозможно обновить фильм. Фильм не найден");
        }
        if (!films.containsKey(film.getId())) {
            throw  new UpdateDataException("Такого фильма ещё нет, невозможно обновить!");
        }
        films.put(film.getId(), film);
    }

    @Override
    public void delete(Integer id) {
        films.remove(id);
    }

    @Override
    public int getNumberFilms() {
        return films.size();
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

}

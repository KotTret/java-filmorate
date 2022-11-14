package ru.yandex.practicum.filmorate.storage.film;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.IdGenerator;

import java.util.*;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements  FilmStorage{

    private final Map<Integer, Film> films = new HashMap<>();

    //   private final Set<Film> popularFilms = new TreeSet<>(Comparator.comparing(o -> o.getLikesOfUsers().size()));

    private final IdGenerator idGenerator;

    @Override
    public void addFilm(Film film) {
        film.setId(idGenerator.getId());
        films.put(film.getId(), film);
    }

    @Override
    public void upDateFilm(Film film) {
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

    @Override
    public boolean containsId(Integer id) {
        return films.containsKey(id);
    }

    @Override
    public Film getFilm(Integer id) {
        return films.get(id);
    }

}

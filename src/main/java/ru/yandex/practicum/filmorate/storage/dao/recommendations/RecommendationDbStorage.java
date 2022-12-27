package ru.yandex.practicum.filmorate.storage.dao.recommendations;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendationDbStorage implements RecommendationStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Integer[]> getAllLikes() {
        List<Integer[]> allLikes = jdbcTemplate.query("SELECT * FROM film_likes", RecommendationDbStorage::allLikes);
        return allLikes;
    }
    private static Integer[] allLikes(ResultSet resultSet, int rowNum) throws SQLException {
        return new Integer[]{resultSet.getInt("film_id"), resultSet.getInt("user_id")};
    }

    @Override
    public List<Film> getRecommendationsFilms(List<Integer> filmsId) {
        final String id = filmsId.stream().map(Object::toString)
                .collect(Collectors.joining(","));
        final String filmList = "SELECT films.*, mpa.mpa_name " +
                "FROM films, mpa " +
                "WHERE films.mpa_id = mpa.mpa_id " +
                "AND films.film_id IN (" + id + ")";
        Map<Integer, Film> films = new HashSet<>(jdbcTemplate.query(filmList, FilmDbStorage::mapRowToFilm))
                .stream().collect(Collectors.toMap(Film::getId, film -> film));
        return new ArrayList<>(films.values());
    }
}
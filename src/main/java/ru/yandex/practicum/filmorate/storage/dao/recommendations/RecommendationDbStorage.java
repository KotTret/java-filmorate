package ru.yandex.practicum.filmorate.storage.dao.recommendations;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendationDbStorage implements RecommendationStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate nameJdbcTemplate;

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
        SqlParameterSource parameters = new MapSqlParameterSource("idFilms", films.keySet());
        String genres = "SELECT fg.film_id, fg.genre_id, g.genre_name from FILM_GENRES as fg " +
                "LEFT JOIN genres AS g ON  fg.genre_id = g.genre_id where film_id IN (:idFilms)";
        nameJdbcTemplate.query(genres, parameters,
                (rs, rowNum) -> films.get(rs.getInt("film_id")).getGenres().add(GenreDAO.mapRowToGenre(rs, rowNum)));
        return new ArrayList<>(films.values());
    }
}
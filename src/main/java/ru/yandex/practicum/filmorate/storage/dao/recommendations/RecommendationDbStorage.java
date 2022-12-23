package ru.yandex.practicum.filmorate.storage.dao.recommendations;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDbStorage;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendationDbStorage implements RecommendationStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Integer[]> getAllLikes() {
        List<Integer[]> allLikes = new ArrayList<>();
        jdbcTemplate.query("select * from film_likes", (ResultSet rs) -> {
            Integer[] like = {rs.getInt("film_id"), rs.getInt("user_id")};
            allLikes.add(like);
            while (rs.next()) {
                like = new Integer[]{rs.getInt("film_id"), rs.getInt("user_id")};
                allLikes.add(like);
            }
        });
        return allLikes;
    }

    @Override
    public List<Film> getRecommendationsFilms(List<Integer> filmsId) {
        final String id = filmsId.stream().map(Object::toString)
                .collect(Collectors.joining(","));
        final String filmList = "SELECT films.*, mpa.mpa_name " +
                "FROM films, mpa " +
                "WHERE films.mpa_id = mpa.mpa_id " +
                "AND films.film_id IN (" + id + ")";
        Set<Film> setFilm = new HashSet<>(jdbcTemplate.query(filmList, FilmDbStorage::mapRowToFilm));
        List<Film> filmsList = new ArrayList<>();
        filmsList.addAll(setFilm);
        for(Film film: filmsList) {
            jdbcTemplate.query("SELECT genres.genre_id, genres.genre_name " +
                    "FROM films, genres, film_genres " +
                    "WHERE films.film_id = film_genres.film_id " +
                    "AND film_genres.genre_id = genres.genre_id " +
                    "AND films.film_id = ?", (ResultSet rs) -> {
                film.getGenres().add(new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
                while (rs.next()) {
                    film.getGenres().add(new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
                }
            }, film.getId());
        }
        return filmsList;
    }
}

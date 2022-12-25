package ru.yandex.practicum.filmorate.storage.dao.recommendations;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        jdbcTemplate.query("SELECT * FROM film_likes", (ResultSet rs) -> {
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
        final String genre = "SELECT genres.genre_id, genres.genre_name, film_genres.film_id " +
                "FROM films, genres, film_genres " +
                "WHERE films.film_id = film_genres.film_id " +
                "AND film_genres.genre_id = genres.genre_id " +
                "AND films.film_id = ? ";
        Set<Film> setFilm = new HashSet<>();
        jdbcTemplate.query(filmList, (ResultSet rs) -> {
            Film film = FilmDbStorage.mapRowToFilm(rs, rs.getRow());
            List<Genre> genres = (jdbcTemplate.query(genre, RecommendationDbStorage::mapRowToGenre, rs.getInt("film_id")));
            film.getGenres().addAll(genres);
            setFilm.add(film);
            while (rs.next()) {
                film = FilmDbStorage.mapRowToFilm(rs, rs.getRow());
                genres = (jdbcTemplate.query(genre, RecommendationDbStorage::mapRowToGenre, rs.getInt("film_id")));
                film.getGenres().addAll(genres);
                setFilm.add(film);
            }
        });
        return new ArrayList<>(setFilm);
    }

    public static Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }
}

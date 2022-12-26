package ru.yandex.practicum.filmorate.storage.dao.reviews;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Reviews;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewsDbStorage implements ReviewsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Reviews getReviewsById(Integer reviewId) {
        String findReviewById = "SELECT * FROM film_reviews WHERE review_id = ?";
        return jdbcTemplate.queryForObject(findReviewById, this::mapRowToReviews, reviewId);
    }

    @Override
    public List<Reviews> getReviewByFilmId(Integer filmId, Integer count) {
        String findReviewById = "SELECT * FROM film_reviews WHERE film_id = ? ORDER BY useful DESC limit ?";
        return jdbcTemplate.query(findReviewById, this::mapRowToReviews, filmId, count);
    }

    @Override
    public List<Reviews> findAllReviews() {
        List<Reviews> result = new ArrayList<>();
        String findReviewOne = "SELECT * FROM film_reviews WHERE useful > 0 ORDER BY useful DESC";
        result.addAll(jdbcTemplate.query(findReviewOne, this::mapRowToReviews));
        String findReviewTwo = "SELECT * FROM film_reviews WHERE useful = 0 OR useful IS NULL ORDER BY review_id";
        result.addAll(jdbcTemplate.query(findReviewTwo, this::mapRowToReviews));
        String findReviewThree = "SELECT * FROM film_reviews WHERE useful < 0 ORDER BY useful";
        result.addAll(jdbcTemplate.query(findReviewThree, this::mapRowToReviews));
        return result;
    }

    @Override
    public Reviews addReviews(Reviews reviews) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film_reviews")
                .usingGeneratedKeyColumns("review_id");
        Integer id = simpleJdbcInsert.executeAndReturnKey(reviews.toMap()).intValue();
        reviews.setReviewId(id);
        return reviews;
    }

    @Override
    public Reviews updateReviews(Reviews reviews) {
        String updateReviews = "UPDATE film_reviews SET content = ?, " +
                "is_positive = ? " +
                "WHERE review_id = ?";
        jdbcTemplate.update(updateReviews,
                reviews.getContent(),
                reviews.getIsPositive(),
                reviews.getReviewId());
        return getReviewsById(reviews.getReviewId());
    }

    @Override
    public void updateUseful(Reviews review) {
        final String updateUseful = "UPDATE film_reviews SET " +
                "useful = ? " +
                "WHERE review_id = ?";
        jdbcTemplate.update(updateUseful, review.getUseful(), review.getReviewId());
    }

    @Override
    public void updateReviewsIsPositive(Integer reviewId, Boolean isPositive, Integer userId) {
        String updateReviewsIsPositive = "INSERT INTO reviews_is_positive (is_positive, review_id, user_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(updateReviewsIsPositive, isPositive, reviewId, userId);
    }

    @Override
    public void deleteReviews(Integer reviewId) {
        String sqlQuery = "delete from film_reviews where review_id = ?";
        jdbcTemplate.update(sqlQuery, reviewId);
    }

    @Override
    public void deleteLike(Integer reviewId, Integer userId) {
        final String deleteLike = "DELETE FROM reviews_is_positive WHERE review_id = ? AND user_id = ? AND is_positive = ?";
        jdbcTemplate.update(deleteLike, reviewId, userId, true);
    }

    @Override
    public void deleteDislike(Integer reviewId, Integer userId) {
        final String deleteDislike = "DELETE FROM reviews_is_positive WHERE review_id = ? AND user_id = ? AND is_positive = ?";
        jdbcTemplate.update(deleteDislike, reviewId, userId, false);
    }

    @Override
    public boolean checkReview(Reviews reviews) {
        final String sqlQuery = "SELECT CASE WHEN COUNT(1) > 0 THEN TRUE ELSE FALSE END AS result " +
                "FROM film_reviews WHERE user_id = ? AND film_id = ?";
        String result = jdbcTemplate.query(sqlQuery,
                (rs, rn) -> rs.getString("result"),
                reviews.getUserId(),
                reviews.getFilmId()).get(0);
        return Boolean.parseBoolean(result);
    }

    @Override
    public boolean checkReview(Integer reviewId) {
        final String sqlQuery = "SELECT CASE WHEN COUNT(1) > 0 THEN TRUE ELSE FALSE END AS result " +
                "FROM film_reviews WHERE review_id = ?";
        String result = jdbcTemplate.query(sqlQuery,
                (rs, rn) -> rs.getString("result"),
                reviewId).get(0);
        return Boolean.parseBoolean(result);
    }

    @Override
    public boolean checkLikeOrDislike(Integer reviewId, Integer userId, boolean check) {
        final String checkPositive = "SELECT CASE WHEN COUNT(1) > 0 THEN TRUE ELSE FALSE END AS result " +
                "FROM reviews_is_positive WHERE review_id = ? AND user_id = ? AND is_positive = ?";
        String result = jdbcTemplate.query(checkPositive,
                (rs, rn) -> rs.getString("result"),
                reviewId, userId, check).get(0);
        return Boolean.parseBoolean(result);
    }

    private Reviews mapRowToReviews(ResultSet resultSet, int rowNum) throws SQLException {
        return Reviews.builder()
                .reviewId(resultSet.getInt("review_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id"))
                .useful(resultSet.getInt("useful"))
                .build();
    }
}
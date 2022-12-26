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
        String findAllReviews = "SELECT * FROM film_reviews ORDER BY useful DESC";
        return new ArrayList<>(jdbcTemplate.query(findAllReviews, this::mapRowToReviews));
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
    public void updateReviewsIsPositive(Integer reviewId, Integer isPositive, Integer userId) {
        String updateReviewsIsPositive = "INSERT INTO reviews_is_positive (review_id, user_id, is_positive) VALUES (?, ?, ?)";
        jdbcTemplate.update(updateReviewsIsPositive, reviewId, userId, isPositive);
        useful(reviewId);
    }

    @Override
    public void deleteReviews(Integer reviewId) {
        String sqlQuery = "delete from film_reviews where review_id = ?";
        jdbcTemplate.update(sqlQuery, reviewId);
    }

    @Override
    public void deleteLikeOrDislike(Integer reviewId, Integer userId) {
        useful(reviewId);
        final String deleteLike = "DELETE FROM reviews_is_positive WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(deleteLike, reviewId, userId);
    }

    private void useful(Integer id) {
        String updateUseful;
        updateUseful = "update film_reviews r set useful = (select sum(l.is_positive) " +
                "from reviews_is_positive l where l.review_id = r.review_id)  where review_id = ?";
        jdbcTemplate.update(updateUseful, id);
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
    public boolean checkLikeOrDislike(Integer reviewId, Integer userId, Integer check) {
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
package ru.yandex.practicum.filmorate.storage.dao.reviews;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Reviews;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewsDbStorage implements ReviewsStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Reviews> findAllReviews() {
        String findAllReviews = "SELECT * FROM film_reviews";
        return jdbcTemplate.query(findAllReviews, this::mapRowToReviews);
    }

    @Override
    public List<Reviews> getReviewByFilmId(Integer id, Integer count) {
        String findReviewById = "SELECT * FROM film_reviews WHERE film_id = ? desc limit ?";
        return jdbcTemplate.query(findReviewById, this::mapRowToReviews,id, count);
    }

    @Override
    public Reviews getReviewsById(Integer reviewId) {
        String findReviewById = "SELECT * FROM film_reviews WHERE review_id = ?";
        return jdbcTemplate.queryForObject(findReviewById, this::mapRowToReviews, reviewId);
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
        String updateReviews = "UPDATE film_reviews SET content = ?, is_positive = ?, " +
                "user_id = ?, film_id = ?, useful = ? WHERE review_id = ?";
        jdbcTemplate.update(updateReviews,
                reviews.getContent(),
                reviews.getIsPositive(),
                reviews.getUserId(),
                reviews.getFilmId(),
                reviews.getUseful(),
                reviews.getReviewId());
        return getReviewsById(reviews.getReviewId());
    }

    @Override
    public Reviews updateReviewsIsPositive(Integer reviewId, Boolean isPositive, Integer userId) {
        String updateReviewsIsPositive = "UPDATE film_reviews SET is_positive = ? WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(updateReviewsIsPositive, isPositive, reviewId, userId);
        return getReviewsById(reviewId);
    }

    @Override
    public void deleteReviews(Integer reviewId) {
        String sqlQuery = "delete from film_reviews where review_id = ?";
        jdbcTemplate.update(sqlQuery, reviewId);
    }

    @Override
    public boolean checkReview(Integer reviewId) {
        String sqlQuery = "SELECT review_id FROM film_reviews where review_id = ?";
        return !jdbcTemplate.queryForList(sqlQuery, Integer.class, reviewId).isEmpty();
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

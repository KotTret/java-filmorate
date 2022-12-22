package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Reviews;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Validated
public class ReviewsController {
    private  final ReviewsService reviewsService;

    @GetMapping("/reviews/{reviewId}")
    public Reviews getReviewsByUrlId(@PathVariable Integer reviewId) {
        return reviewsService.getReviewById(reviewId);
    }

    @GetMapping("/reviews")
    public List<Reviews> findAllReviews(@RequestParam(required = false) Optional<Integer> filmId,
                                        @RequestParam(required = false, defaultValue = "10") int count) {
        if (filmId.isPresent()) {
            return reviewsService.getReviewByFilmId(filmId.get(), count);
        } else {
            return reviewsService.findAllReviews();
        }
    }

    @PostMapping("/reviews")
    public Reviews addReviews(@Valid @RequestBody Reviews reviews) {
        return reviewsService.addReviews(reviews);
    }

    @PutMapping("/reviews")
    public Reviews updateReviews(@Valid @RequestBody Reviews reviews) {
        return reviewsService.updateReviews(reviews);
    }

    @PutMapping(value = "/reviews/{reviewId}/{isPositive}/{userId}")
    public void updateReviews(@PathVariable Integer reviewId, @PathVariable String isPositive,
                              @PathVariable Integer userId) {
        reviewsService.updateReviewsIsPositive(reviewId, isPositive, userId);
    }

    @DeleteMapping(value = "/reviews/{reviewId}")
    public void deleteReviews(@PathVariable Integer reviewId) {
        reviewsService.deleteReviews(reviewId);
    }

    @DeleteMapping("/reviews/{reviewId}/{isPositive}/{userId}")
    public void deleteIsPositive(@PathVariable Integer reviewId, @PathVariable String isPositive,
                                 @PathVariable Integer userId) {
        reviewsService.deleteIsPositive(reviewId, isPositive, userId);
    }
}

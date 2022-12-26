package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Create;
import ru.yandex.practicum.filmorate.model.Reviews;
import ru.yandex.practicum.filmorate.model.Update;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/reviews")
public class ReviewsController {
    private  final ReviewsService reviewsService;

    @GetMapping("/{reviewId}")
    public Reviews findById(@PathVariable Integer reviewId) {
        return reviewsService.getById(reviewId);
    }


    //@Positive count не проходят все тесты.
    @GetMapping
    public List<Reviews> findAll(@RequestParam(required = false) Optional<Integer> filmId,
                                 @RequestParam(required = false, defaultValue = "10") @Positive int count) {
        if (filmId.isPresent()) {
            return reviewsService.getByFilmId(filmId.get(), count);
        } else {
            return reviewsService.findAll();
        }
    }

    @PostMapping
    public Reviews create(@Validated(Create.class) @RequestBody Reviews reviews) {
        return reviewsService.add(reviews);
    }

    @PutMapping
    public Reviews update(@Validated(Update.class) @RequestBody Reviews reviews) {
        return reviewsService.update(reviews);
    }

    @PutMapping(value = "/{reviewId}/{isPositive}/{userId}")
    public void update(@PathVariable Integer reviewId, @PathVariable String isPositive,
                              @PathVariable Integer userId) {
        int like = 0;
        if (isPositive.equalsIgnoreCase("like")) {
            like = 1;
        } else if (isPositive.equalsIgnoreCase("dislike")) {
            like = -1;
        }
        reviewsService.updateIsPositive(reviewId, isPositive, userId, like);
    }

    @DeleteMapping(value = "/{reviewId}")
    public void delete(@PathVariable Integer reviewId) {
        reviewsService.delete(reviewId);
    }

    @DeleteMapping("/{reviewId}/{isPositive}/{userId}")
    public void delete(@PathVariable Integer reviewId, @PathVariable String isPositive,
                                 @PathVariable Integer userId) {
        int like = 0;
        if (isPositive.equalsIgnoreCase("like")) {
            like = 1;
        } else if (isPositive.equalsIgnoreCase("dislike")) {
            like = -1;
        }
        reviewsService.deleteIsPositive(reviewId, isPositive, userId, like);
    }
}

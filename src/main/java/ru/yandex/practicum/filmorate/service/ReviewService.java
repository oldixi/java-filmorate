package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.WrongIdException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private static final int DUMMY_PARAM_VALUE = -1;
    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final FeedStorage feedStorage;
    private final FilmService filmService;
    private final UserService userService;

    public Review addReview(Review review) {
        if (!filmService.isLegalFilmId(review.getFilmId()) || !userService.isLegalUserId(review.getUserId())) {
            return review;
        }
        Review reviewAdded = reviewStorage.addReview(review);
        feedStorage.addReview(reviewAdded.getUserId(), reviewAdded.getReviewId());
        return reviewAdded;
    }

    public Review updateReview(Review review) {
        if (!filmService.isLegalFilmId(review.getFilmId()) || !userService.isLegalUserId(review.getUserId())) {
            return review;
        }
        Review reviewUpdated = reviewStorage.updateReview(review);
        feedStorage.updateReview(reviewUpdated.getUserId(), reviewUpdated.getReviewId());
        return reviewUpdated;
    }

    public void deleteReview(long id) {
        Review review = getReviewById(id);
        if (userService.isLegalUserId(review.getUserId())) {
            reviewStorage.deleteReview(id);
            feedStorage.deleteReview(review.getUserId(), id);
        }
    }

    public Review getReviewById(long id) {
        if (isIncorrectId(id)) {
            throw new WrongIdException("Param must be more then 0");
        }
        Optional<Review> ReviewOpt = reviewStorage.getReviewById(id);
        if (ReviewOpt.isEmpty()) {
            throw new WrongIdException("No review with id = " + id + " in DB was found.");
        }
        return ReviewOpt.get();
    }

    public List<Review> getAllReviews() {
        return reviewStorage.getAllReviews();
    }

    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        if (filmId == DUMMY_PARAM_VALUE) {
            return getAllReviews();
        }
        filmService.isLegalFilmId(filmId);
        return reviewStorage.getReviewsByFilmId(filmId, count);
    }

    public void addLikeToReview(long id, long userId) {
        if (isLegalReviewId(id) && userService.isLegalUserId(userId)) {
            reviewLikeStorage.addLike(id, userId);
        }
    }

    public void addDislikeToReview(long id, long userId) {
        if (isLegalReviewId(id) && userService.isLegalUserId(userId)) {
            reviewLikeStorage.addDislike(id, userId);
        }
    }

    public void deleteLikeOrDislike(long id, long userId) {
        if (isLegalReviewId(id) && userService.isLegalUserId(userId)) {
            reviewLikeStorage.deleteLikeOrDislike(id, userId);
        }
    }

    public boolean isLegalReviewId(Long reviewId) {
        return getReviewById(reviewId) != null;
    }

    private boolean isIncorrectId(long id) {
        return id <= 0;
    }
}


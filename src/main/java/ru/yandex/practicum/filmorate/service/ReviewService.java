package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.WrongFilmIdException;
import ru.yandex.practicum.filmorate.exception.WrongUserIdException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;

    public Review addReview(Review review) {
        if (isIncorrectId(review.getUserId()) || isIncorrectId(review.getFilmId())) {
            throw new WrongFilmIdException("Wrong film or user id.");
        }

        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        if (isIncorrectId(review.getUserId()) || isIncorrectId(review.getFilmId())) {
            throw new ValidationException("Wrong film or user id.");
        }

        return reviewStorage.updateReview(review);
    }

    public void deleteReview(long id) {
        if (isIncorrectId(id)) {
            throw new WrongFilmIdException("Bad review id");
        }

        reviewStorage.deleteReview(id);
    }

    public Review getReviewById(long id) {
        if (isIncorrectId(id)) {
            throw new WrongFilmIdException("Bad review id");
        }

        return reviewStorage.getReviewById(id);
    }

    public List<Review> getAllReviews() {
        return reviewStorage.getAllReviews();
    }

    public List<Review> getReviewsByFilmId(long filmId, int count) {
        if (isIncorrectId(filmId)) {
            throw new WrongFilmIdException("Bad film id");
        }

        return reviewStorage.getReviewsByFilmId(filmId, count);
    }

    public void addLikeToReview(long id, long userId) {
        if (isIncorrectId(id)) {
            throw new WrongFilmIdException("Bad review id");
        }

        if (isIncorrectId(userId)) {
            throw new WrongUserIdException("Bad user id");
        }

        reviewLikeStorage.addLike(id, userId);
    }


    public void addDislikeToReview(long id, long userId) {
        if (isIncorrectId(id)) {
            throw new WrongFilmIdException("Bad review id");
        }

        if (isIncorrectId(userId)) {
            throw new WrongUserIdException("Bad user id");
        }

        reviewLikeStorage.addDislike(id, userId);
    }

    public void deleteLikeOrDislike(long id, long userId) {
        if (isIncorrectId(id)) {
            throw new WrongFilmIdException("Bad review id");
        }

        if (isIncorrectId(userId)) {
            throw new WrongUserIdException("Bad user id");
        }

        reviewLikeStorage.deleteLikeOrDislike(id, userId);
    }

    private boolean isIncorrectId(long id) {
        return id <= 0;
    }
}
package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.WrongFilmIdException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DbReviewStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review addReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "insert into reviews (content, is_positive, user_id, film_id) values (?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return review;
    }

    @Override
    public Review updateReview(Review review) {

        int response = jdbcTemplate.update("update reviews set content = ?, is_positive = ?, user_id = ?, " +
                        "film_id = ? where id = ?",
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getReviewId());

        if (response == 0) {
            throw new WrongFilmIdException("No such review in DB with id = " + review.getReviewId() + ". Update failed");
        }

        return review;
    }

    @Override
    public void deleteReview(long id) {
        if (isIncorrectId(id)) {
            throw new WrongFilmIdException("Id must be more than 0");
        }

        jdbcTemplate.update("delete from reviews where id = ?", id);
    }

    @Override
    public Review getReviewById(long id) {
        String sqlQuery = "select r.*, u.cnt from reviews r left join (select review_id, sum(useful) cnt " +
                "from review_like group by review_id) u on r.id = u.review_id " +
                "where r.id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new WrongFilmIdException("There is no review in DB with id = " + id);
        }
    }

    @Override
    public List<Review> getAllReviews() {
        return jdbcTemplate.query(
                "select r.*, u.cnt from reviews r left join (select review_id, sum(useful) cnt " +
                        "from review_like group by review_id) u on r.id = u.review_id",
                this::mapper
        );
    }

    //Выводим последние написанные отзывы для фильма
    @Override
    public List<Review> getReviewsByFilmId(long filmId, int count) {
        return jdbcTemplate.query(
                "select r.*, u.cnt from reviews r join (select review_id, sum(useful) cnt from review_like group by review_id) u " +
                        "on r.id = u.review_id" +
                        "where r.film_id = ? sort by useful desc " +
                        "limit = ?",
                this::mapper,
                filmId,
                count
        );
    }

    private Review mapper(ResultSet resultSet, int rowNum) {
        try {
            Review review = new Review();
            review.setReviewId(resultSet.getLong("id"));
            review.setContent(resultSet.getString("content"));
            review.setIsPositive(resultSet.getBoolean("is_positive"));
            review.setUserId(resultSet.getLong("user_id"));
            review.setFilmId(resultSet.getLong("film_id"));
            review.setUseful(resultSet.getInt("u.cnt"));
            return review;
        } catch (SQLException e) {
            throw new WrongFilmIdException(e.getMessage());
        }
    }

    private boolean isIncorrectId(long id) {
        return id <= 0;
    }
}

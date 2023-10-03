DROP TABLE IF EXISTS users, films, ratings, film_genre, genres, friends, film_like, friendship_request CASCADE;
CREATE TABLE films (
    id int generated by default as identity NOT NULL,
    name varchar(200),
    description varchar(200),
    release_date date,
    duration int,
    rating int
);
CREATE TABLE users (
    id int GENERATED by default as identity NOT NULL,
    name varchar(200) NOT NULL,
    login varchar(200) NOT NULL,
    email varchar(200) NOT NULL,
    birthday date
);
CREATE TABLE ratings (
    id int generated by default as identity NOT NULL,
    name varchar(200)
);
CREATE TABLE genres (
    id int generated by default as identity NOT NULL,
    name varchar(200)
);
CREATE TABLE film_genre (
    film_id int NOT NULL,
    genre_id int NOT NULL
);
CREATE TABLE film_like (
    film_id int NOT NULL,
    user_id int NOT NULL
);
CREATE TABLE friends (
    user_id int NOT NULL,
    friend_id int NOT NULL
);
CREATE TABLE friendship_request (
    from_id int NOT NULL,
    to_id int NOT NULL
);
ALTER TABLE films ADD CONSTRAINT pk_id_films PRIMARY KEY(id);
ALTER TABLE users ADD CONSTRAINT pk_id_users PRIMARY KEY(id);
ALTER TABLE ratings ADD CONSTRAINT pk_id_retings PRIMARY KEY(id);
ALTER TABLE genres ADD CONSTRAINT pk_id_genres PRIMARY KEY(id);
ALTER TABLE film_genre ADD CONSTRAINT pk_film_genre PRIMARY KEY(film_id, genre_id);
ALTER TABLE film_genre ADD CONSTRAINT fk_film_id FOREIGN KEY(film_id) REFERENCES films(id);
ALTER TABLE film_genre ADD CONSTRAINT fk_genre_id FOREIGN KEY(genre_id) REFERENCES genres(id);
ALTER TABLE film_like ADD CONSTRAINT pk_film_user PRIMARY KEY(film_id, user_id);
ALTER TABLE film_like ADD CONSTRAINT fk_film_id_like FOREIGN KEY(film_id) REFERENCES films(id);
ALTER TABLE film_like ADD CONSTRAINT fk_user_id_like FOREIGN KEY(user_id) REFERENCES users(id);
ALTER TABLE friends ADD CONSTRAINT pk_friends PRIMARY KEY(user_id, friend_id);
ALTER TABLE friends ADD CONSTRAINT fk_user_id FOREIGN KEY(user_id) REFERENCES users(id);
ALTER TABLE friends ADD CONSTRAINT fk_friend_id FOREIGN KEY(friend_id) REFERENCES users(id);
ALTER TABLE friendship_request ADD CONSTRAINT pk_from_to PRIMARY KEY(from_id, to_id);
ALTER TABLE friendship_request ADD CONSTRAINT fk_from_id FOREIGN KEY(from_id) REFERENCES users(id);
ALTER TABLE friendship_request ADD CONSTRAINT fk_to_id FOREIGN KEY(to_id) REFERENCES users(id);
INSERT INTO ratings (name) values ('G');
INSERT INTO ratings (name) values ('PG');
INSERT INTO ratings (name) values ('PG-13');
INSERT INTO ratings (name) values ('R');
INSERT INTO ratings (name) values ('NC-17');
INSERT INTO genres (name) values ('Комедия');
INSERT INTO genres (name) values ('Драма');
INSERT INTO genres (name) values ('Мультфильм');
INSERT INTO genres (name) values ('Триллер');
INSERT INTO genres (name) values ('Документальный');
INSERT INTO genres (name) values ('Боевик');

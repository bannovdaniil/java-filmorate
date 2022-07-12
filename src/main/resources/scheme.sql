CREATE TABLE IF NOT EXISTS "ratings" (
    rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY  PRIMARY KEY,
    name VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS "films" (
    film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(250) NOT NULL,
    duration INTEGER NOT NULL,
    likes INTEGER NOT NULL DEFAULT 0,
    rating_id INTEGER REFERENCES "ratings"(rating_id) ON DELETE NO ACTION,
    CONSTRAINT check_films_duration CHECK(duration > 0)
);

CREATE TABLE IF NOT EXISTS "genres"(
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS "film_genres"(
    film_id INTEGER REFERENCES "films"(film_id) ON DELETE NO ACTION,
    genre_id INTEGER REFERENCES "genres"(genre_id) ON DELETE NO ACTION,
    CONSTRAINT pk_film_genres PRIMARY KEY(film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS "users"(
    user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(30) UNIQUE NOT NULL,
    login VARCHAR(30) UNIQUE NOT NULL,
    name VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS "friends"(
    user_id INTEGER REFERENCES "users"(user_id) ON DELETE NO ACTION,
    friend_id INTEGER REFERENCES "users"(user_id) ON DELETE NO ACTION,
    status VARCHAR(10) NOT NULL,
    -- status: запрос, отклонен, подтвержден / REQUEST, REJECTED, confirmed
    CONSTRAINT pk_friends PRIMARY KEY(user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS "likes"(
    film_id INTEGER REFERENCES "films"(film_id) ON DELETE NO ACTION,
    user_id INTEGER REFERENCES "users"(user_id) ON DELETE NO ACTION,
    CONSTRAINT pk_likes PRIMARY KEY(film_id, user_id)
);
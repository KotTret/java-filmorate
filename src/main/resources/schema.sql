CREATE TABLE IF NOT EXISTS genres
(
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS mpa
(
    mpa_id    INTEGER  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_name    VARCHAR(10) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS films
(
    film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    release_date DATE,
    duration_in_minutes INTEGER,
    mpa_id INTEGER,
    rate INTEGER,
        FOREIGN KEY (mpa_id)
        REFERENCES mpa (mpa_id)
);

CREATE TABLE IF NOT EXISTS users
(
    user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    login VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(50),
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS film_likes
(
    film_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (user_id)
    REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (film_id)
    REFERENCES films (film_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS user_friends
(
    user_id INTEGER NOT NULL,
    friend_id INTEGER NOT NULL,
    FOREIGN KEY (user_id)
    REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id)
    REFERENCES users (user_id) ON DELETE CASCADE

);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id INTEGER NOT NULL,
    genre_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS directors
(
    director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    director_name VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_directors
(
    film_id INTEGER NOT NULL,
    director_id INTEGER NOT NULL,
    FOREIGN KEY (film_id)
        REFERENCES films (film_id) ON DELETE CASCADE,
    FOREIGN KEY (director_id)
        REFERENCES directors (director_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, director_id)
);
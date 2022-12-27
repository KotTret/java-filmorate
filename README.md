# java-filmorate

Функциональность приложения эквивалентна API, посредством которого происходит взаимодействие.
## Реализованные эндпоинты:

<details> 
 <summary><h3>Пользователи</h3></summary>

* **GET** /users - получение списка всех пользователей
* **GET** /users/{userId} - получение информации о пользователе по его id
* **POST** /users - создание пользователя
* **PUT** /users - редактирование пользователя
* **PUT** /users/{id}/friends/{friendId} — добавление в друзья
* **DELETE** /users/{id}/friends/{friendId} — удаление из друзей
* **DELETE** /users/{userId} — удаление пользователя
* **GET** /users/{id}/friends — возвращает список пользователей, являющихся его друзьями
* **GET** /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем
* **GET** /users/{id}/feed — возвращает ленту событий пользователя.
</details>

<details>
  <summary><h3>Фильмы</h3></summary>

* **GET** /films - получение списка всех фильмов
* **GET** /films/{filmId} - получение информации о фильме по его id
* **POST** /films - создание фильма
* **PUT** /films - редактирование фильма
* **PUT** /films/{id}/like/{userId} — пользователь ставит лайк фильму
* **DELETE** /films/{id}/like/{userId} — пользователь удаляет лайк
* **DELETE** /films/{filmId} — удаление фильма
* **GET** /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, возвращает первые 10
* **GET** /films/common?userId={userId}&friendId={friendId} — Возвращает список фильмов, отсортированных по популярности.
* **GET** /films/director/{directorId} — Возвращает список режиссёров
* **GET** /films/search?query={query}&by={by} — Возвращает список фильмов, отсортированных по популярности. query — текст для поиска by — может принимать значения director (поиск по режиссёру), title (поиск по названию), либо оба значения через запятую при поиске одновременно и по режиссеру и по названию.

</details>
<details>
  <summary><h3>Жанры</h3></summary>

* **GET** /genres - получение списка всех жанров
* **GET** /genres/{id} - получение информации о жанре по его id

</details>
<details>
  <summary><h3>Рейтинги</h3></summary>

* **GET** /mpa - получение списка всех рейтингов
* **GET** /mpa/{id} - получение информации о рейтинге по его id

</details>

<details>
  <summary><h3>Рекомендации</h3></summary>

* **GET** /users/{id}/recommendations - Возвращает рекомендации по фильмам для просмотра.

</details>


<details>
  <summary><h3>Отзывы</h3></summary>

* **GET** /reviews - получение списка всех отзывов
* **GET** /reviews/{reviewId} - получение отзыва по идентификатору.
* **POST** /reviews - создание отзыва
* **PUT** /reviews - редактирование отзыва
* **PUT** /reviews/{reviewId}/{isPositive}/{userId} — пользователь ставит лайк отзыву
* **DELETE** /reviews/{reviewId}/{isPositive}/{userId} —  пользователь удаляет лайк/дизлайк отзыву.
* **DELETE** /reviews/{reviewId} — удаление отзыва
</details>

<details>
  <summary><h3>Режиссёры</h3></summary>

* **GET** /directors - получение списка всех режиссёров
* **GET** /directors/{id} - получение режиссёра по идентификатору.
* **POST** /directors - добавление режиссёра
* **PUT** /directors - редактирование режиссёра
* **DELETE** /directors/{id} — удаление режиссёра
</details>

## Схема БД и примеры запросов

![plot](./src/main/resources/DB.png)

<details>
  <summary><h3>Для пользователей:</h3></summary>

* создание пользователя
```SQL
INSERT INTO users (email, login, name, birthday)
VALUES ( ?, ?, ?, ? );
```
* редактирование пользователя
```SQL
UPDATE users
SET email = ?,
    login = ?,
    name = ?,
    birthday = ?
WHERE user_id = ?
```
* получение списка всех пользователей
```SQL
SELECT *
FROM users
```
* получение информации о пользователе по его `id`
```SQL
SELECT *
FROM users
WHERE user_id = ?
```
* добавление в друзья
```SQL
INSERT INTO user_friends(user_id, friend_id) VALUES (?, ?)

```
* удаление из друзей
```SQL
DELETE
FROM user_friends
WHERE user_id = ? AND friend_id = ?
```
* возвращает список пользователей, являющихся его друзьями
```SQL
select * from USERS 
where USER_ID in
(select FRIEND_ID from USER_FRIENDS 
where USER_ID = ?)
```
* список друзей, общих с другим пользователем
```SQL
SELECT users.*
FROM users
INNER JOIN user_friends ON users.user_id = user_friends.friend_id
WHERE user_friends.user_id = ?

INTERSECT

SELECT users.*
FROM users
INNER JOIN user_friends ON users.user_id = user_friends.friend_id
WHERE user_friends.user_id = ?
```

</details>
<details>
  <summary><h3>Для фильмов:</h3></summary>

* создание фильма
```SQL
INSERT INTO films (name, description, release_date, duration_in_minutes, mpa_id)
VALUES (?, ?, ?, ?, ?)
```
* редактирование фильма
```SQL
UPDATE films
SET name = ?,
    description = ?,
    release_date = ?,
    duration_in_minutes = ?,
    mpa_id = ?
WHERE film_id = ?
```
* получение списка всех фильмов
```SQL
SELECT * FROM films
```
* получение информации о фильме по его `id`
```SQL
SELECT * FROM films f  WHERE f.film_id = ?
```
* пользователь ставит лайк фильму
```SQL
INSERT INTO film_likes(film_id, user_id) VALUES (?, ?)
```
* пользователь удаляет лайк
```SQL
DELETE
FROM film_likes
WHERE film_id = ? AND user_id = ?
```
* возвращает список из первых `count` фильмов по количеству лайков
```SQL
SELECT * FROM films ORDER BY number_of_likes DESC LIMIT ?
```

</details>
<details>
  <summary><h3>Для жанров:</h3></summary>

* получение списка всех жанров
```SQL
SELECT *
FROM genres
ORDER BY genre_id
```
* получение информации о жанре по его `id`
```SQL
SELECT *
FROM genres
WHERE genre_id = ?
```

</details>
<details>
  <summary><h3>Для рейтингов:</h3></summary>

* получение списка всех рейтингов
```SQL
SELECT *
FROM mpa
ORDER BY mpa_id
```
* получение информации о рейтинге по его `id`
```SQL
SELECT *
FROM mpa
WHERE mpa_id = ?
```

</details>

<details>
  <summary><h3>Для отзывов:</h3></summary>

* создание отзыва
```SQL
insert into events (timestamp, user_id,event_type, operation, entity_id) 
values (?, ?, ?, ?, ?)
```
* редактирование отзыва
```SQL
UPDATE film_reviews SET content = ?, is_positive = ? 
WHERE review_id = ?
```
* получение списка всех отзывов
```SQL
SELECT * FROM films
```
* получение информации о фильме по его `id`
```SQL
SELECT * FROM films f  WHERE f.film_id = ?
```
* пользователь ставит лайк фильму
```SQL
INSERT INTO film_likes(film_id, user_id) VALUES (?, ?)
```
* пользователь удаляет лайк
```SQL
DELETE
FROM film_likes
WHERE film_id = ? AND user_id = ?
```
* возвращает список из первых `count` фильмов по количеству лайков
```SQL
SELECT * FROM films ORDER BY number_of_likes DESC LIMIT ?
```

</details>
package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;

@Component
@RequiredArgsConstructor
public class LikesDAO {

    private final JdbcTemplate jdbcTemplate;

    public void putLike(Integer id, Integer userId) {
        String sqlQuery = "insert into film_likes(FILM_ID, USER_ID) values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);

        setRate(id);
    }

    public void deleteLike(Integer id, Integer userId) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? and USER_ID = ?";
        if (jdbcTemplate.update(sqlQuery, id, userId) < 1) {
            throw new ObjectNotFoundException(String.format("Пользователь с id=%d не ставил лайк фильму с id=%d.", userId, id));
        }

       setRate(id);
    }

    private void setRate(Integer filmId) {
        String sqlQuery = "update FILMS f set rate = (select count(l.user_id) " +
                "from FILM_LIKES l where l.film_id = f.film_id) where film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

}

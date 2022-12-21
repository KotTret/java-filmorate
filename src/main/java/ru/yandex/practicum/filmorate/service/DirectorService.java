package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {

    private final DirectorStorage directorStorage;

    public List<Director> findAll() {
        List<Director> directors = directorStorage.findAll();
        log.info("Текущее количество режиссёров: {}", directors.size());
        return directors;
    }

    public Director getById(Integer id) {
        Director director = directorStorage.get(id);
        log.info("Запрошена информация о режиссёре: {}", director.getName());
        return director;
    }

    public Director create(Director director) {
        directorStorage.add(director);
        log.info("Добавлен режиссёр: {}", director.getName());
        return director;
    }

    public Director update(Director director) {
        if (director.getId() == null) {
            throw  new ObjectNotFoundException("Идентификатор режиссёра отсутствует, невозможно обновить данные. " +
                    "режиссёр не найден");
        }
        directorStorage.update(director);
        log.info("Информация о режиссёре обнолвена: {}", director.getName());
        return director;
    }

    public void delete(Integer id) {
        checkDirector(id);
        directorStorage.delete(id);
    }

    private void checkDirector(Integer id) {
        if (!directorStorage.containsId(id)) {
            throw new UserNotFoundException("Режиссёр не найден, проверьте верно ли указан Id");
        }
    }
}

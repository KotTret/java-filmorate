package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Create;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Update;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public List<Director> findAll() {
        return directorService.findAll();
    }

    @GetMapping(value = "/{id}")
    public Director read(@Valid @PathVariable Integer id) {
        return directorService.getById(id);
    }

    @PostMapping
    public Director create(@Validated(Create.class) @RequestBody Director director){
        return directorService.create(director);
    }

    @PutMapping()
    public Director put(@Validated(Update.class) @RequestBody Director director) {
        return directorService.update(director);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@Valid @PathVariable Integer id) {
         directorService.delete(id);
    }
}

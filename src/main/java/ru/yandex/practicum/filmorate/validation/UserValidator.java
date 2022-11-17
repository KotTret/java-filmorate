package ru.yandex.practicum.filmorate.validation;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Component
public class UserValidator {

    private final static EmailValidator emailValidator =  EmailValidator.getInstance();

    public void validate(User user)  {

        if (!emailValidator.isValid(user.getEmail())) {
            throw new ValidationException("Неверно указан адрес электронной почты.");
        }

        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}

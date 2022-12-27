package ru.yandex.practicum.filmorate.exception;

public class ObjectExistsException extends RuntimeException{
    public ObjectExistsException() {
    }

    public ObjectExistsException(String message) {
        super(message);
    }

    public ObjectExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

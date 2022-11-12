package ru.yandex.practicum.filmorate.exception;

public class UpdateDataException extends RuntimeException{

    public UpdateDataException() {
    }

    public UpdateDataException(String message) {
        super(message);
    }

    public UpdateDataException(String message, Throwable cause) {
        super(message, cause);
    }
}

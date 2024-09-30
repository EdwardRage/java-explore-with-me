package ru.practicum.ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse errorHandle(final CustomDateTimeException e) {
        log.info("error " + e.getMessage());
        return new ErrorResponse("Ошибка: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse errorHandle(final Exception e) {
        log.info("error " + e.getMessage());
        return new ErrorResponse("Ошибка: ", e.getMessage());
    }
}

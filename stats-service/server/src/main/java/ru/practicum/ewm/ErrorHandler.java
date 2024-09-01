package ru.practicum.ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ErrorResponse errorHandle(final Throwable e) {
        log.info("error " + e.getMessage());
        return new ErrorResponse("Ошибка: ", e.getMessage());
    }
}

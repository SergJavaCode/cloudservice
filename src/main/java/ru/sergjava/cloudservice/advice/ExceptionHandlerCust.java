package ru.sergjava.cloudservice.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.sergjava.cloudservice.dto.ErrorDto;
import ru.sergjava.cloudservice.exceptions.BadRequestExceptionCust;
import ru.sergjava.cloudservice.exceptions.UnauthorizedExceptionCust;

@RestControllerAdvice
public class ExceptionHandlerCust {
    @ExceptionHandler(BadRequestExceptionCust.class)
    public ResponseEntity<ErrorDto> credentialsHandler(BadRequestExceptionCust e) {
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedExceptionCust.class)
    public ResponseEntity<ErrorDto> unauthorizedCustHandler(UnauthorizedExceptionCust e) {
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

}

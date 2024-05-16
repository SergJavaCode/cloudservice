package ru.sergjava.cloudservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)

public class BadRequestExceptionCust extends RuntimeException{
    public BadRequestExceptionCust(String msg) {
        super(msg);
    }
}

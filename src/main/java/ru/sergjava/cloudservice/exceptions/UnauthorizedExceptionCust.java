package ru.sergjava.cloudservice.exceptions;

public class UnauthorizedExceptionCust extends RuntimeException{
    public UnauthorizedExceptionCust(String msg) {
        super(msg);
    }
}
package ru.sergjava.cloudservice.exceptions;

public class InternalServerErrorCust extends RuntimeException{
    public InternalServerErrorCust(String msg) {
        super(msg);
    }
}
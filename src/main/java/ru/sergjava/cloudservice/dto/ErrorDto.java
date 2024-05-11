package ru.sergjava.cloudservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class ErrorDto {
    public ErrorDto(String message) {
        this.message = message;
        this.id = Math.abs(UUID.randomUUID().hashCode());
    }

    private String message;
    private Integer id;
}

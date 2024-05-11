package ru.sergjava.cloudservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sergei Iurochkin
 */
@Getter
@Setter
@ToString
public class UserDto {
    private String login;
    private String password;
}

package ru.sergjava.cloudservice.dto;

import lombok.*;

/**
 * @author Sergei Iurochkin
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String login;
    private String password;
}

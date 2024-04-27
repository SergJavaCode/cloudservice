package ru.sergjava.cloudservice.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken {
   @JsonSetter("auth-token")
    public String authToken;

    @JsonGetter("auth-token")
    public String getAuthToken() {
        return authToken;
    }
}

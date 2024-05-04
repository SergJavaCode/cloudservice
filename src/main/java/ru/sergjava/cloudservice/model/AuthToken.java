package ru.sergjava.cloudservice.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class AuthToken {

    @JsonIgnore
    private String userName;
    @JsonIgnore
    private String authToken;

    @JsonGetter("auth-token")
    public String getToken() {
        return authToken;
    }

}

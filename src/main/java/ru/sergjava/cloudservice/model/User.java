package ru.sergjava.cloudservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    private String username;

    private Boolean enabled;
    private String password;
    private String bucket;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "username", cascade = CascadeType.REMOVE)
    private List<Authorities> authorities;
    @Transient
    private String authToken;

}

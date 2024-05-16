package ru.sergjava.cloudservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Authorities {
    @Id
    private String username;
    private String authority;
}

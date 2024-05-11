package ru.sergjava.cloudservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Authorities {
    @Id
    private String username;
    private String authority;
}

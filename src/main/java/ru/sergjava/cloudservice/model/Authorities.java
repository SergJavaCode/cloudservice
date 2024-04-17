package ru.sergjava.cloudservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
public class Authorities {
    @Id
    private String username;
    private String authority;
}

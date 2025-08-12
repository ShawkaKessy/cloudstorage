package ru.netology.cloudstorage.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(name = "auth_token")
    private String authToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FileEntity> files;
}

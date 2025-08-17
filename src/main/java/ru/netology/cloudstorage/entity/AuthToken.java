package ru.netology.cloudstorage.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name="auth_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken {
    @Id
    private String token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}

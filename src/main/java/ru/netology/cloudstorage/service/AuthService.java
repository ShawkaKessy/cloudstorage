package ru.netology.cloudstorage.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String login, String password) {
        Optional<User> userOpt = userRepository.findByLogin(login);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = UUID.randomUUID().toString();
                user.setAuthToken(token);
                userRepository.save(user);
                return token;
            }
        }
        throw new RuntimeException("Invalid credentials");
    }

    public void logout(String token) {
        userRepository.findByAuthToken(token).ifPresent(user -> {
            user.setAuthToken(null);
            userRepository.save(user);
        });
    }

    public Optional<User> findByToken(String token) {
        return userRepository.findByAuthToken(token);
    }

    public User getUserByToken(String token) {
        return userRepository.findByAuthToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
    }
}

package ru.netology.cloudstorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudstorage.dto.LoginRequest;
import ru.netology.cloudstorage.dto.LoginResponse;
import ru.netology.cloudstorage.dto.RegisterRequest;
import ru.netology.cloudstorage.entity.User;
import ru.netology.cloudstorage.repository.UserRepository;
import ru.netology.cloudstorage.service.AuthService;
import ru.netology.cloudstorage.util.PasswordUtil;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByLogin(request.login()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        User user = new User();
        user.setLogin(request.login());
        user.setPassword(PasswordUtil.hash(request.password()));
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.login(), request.password());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("auth-token") String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }
}

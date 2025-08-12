package ru.netology.cloudstorage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudstorage.model.AuthRequest;
import ru.netology.cloudstorage.service.AuthService;

import java.util.Map;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest authRequest) {
        try {
            String token = authService.login(authRequest.getLogin(), authRequest.getPassword());
            return ResponseEntity.ok(Map.of("auth-token", token));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("auth-token") String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }
}

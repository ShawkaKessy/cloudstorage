package ru.netology.cloudstorage.service;

import ru.netology.cloudstorage.dto.AuthRequest;
import ru.netology.cloudstorage.dto.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
}

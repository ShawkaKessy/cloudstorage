package ru.netology.cloudstorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.entity.User;
import ru.netology.cloudstorage.repository.UserRepository;
import ru.netology.cloudstorage.util.PasswordUtil;
import ru.netology.cloudstorage.util.TokenUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final Map<String, User> tokens = new ConcurrentHashMap<>();

    @Override
    public String login(String login, String password) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!user.getPassword().equals(PasswordUtil.hash(password))) {
            throw new RuntimeException("Неверный пароль");
        }

        String token = TokenUtil.generateToken();
        tokens.put(token, user);
        return token;
    }

    @Override
    public User getUserByToken(String token) {
        User user = tokens.get(token);
        if (user == null) throw new RuntimeException("Неверный токен");
        return user;
    }

    @Override
    public void logout(String token) {
        tokens.remove(token);
    }
}

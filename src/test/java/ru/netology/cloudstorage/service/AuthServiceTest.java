package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.Test;
import ru.netology.cloudstorage.entity.User;
import ru.netology.cloudstorage.repository.UserRepository;
import ru.netology.cloudstorage.util.PasswordUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Test
    void login_ok() {
        UserRepository ur = mock(UserRepository.class);
        AuthService svc = new AuthServiceImpl(ur);

        User u = new User();
        u.setLogin("user");
        u.setPassword(PasswordUtil.hash("pass"));

        when(ur.findByLogin("user")).thenReturn(Optional.of(u));

        String token = svc.login("user", "pass");
        assertNotNull(token);
    }

    @Test
    void login_bad_credentials() {
        UserRepository ur = mock(UserRepository.class);
        AuthService svc = new AuthServiceImpl(ur);

        when(ur.findByLogin("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> svc.login("user","pass"));
    }
}

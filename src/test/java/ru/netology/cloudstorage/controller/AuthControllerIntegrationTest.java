package ru.netology.cloudstorage.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import ru.netology.cloudstorage.dto.AuthRequest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void loginReturnsToken() {
        AuthRequest req = new AuthRequest();
        req.setLogin("user");
        req.setPassword("password");

        ResponseEntity<Map> resp = rest.postForEntity("/auth/login", req, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).containsKey("auth-token");
        assertThat(resp.getBody().get("auth-token")).asString().isNotBlank();
    }
}

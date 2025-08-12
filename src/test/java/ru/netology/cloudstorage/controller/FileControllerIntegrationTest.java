package ru.netology.cloudstorage.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.netology.cloudstorage.model.AuthRequest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String token;

    @BeforeEach
    void login() {
        AuthRequest authRequest = new AuthRequest("user", "password");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthRequest> request = new HttpEntity<>(authRequest, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("/login", request, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        this.token = (String) response.getBody().get("auth-token");
        assertThat(token).isNotBlank();
    }


    @Test
    void upload_list_download_delete_file() {
        // ---------- 1. Загрузка файла ----------
        HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", token);  // заголовок auth-token
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource fileAsResource = new ByteArrayResource("Привет, файл!".getBytes()) {
            @Override
            public String getFilename() {
                return "test.txt";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileAsResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Void> uploadResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/file?filename=test.txt",
                requestEntity,
                Void.class
        );
        assertThat(uploadResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // ---------- 2. Получение списка ----------
        headers = new HttpHeaders();
        headers.set("auth-token", token);
        HttpEntity<Void> listRequest = new HttpEntity<>(headers);

        ResponseEntity<String> listResponse = restTemplate.exchange(
                "http://localhost:" + port + "/list?limit=10",
                HttpMethod.GET,
                listRequest,
                String.class
        );
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).contains("test.txt");

        // ---------- 3. Скачивание ----------
        ResponseEntity<byte[]> downloadResponse = restTemplate.exchange(
                "http://localhost:" + port + "/file?filename=test.txt",
                HttpMethod.GET,
                listRequest,
                byte[].class
        );
        assertThat(downloadResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(new String(downloadResponse.getBody())).isEqualTo("Привет, файл!");

        // ---------- 4. Удаление ----------
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "http://localhost:" + port + "/file?filename=test.txt",
                HttpMethod.DELETE,
                listRequest,
                Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

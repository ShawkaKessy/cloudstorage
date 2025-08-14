package ru.netology.cloudstorage.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.boot.test.web.client.TestRestTemplate;
import ru.netology.cloudstorage.dto.AuthRequest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileControllerIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    private String token;

    @BeforeEach
    void login() {
        AuthRequest req = new AuthRequest();
        req.setLogin("user");
        req.setPassword("password");

        ResponseEntity<Map> r = rest.postForEntity("/auth/login", req, Map.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        token = (String) r.getBody().get("auth-token");
        assertThat(token).isNotBlank();
    }

    @Test
    void upload_list_download_delete() {
        // upload
        HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource resource = new ByteArrayResource("hello".getBytes()) {
            @Override public String getFilename() { return "test.txt"; }
        };
        MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);

        ResponseEntity<Void> up = rest.postForEntity("/file/upload?filename=test.txt", new HttpEntity<>(body, headers), Void.class);
        assertThat(up.getStatusCode()).isEqualTo(HttpStatus.OK);

        // list
        ResponseEntity<List> list = rest.exchange("/file/list?limit=10",
                HttpMethod.GET, new HttpEntity<>(headers), List.class);
        assertThat(list.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(list.getBody().toString()).contains("test.txt");

        // download
        ResponseEntity<byte[]> dl = rest.exchange("/file/download?filename=test.txt",
                HttpMethod.GET, new HttpEntity<>(headers), byte[].class);
        assertThat(dl.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(new String(dl.getBody())).isEqualTo("hello");

        // delete
        ResponseEntity<Void> del = rest.exchange("/file/delete?filename=test.txt",
                HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
        assertThat(del.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

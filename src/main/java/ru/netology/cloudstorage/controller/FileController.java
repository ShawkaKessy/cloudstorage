package ru.netology.cloudstorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudstorage.dto.FileResponse;
import ru.netology.cloudstorage.entity.User;
import ru.netology.cloudstorage.service.AuthService;
import ru.netology.cloudstorage.service.FileService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final AuthService authService;

    private User auth(String token) {
        return authService.getUserByToken(token);
    }

    @PostMapping("/file")
    public ResponseEntity<Void> uploadFile(@RequestHeader("auth-token") String token,
                                           @RequestParam String filename,
                                           @RequestBody byte[] content) {
        fileService.uploadFile(auth(token), filename, content);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> downloadFile(@RequestHeader("auth-token") String token,
                                               @RequestParam String filename) {
        return ResponseEntity.ok(fileService.downloadFile(auth(token), filename));
    }

    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(@RequestHeader("auth-token") String token,
                                           @RequestParam String filename) {
        fileService.deleteFile(auth(token), filename);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/file")
    public ResponseEntity<Void> renameFile(@RequestHeader("auth-token") String token,
                                           @RequestParam String oldName,
                                           @RequestParam String newName) {
        fileService.renameFile(auth(token), oldName, newName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileResponse>> listFiles(@RequestHeader("auth-token") String token) {
        return ResponseEntity.ok(fileService.listFiles(auth(token)));
    }
}

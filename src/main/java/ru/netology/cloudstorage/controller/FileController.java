package ru.netology.cloudstorage.controller;

import java.util.HashMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.model.FileEntity;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.service.AuthService;
import ru.netology.cloudstorage.service.FileService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class FileController {

    private final FileService fileService;
    private final AuthService authService;

    public FileController(FileService fileService, AuthService authService) {
        this.fileService = fileService;
        this.authService = authService;
    }

    private User getUserByToken(String token) {
        return authService.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));
    }

    @PostMapping("/file")
    public ResponseEntity<Void> uploadFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename,
            @RequestParam("file") MultipartFile file) throws IOException {
        User user = getUserByToken(token);
        fileService.uploadFile(user, filename, file);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename) throws IOException {
        User user = getUserByToken(token);
        fileService.deleteFile(user, filename);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> downloadFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename) throws IOException {
        User user = getUserByToken(token);
        byte[] fileData = fileService.downloadFile(user, filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(fileData);
    }

    @PutMapping("/file")
    public ResponseEntity<Void> renameFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String oldFilename,
            @RequestBody Map<String, String> request) throws IOException {
        String newFilename = request.get("filename");
        if (newFilename == null || newFilename.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        User user = getUserByToken(token);
        fileService.renameFile(user, oldFilename, newFilename);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> listFiles(
            @RequestHeader("auth-token") String token,
            @RequestParam("limit") int limit) {
        User user = getUserByToken(token);
        List<FileEntity> files = fileService.listFiles(user);
        List<Map<String, Object>> result = files.stream()
                .limit(limit)
                .map(file -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("filename", file.getFilename());
                    map.put("size", file.getSize());
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}

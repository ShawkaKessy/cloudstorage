package ru.netology.cloudstorage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.FileRenameRequest;
import ru.netology.cloudstorage.dto.FileResponseDto;

import java.util.List;

public interface FileController {
    @PostMapping("/upload")
    void upload(@RequestParam MultipartFile file, @RequestParam String token);

    @DeleteMapping("/delete")
    void delete(@RequestParam String filename, @RequestParam String token);

    @PutMapping("/rename")
    void rename(@RequestBody FileRenameRequest body, @RequestParam String token);

    @GetMapping("/list")
    List<FileResponseDto> list(@RequestParam String token);

    @GetMapping("/download")
    byte[] download(@RequestParam String filename, @RequestParam String token);

    ResponseEntity<?> rename(@RequestHeader("auth-token") String authToken,
                             @RequestBody FileRenameRequest request);
    // другие методы
}
package ru.netology.cloudstorage.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.model.FileEntity;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.FileRepository;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final Path root = Paths.get("files");

    public FileService(FileRepository fileRepository) throws IOException {
        this.fileRepository = fileRepository;
        Files.createDirectories(root);
    }

    private void validateFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename is empty");
        }
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename");
        }
    }

    private Path getUserDir(User user) throws IOException {
        Path userDir = root.resolve(String.valueOf(user.getId()));
        if (!Files.exists(userDir)) {
            Files.createDirectories(userDir);
        }
        return userDir;
    }

    public void uploadFile(User user, String filename, MultipartFile file) throws IOException {
        validateFilename(filename);
        Path userDir = getUserDir(user);
        Path filePath = userDir.resolve(filename);
        Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        Optional<FileEntity> existingFile = fileRepository.findByUserAndFilename(user, filename);
        FileEntity entity;
        if (existingFile.isPresent()) {
            entity = existingFile.get();
            entity.setSize(file.getSize());
            entity.setUploadTime(LocalDateTime.now());
        } else {
            entity = new FileEntity();
            entity.setUser(user);
            entity.setFilename(filename);
            entity.setSize(file.getSize());
            entity.setUploadTime(LocalDateTime.now());
        }
        fileRepository.save(entity);
    }

    public void deleteFile(User user, String filename) throws IOException {
        validateFilename(filename);
        FileEntity entity = fileRepository.findByUserAndFilename(user, filename)
                .orElseThrow(() -> new RuntimeException("File not found"));
        Path userDir = getUserDir(user);
        Path filePath = userDir.resolve(filename);
        Files.deleteIfExists(filePath);
        fileRepository.delete(entity);
    }

    public byte[] downloadFile(User user, String filename) throws IOException {
        validateFilename(filename);
        FileEntity entity = fileRepository.findByUserAndFilename(user, filename)
                .orElseThrow(() -> new RuntimeException("File not found"));
        Path userDir = getUserDir(user);
        Path filePath = userDir.resolve(entity.getFilename());
        if (!Files.exists(filePath)) throw new RuntimeException("File not found on disk");
        return Files.readAllBytes(filePath);
    }

    public void renameFile(User user, String oldFilename, String newFilename) throws IOException {
        validateFilename(oldFilename);
        validateFilename(newFilename);
        FileEntity entity = fileRepository.findByUserAndFilename(user, oldFilename)
                .orElseThrow(() -> new RuntimeException("File not found"));
        Path userDir = getUserDir(user);
        Path oldPath = userDir.resolve(oldFilename);
        Path newPath = userDir.resolve(newFilename);
        if (Files.exists(newPath)) {
            throw new RuntimeException("File with new name already exists");
        }
        Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
        entity.setFilename(newFilename);
        fileRepository.save(entity);
    }

    public List<FileEntity> listFiles(User user) {
        return fileRepository.findAllByUser(user);
    }
}

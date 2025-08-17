package ru.netology.cloudstorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.dto.FileResponse;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.entity.User;
import ru.netology.cloudstorage.repository.FileRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Override
    public void uploadFile(User user, String filename, byte[] content) {
        FileEntity file = new FileEntity();
        file.setOwner(user);
        file.setFilename(filename);
        file.setContent(content);
        fileRepository.save(file);
    }

    @Override
    public byte[] downloadFile(User user, String filename) {
        FileEntity file = fileRepository.findByOwnerAndFilename(user, filename)
                .orElseThrow(() -> new RuntimeException("Файл не найден"));
        return file.getContent();
    }

    @Override
    public void deleteFile(User user, String filename) {
        FileEntity file = fileRepository.findByOwnerAndFilename(user, filename)
                .orElseThrow(() -> new RuntimeException("Файл не найден"));
        fileRepository.delete(file);
    }

    @Override
    public void renameFile(User user, String oldName, String newName) {
        FileEntity file = fileRepository.findByOwnerAndFilename(user, oldName)
                .orElseThrow(() -> new RuntimeException("Файл не найден"));
        file.setFilename(newName);
        fileRepository.save(file);
    }

    @Override
    public List<FileResponse> listFiles(User user) {
        return fileRepository.findByOwner(user).stream()
                .map(f -> new FileResponse(f.getFilename(), f.getContent().length))
                .collect(Collectors.toList());
    }
}

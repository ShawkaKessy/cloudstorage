package ru.netology.cloudstorage.service;

import ru.netology.cloudstorage.dto.FileResponse;
import ru.netology.cloudstorage.entity.User;

import java.util.List;

public interface FileService {
    void uploadFile(User user, String filename, byte[] content);
    byte[] downloadFile(User user, String filename);
    void deleteFile(User user, String filename);
    void renameFile(User user, String oldName, String newName);
    List<FileResponse> listFiles(User user);
}

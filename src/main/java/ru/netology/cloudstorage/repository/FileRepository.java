package ru.netology.cloudstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.cloudstorage.model.FileEntity;
import ru.netology.cloudstorage.model.User;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findAllByUser(User user);
    Optional<FileEntity> findByUserAndFilename(User user, String filename);
    void deleteByUserAndFilename(User user, String filename);
}

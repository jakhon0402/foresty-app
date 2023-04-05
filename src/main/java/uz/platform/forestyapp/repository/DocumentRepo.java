package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.Document;

import java.util.List;
import java.util.UUID;

public interface DocumentRepo extends JpaRepository<Document, UUID> {
    List<Document> findAllByUserId(UUID id);

    void deleteAllByUserId(UUID id);
}

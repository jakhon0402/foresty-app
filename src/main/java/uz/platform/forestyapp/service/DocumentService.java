package uz.platform.forestyapp.service;

import org.springframework.web.multipart.MultipartFile;
import uz.platform.forestyapp.entity.Document;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface DocumentService {
    ApiResponse addDocument(MultipartFile file,User currentUser) throws IOException;
    ApiResponse deleteDocument(UUID id,User currentUser);
    ApiResponse getDocuments(UUID userId,User currentUser);
}

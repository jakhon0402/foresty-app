package uz.platform.forestyapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.security.CurrentUser;
import uz.platform.forestyapp.service.DocumentService;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/document")
public class DocumentController {
    @Autowired
    DocumentService documentService;

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR')")
    @GetMapping("/{userId}")
    public HttpEntity<?> getDocuments(@PathVariable("userId")UUID id, @CurrentUser User user){
        ApiResponse apiResponse = documentService.getDocuments(id, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR')")
    @PostMapping
    public HttpEntity<?> uploadDocument(@RequestParam("file")MultipartFile multipartFile, @CurrentUser User user) throws IOException {
        ApiResponse apiResponse = documentService.addDocument(multipartFile, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR')")
    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteDocument(@PathVariable("id")UUID id, @CurrentUser User user) {
        ApiResponse apiResponse = documentService.deleteDocument(id, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }
}

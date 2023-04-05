package uz.platform.forestyapp.service.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.platform.forestyapp.entity.Attachment;
import uz.platform.forestyapp.entity.AttachmentContent;
import uz.platform.forestyapp.entity.Document;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.entity.enums.FileTypeName;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.repository.AttachmentContentRepo;
import uz.platform.forestyapp.repository.AttachmentRepo;
import uz.platform.forestyapp.repository.DocumentRepo;
import uz.platform.forestyapp.repository.UserRepo;
import uz.platform.forestyapp.service.DocumentService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    AttachmentRepo attachmentRepo;

    @Autowired
    AttachmentContentRepo attachmentContentRepo;

    @Autowired
    DocumentRepo documentRepo;

    @Override
    public ApiResponse addDocument(MultipartFile file,User currentUser) throws IOException {
        if(!file.isEmpty()){
            String originalFilename = file.getOriginalFilename();
            long size = file.getSize();
            String contentType = file.getContentType();
            Attachment attachment = new Attachment();
            AttachmentContent attachmentContent = new AttachmentContent();

            attachment.setOriginalName(originalFilename);
            attachment.setContentType(contentType);
            attachment.setSize(size);
            attachment.setTypeName(FileTypeName.DOCUMENT);
            attachmentContent.setBytes(file.getBytes());

            attachment.setAttachmentContent(attachmentContent);
            Document document = Document.builder()
                    .user(currentUser)
                    .file(attachment)
                    .build();
            documentRepo.save(document);
            return new ApiResponse("Yangi hujjat saqlandi!",true);
        }
        else {
            return new ApiResponse("Fayl bo'sh bo'lmasligi kerak!",false);
        }
    }

    @Override
    public ApiResponse deleteDocument(UUID id, User currentUser) {
        Optional<Document> optionalDocument = documentRepo.findById(id);
        if(optionalDocument.isEmpty()){
            return new ApiResponse("Bunday idlik hujjat mavjud emas!",false);
        }
        Document document = optionalDocument.get();
        if(!currentUser.getEducationCenter().getId().equals(document.getUser().getEducationCenter().getId())){
            return new ApiResponse("Ushbu hujjat joriy o'quv markazga tegishli emas!",false);
        }
        documentRepo.delete(document);
        return new ApiResponse("Hujjat o'chirildi!",true);
    }

    @Override
    public ApiResponse getDocuments(UUID userId, User currentUser) {
        Optional<User> optionalUser = userRepo.findById(userId);
        if(optionalUser.isEmpty()){
            return new ApiResponse("Bunday idlik user mavjud emas!",false);
        }
        User user = optionalUser.get();
        if(!currentUser.getEducationCenter().getId().equals(user.getEducationCenter().getId())){
            return new ApiResponse("Ushbu hujjat joriy o'quv markazga tegishli emas!",false);
        }
        List<Document> documentList = documentRepo.findAllByUserId(userId);
        return new ApiResponse(documentList,true);
    }
}

package uz.platform.forestyapp.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.platform.forestyapp.entity.Attachment;
import uz.platform.forestyapp.entity.AttachmentContent;
import uz.platform.forestyapp.entity.enums.FileTypeName;
import uz.platform.forestyapp.repository.AttachmentContentRepo;
import uz.platform.forestyapp.repository.AttachmentRepo;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AttachmentController {
    @Autowired
    AttachmentRepo attachmentRepo;

    @Autowired
    AttachmentContentRepo attachmentContentRepo;

    @GetMapping("/image/{id}")
    public void getImage(@PathVariable UUID id, HttpServletResponse response) throws IOException {
        Optional<Attachment> optionalAttachment = attachmentRepo.findById(id);
        if(optionalAttachment.isPresent()){
            Attachment attachment = optionalAttachment.get();
            if(attachment.getTypeName().equals(FileTypeName.IMAGE)){
            Optional<AttachmentContent> optionalAttachmentContent = attachmentContentRepo.findById(attachment.getAttachmentContent().getId());
            if(optionalAttachmentContent.isPresent()){
                AttachmentContent attachmentContent = optionalAttachmentContent.get();
                response.setHeader("Content-Disposition", "attachment; filename=\""+attachment.getOriginalName()+"\"");
                response.setContentType(attachment.getContentType());
                FileCopyUtils.copy(attachmentContent.getBytes(),response.getOutputStream());
            }
          }
        }
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR')")
    @GetMapping("/download/document/{id}")
    public void downloadDocument(@PathVariable UUID id, HttpServletResponse response) throws IOException {
        Optional<Attachment> optionalAttachment = attachmentRepo.findById(id);
        if(optionalAttachment.isPresent()){
            Attachment attachment = optionalAttachment.get();
            if(attachment.getTypeName().equals(FileTypeName.DOCUMENT)){
                Optional<AttachmentContent> optionalAttachmentContent = attachmentContentRepo.findById(attachment.getAttachmentContent().getId());
                if(optionalAttachmentContent.isPresent()){
                    AttachmentContent attachmentContent = optionalAttachmentContent.get();
                    response.addHeader("Content-Disposition", "attachment; filename=\""+attachment.getOriginalName()+"\"");
                    response.setContentType(attachment.getContentType());
                    FileCopyUtils.copy(attachmentContent.getBytes(),response.getOutputStream());
                }
            }
        }
    }
}

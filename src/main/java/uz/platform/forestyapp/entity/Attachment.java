package uz.platform.forestyapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.enums.FileTypeName;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Attachment extends AbsUUIDEntity {
    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private FileTypeName typeName;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER,cascade = {CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE,CascadeType.ALL})
    private AttachmentContent attachmentContent;
}

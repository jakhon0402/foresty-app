package uz.platform.forestyapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

@Entity(name="documents")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Document extends AbsUUIDEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE})
    private Attachment file;
}

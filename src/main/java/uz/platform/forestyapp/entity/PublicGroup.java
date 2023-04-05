package uz.platform.forestyapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.enums.PublicGroupStatusName;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PublicGroup extends AbsUUIDEntity {
    @Column
    private String description;

    @Column
    private PublicGroupStatusName status;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private Attachment headerImage;

}

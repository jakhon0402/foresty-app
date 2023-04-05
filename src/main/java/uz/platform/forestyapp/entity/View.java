package uz.platform.forestyapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
@Entity
public class View extends AbsUUIDEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private PublicGroup publicGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}

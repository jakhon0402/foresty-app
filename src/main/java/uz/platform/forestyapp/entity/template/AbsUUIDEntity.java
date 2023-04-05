package uz.platform.forestyapp.entity.template;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.util.UUID;

@Data
@MappedSuperclass
public abstract class AbsUUIDEntity extends AbsMainEntity {
    @Id
    @GeneratedValue
    private UUID id;
}

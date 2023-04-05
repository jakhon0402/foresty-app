package uz.platform.forestyapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Order extends AbsUUIDEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Client client;

    @Column(nullable = false)
    private Integer orderSum;

    private boolean cancelled;

    private boolean paid;

    private boolean delivered;
}

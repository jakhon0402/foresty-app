package uz.platform.forestyapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.template.AbsLongEntity;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderTransaction extends AbsUUIDEntity {
    //PAYME TOMONIDAN BERILADIGAN ID
    private String transactionId;

    private Timestamp transactionCreationTime;

    private Timestamp performTime;

    private Timestamp cancelTime;

    private Integer reason;

    private Integer state;

    @JoinColumn(insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    public OrderTransaction(String transactionId, Timestamp transactionCreationTime, Integer state, UUID orderId) {
        this.transactionId = transactionId;
        this.transactionCreationTime = transactionCreationTime;
        this.state = state;
        this.orderId = orderId;
    }
}

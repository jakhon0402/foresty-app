package uz.platform.forestyapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.template.AbsLongEntity;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payment extends AbsLongEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Client client;

    //USHBU TO'LOVDAN QANCHA TISHGANI
    @Column(nullable = false)
    private Double paySum;

    //USHBU TO'LOVDAN QANCHA QOLGANI
    @Column(nullable = false)
    private Double leftoverSum;

    private Timestamp payDate = new Timestamp(System.currentTimeMillis());

    private UUID orderTransactionId;

    private String transactionId;

    private Boolean cancelled = false;

    public Payment(Client client, Double paySum, Timestamp payDate, UUID orderTransactionId, String transactionId) {
        this.client = client;
        this.paySum = paySum;
        this.leftoverSum = leftoverSum;
        this.payDate = payDate;
        this.orderTransactionId = orderTransactionId;
        this.transactionId = transactionId;
    }
}

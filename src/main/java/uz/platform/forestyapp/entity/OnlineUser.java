package uz.platform.forestyapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.template.AbsLongEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OnlineUser extends AbsLongEntity {
    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String sessionId;
}

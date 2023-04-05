package uz.platform.forestyapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EducationCenter extends AbsUUIDEntity implements Serializable {
    @Column(nullable = false,length = 50)
    private String name;

    @Column(nullable = false,length = 20)
    private String username;

    @Column(nullable = false,length = 13)
    private String phoneNumber;

    @Column(nullable = false)
    private long balance;

    @OneToOne(cascade = {CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE})
    private Address address;

    @OneToOne
    private CurrentPlan currentPlan;

    @OneToOne(cascade = {CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE})
    private Attachment logo;

    @OneToOne(cascade = {CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE})
    private Attachment headerImage;

    @Column
    private String description;

    @Column
    private String telegram;
    @Column
    private String instagram;
    @Column
    private String twitter;
    @Column
    private String youtube;
    @Column
    private String facebook;
    @Column
    private String website;

}

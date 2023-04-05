package uz.platform.forestyapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.platform.forestyapp.entity.enums.CurrentPlanStatus;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

import java.io.Serializable;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User extends AbsUUIDEntity implements UserDetails, Serializable {

    @Column(length = 20)
    private String firstName;

    @Column(length = 20)
    private String lastName;

    @Column(length = 20)
    private String fatherName;

    @Column(length = 50)
    private String email;

    @Column(length = 30,unique = true)
    private String username;

    @JsonIgnore
    @Column
    private String password;

    @Column(length = 9)
    private String phoneNumber;

    @Column
    private Date birthDate;

    @OneToOne
    private Role role;

    @OneToOne(cascade = {CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE})
    private Address address;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy = "user",fetch = FetchType.LAZY)
    private List<Document> documents;

    @JsonIgnore
    @ManyToOne
    private EducationCenter educationCenter;

    @JsonIgnore
    @Column
    private String emailCode;

    @JsonIgnore
    @Column
    private ZonedDateTime emailCodeCreatedAt;

    @OneToOne(cascade = {CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE})
    private Attachment avatar;

    @Transient
    private String educationCenterName;

    @Transient
    private UUID centerLogoId;

    @Transient
    private CurrentPlanStatus planStatus;

    @JsonIgnore
    private boolean accountNonExpired=true;

    @JsonIgnore
    private boolean accountNonLocked=true;

    @JsonIgnore
    private boolean credentialsNonExpired=true;

//    @JsonIgnore
    private boolean enabled=false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(this.role);
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @PostLoad
    private void setCenterData(){
        if(this.educationCenter != null){
        this.educationCenterName = this.educationCenter.getName();
        if(this.educationCenter.getLogo()!=null){
            this.centerLogoId = this.educationCenter.getLogo().getId();
        }
        if(this.educationCenter.getCurrentPlan()!=null){
        this.planStatus = this.educationCenter.getCurrentPlan().getStatus();}}
    }
}

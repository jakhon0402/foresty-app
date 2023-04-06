package uz.platform.forestyapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.platform.forestyapp.entity.*;
import uz.platform.forestyapp.entity.enums.ColorName;
import uz.platform.forestyapp.entity.enums.RoleName;
import uz.platform.forestyapp.repository.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uz.platform.forestyapp.entity.enums.ColorName.*;

@Configuration
public class FirstLoadConfig {
    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EducationCenterRepo educationCenterRepo;

    @Autowired
    SubjectRepo subjectRepo;

    @Autowired
    ClientRepo clientRepo;

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            addRoles();
            addSuperAdmin();
            addForestySubjects();
        };
    }

    private void addRoles(){
        if(roleRepo.count()==0){
            for (RoleName roleName : RoleName.values()) {
                Role role = new Role(roleName);
                roleRepo.save(role);
            }
        }
    }

    private void addSuperAdmin() {
        Optional<Role> optionalRole = roleRepo.findByRoleName(RoleName.SUPER_ADMIN);
        Optional<User> optionalUser = userRepo.findByRoleRoleName(RoleName.SUPER_ADMIN);
        if(optionalRole.isPresent()) {
            return;
        }
        User user = new User();
        user.setEmail("jakhon99dev@gmail.com");
        user.setUsername("jakhon99dev");
        user.setFirstName("Jakhongir");
        user.setLastName("Egamberdiyev");
        user.setBirthDate(Date.valueOf(LocalDate.of(2001,2,4)));
        user.setPassword(passwordEncoder.encode("oldPassword"));
        user.setPhoneNumber("990414233");
        user.setRole(optionalRole.get());
        user.setEnabled(true);
        userRepo.save(user);
        System.out.println("Super admin qo'shildi!");
    }

    private void addForestySubjects(){
        if(subjectRepo.existsByIsPrivate(false)) return;
        String[] subjectNames = {"Matematika","Fizika","Ingliz tili","Biologiya","Geografiya","Kimyo","Ona tili va adabiyot","Tarix"};
        ColorName[] colorNames = {FOREST,LAVENDER,MAROON,ORANGE,OCEAN_BLUE,MIDNIGHT,AMBER,WALNUT};

        List<Subject> forestySubjects = new ArrayList<>();

        for (int i=0;i<subjectNames.length;i++) {
            Subject forestySubject = Subject.builder()
                    .name(subjectNames[i])
                    .color(colorNames[i])
                    .isPrivate(false)
                    .build();
            forestySubjects.add(forestySubject);
        }
        subjectRepo.saveAll(forestySubjects);
    }

    private void addPaycomClient(){
        if (!clientRepo.existsByPhoneNumber("Paycom")) {
            clientRepo.save(new Client(
                    "Paycom",
                    passwordEncoder.encode("PaycomUchunParolEdiBu")
            ));
            clientRepo.save(new Client(
                    "+197001234567",
                    passwordEncoder.encode("parolClient")
            ));
        }
    }
}

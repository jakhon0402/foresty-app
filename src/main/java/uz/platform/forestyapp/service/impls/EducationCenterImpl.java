package uz.platform.forestyapp.service.impls;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.*;
import uz.platform.forestyapp.entity.enums.CurrentPlanStatus;
import uz.platform.forestyapp.entity.enums.PlanLimitName;
import uz.platform.forestyapp.entity.enums.RoleName;
import uz.platform.forestyapp.payload.AddressDto;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.EducationCenterDto;
import uz.platform.forestyapp.payload.UserDto;
import uz.platform.forestyapp.repository.*;
import uz.platform.forestyapp.service.utils.AddressService;
import uz.platform.forestyapp.service.EducationCenterService;
import uz.platform.forestyapp.service.utils.UserService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class EducationCenterImpl implements EducationCenterService {

    @Autowired
    EducationCenterRepo educationCenterRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @Autowired
    PlanRepo planRepo;

    @Autowired
    CurrentPlanRepo currentPlanRepo;

    @Override
    public ApiResponse addEducationCenter(EducationCenterDto educationCenterDto) {
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        boolean centerExistsByUsername = educationCenterRepo.existsByUsername(educationCenterDto.getUsername());
        if(centerExistsByUsername){
            return new ApiResponse("Bunday usernamelik Uquv markaz mavjud!",false);
        }
        UserDto ownerDto = educationCenterDto.getOwner();
        if(userRepo.existsByUsername(ownerDto.getUsername())){
            return new ApiResponse("Bunday usernamelik user tizimda mavjud!",false);
        }
        Optional<Role> optionalRole = roleRepo.findByRoleName(RoleName.OWNER);
        Optional<Role> userRole = roleRepo.findByRoleName(RoleName.USER);

        if(userRepo.existsByEmailAndRoleIsNot(ownerDto.getEmail(),userRole.get())){
            return new ApiResponse("Bunday emaillik user tizimda mavjud!",false);
        }

        Optional<Plan> optionalPlan = planRepo.findById(educationCenterDto.getCurrentPlan().getPlanId());
        if(optionalPlan.isEmpty()){
            return new ApiResponse("Bunday tarif reja tizimda mavjud emas!",false);
        }
        CurrentPlan currentPlan = CurrentPlan.builder()
                .plan(optionalPlan.get())
                .status(CurrentPlanStatus.ACTIVE)
                .expireDate(ZonedDateTime.now(zoneId).plusDays(educationCenterDto.getCurrentPlan().getExpireDate()))
                .build();

        currentPlanRepo.save(currentPlan);

        AddressDto centerAddressDto = educationCenterDto.getAddress();
        Address educationCenterAddress = addressService.saveAddress(centerAddressDto);



        EducationCenter educationCenter = EducationCenter.builder()
                .username(educationCenterDto.getUsername())
                .name(educationCenterDto.getName())
                .phoneNumber(educationCenterDto.getPhoneNumber())
                .balance(0)
                .address(educationCenterAddress)
                .currentPlan(currentPlan)
                .build();
        EducationCenter saved = educationCenterRepo.save(educationCenter);
        User user = userService.saveUser(ownerDto, optionalRole.get(), null, saved);
        userRepo.save(user);
        return new ApiResponse("Yangi O'quv markaz yaratildi!",true);
    }

    @Override
    public List<EducationCenter> getEducationCenters() {
        return educationCenterRepo.findAll();
    }

    @Override
    public boolean checkPlanExpireDate(UUID educationCenterId) {
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        Optional<EducationCenter> optionalEducationCenter = educationCenterRepo.findById(educationCenterId);
        if(optionalEducationCenter.isEmpty()){
            return false;
        }
        ZonedDateTime expireDate = optionalEducationCenter.get().getCurrentPlan().getExpireDate();
        return expireDate.isAfter(ZonedDateTime.now(zoneId));
    }
}

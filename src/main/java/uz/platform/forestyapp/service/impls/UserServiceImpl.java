package uz.platform.forestyapp.service.impls;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.platform.forestyapp.entity.*;
import uz.platform.forestyapp.entity.enums.*;
import uz.platform.forestyapp.payload.*;
import uz.platform.forestyapp.payload.response.CurrentSystemUserRes;
import uz.platform.forestyapp.payload.response.HomeRes;
import uz.platform.forestyapp.repository.*;
import uz.platform.forestyapp.service.EducationCenterService;
import uz.platform.forestyapp.service.UserService;
import uz.platform.forestyapp.service.utils.AddressService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    EducationCenterService educationCenterService;

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    EducationCenterRepo educationCenterRepo;

    @Autowired
    TeacherRepo teacherRepo;

    @Autowired
    GroupRepo groupRepo;

    @Autowired
    StudentRepo studentRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AddressService addressService;

    @Autowired
    AttachmentRepo attachmentRepo;

    @Autowired
    AttachmentContentRepo attachmentContentRepo;

    @Override
    public ApiResponse getCurrentSystemUser(User user) {
        CurrentSystemUserRes currentSystemUserRes = CurrentSystemUserRes.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .educationCenter(user.getEducationCenter())
                .role(user.getRole().getRoleName())
                .avatar(user.getAvatar())
                .build();
        return new ApiResponse(currentSystemUserRes,true);
    }

    @Override
    public HomeRes getHomeData(User user) {
        UUID id = user.getEducationCenter().getId();
        long employeeCount = employeeRepo.countByEmployeeEducationCenterIdAndStatusIn(id, List.of(new EmployeeStatusName[]{EmployeeStatusName.NEW,EmployeeStatusName.ACTIVE}));
        long teacherCount = teacherRepo.countByTeacherEducationCenterIdAndStatusIn(id, List.of(new TeacherStatusName[]{TeacherStatusName.ACTIVE}));
        long groupCount = groupRepo.countByEducationCenterIdAndStatusIn(id, List.of(new GroupStatusName[]{GroupStatusName.NEW,GroupStatusName.ACTIVE}));
        long studentCount = studentRepo.countByStudentEducationCenterIdAndStatusIn(id, List.of(new StudentStatusName[]{StudentStatusName.NEW,StudentStatusName.ACTIVE}));

        return new HomeRes(employeeCount,0,
                teacherCount,0,
                groupCount,0,
                studentCount,0);
    }

    @Override
    public ApiResponse editProfile(User user, UserEditDto userDto) {

        boolean existsByEmail = userRepo.existsByEmailAndRoleRoleNameIsNotAndIdNot(userDto.getEmail(), RoleName.USER, user.getId());
        if(existsByEmail) return new ApiResponse("Bunday emaillik user tizimda mavjud!",false);
        boolean existsByUsernameAndIdNot = userRepo.existsByUsernameAndIdNot(userDto.getUsername(),  user.getId());
        if(existsByUsernameAndIdNot) return new ApiResponse("Bunday usernamelik user tizimda mavjud!",false);
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setFatherName(userDto.getFatherName());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setBirthDate(userDto.getBirthDate());

        if(userDto.getOldPassword()!=null&&userDto.getNewPassword()!=null&&userDto.getReNewPassword()!=null){
            if(!userDto.getNewPassword().equals(userDto.getReNewPassword())) return new ApiResponse("Parollar mos emas!",false);
            if(!passwordEncoder.matches(userDto.getOldPassword(), user.getPassword())) return new ApiResponse("Parol noto'g'ri kiritildi!",false);
            user.setPassword(passwordEncoder.encode(userDto.getNewPassword()));
        }

        if(userDto.getAddress() != null){
            if(user.getAddress()==null){
                Address address = addressService.saveAddress(userDto.getAddress());
                user.setAddress(address);
            }
            else{
               Address editedAddress =  addressService.editAddress(userDto.getAddress(),user.getAddress());
                user.setAddress(editedAddress);
            }
        }
        else{
            if(user.getAddress()!=null){
                Address editedAddress =  addressService.editAddress(new AddressDto(null,null,null),user.getAddress());
                user.setAddress(editedAddress);
            }

        }
        userRepo.save(user);
        return new ApiResponse("Ma'lumotlar tahrirlandi!",true);
    }

    @Override
    public ApiResponse updateAvatar(User user, MultipartFile file) throws IOException {
        if(!file.isEmpty()){
            String originalFilename = file.getOriginalFilename();
            long size = file.getSize();
            String contentType = file.getContentType();
            Attachment attachment;
            AttachmentContent attachmentContent;
            if(user.getAvatar()!=null){
                attachment = user.getAvatar();
                attachmentContent = user.getAvatar().getAttachmentContent();
            }
            else {
                attachment = new Attachment();
                attachmentContent = new AttachmentContent();
            }
            attachment.setOriginalName(originalFilename);
            attachment.setContentType(contentType);
            attachment.setSize(size);
            attachment.setTypeName(FileTypeName.IMAGE);


            attachmentContent.setBytes(file.getBytes());

            attachment.setAttachmentContent(attachmentContent);

            user.setAvatar(attachment);
            userRepo.save(user);

            return new ApiResponse("Avatar o'zgartirildi!",true);
        }
        else {
            return new ApiResponse("Fayl bo'sh bo'lmasligi kerak!",false);
        }
    }

    @Override
    public EducationCenter getCenterData(User user) {
        return user.getEducationCenter();
    }

    @Override
    public ApiResponse editCenterData(User user, CenterEditDto centerEditDto) {
        Optional<EducationCenter> optionalEducationCenter = educationCenterRepo.findById(user.getEducationCenter().getId());
        if(optionalEducationCenter.isEmpty()) return new ApiResponse("Bunday idlik uquv markaz mavjud emas!",false);
        EducationCenter educationCenter = optionalEducationCenter.get();
        if(centerEditDto.getDescription()!=null){
            educationCenter.setDescription(centerEditDto.getDescription());
        }
        educationCenter.setName(centerEditDto.getName());
        educationCenter.setUsername(centerEditDto.getUsername());
        educationCenter.setPhoneNumber(centerEditDto.getPhoneNumber());

        educationCenter.setInstagram(centerEditDto.getInstagram());
        educationCenter.setFacebook(centerEditDto.getFacebook());
        educationCenter.setTelegram(centerEditDto.getTelegram());
        educationCenter.setYoutube(centerEditDto.getYoutube());
        educationCenter.setTwitter(centerEditDto.getTwitter());
        educationCenter.setWebsite(centerEditDto.getWebsite());

        if(centerEditDto.getAddress() != null){
            if(user.getAddress()==null){
                Address address = addressService.saveAddress(centerEditDto.getAddress());
                educationCenter.setAddress(address);
            }
            else{
                Address editedAddress =  addressService.editAddress(centerEditDto.getAddress(),user.getAddress());
                educationCenter.setAddress(editedAddress);
            }
        }
        else{
            if(user.getAddress()!=null){
                Address editedAddress =  addressService.editAddress(new AddressDto(null,null,null),user.getAddress());
                educationCenter.setAddress(editedAddress);
            }
        }

        educationCenterRepo.save(educationCenter);
        return new ApiResponse("Markaz ma'lumotlari tahrirlandi!",true);
    }

    @Override
    public ApiResponse changeCenterLogo(User user, MultipartFile file) throws IOException {
        if(!file.isEmpty()){
            EducationCenter educationCenter = user.getEducationCenter();
            String originalFilename = file.getOriginalFilename();
            long size = file.getSize();
            String contentType = file.getContentType();
            Attachment attachment;
            AttachmentContent attachmentContent;
            if(educationCenter.getLogo()!=null){
                attachment = educationCenter.getLogo();
                attachmentContent = educationCenter.getLogo().getAttachmentContent();
            }
            else {
                attachment = new Attachment();
                attachmentContent = new AttachmentContent();
            }
            attachment.setOriginalName(originalFilename);
            attachment.setContentType(contentType);
            attachment.setSize(size);
            attachment.setTypeName(FileTypeName.IMAGE);


            attachmentContent.setBytes(file.getBytes());

            attachment.setAttachmentContent(attachmentContent);

            educationCenter.setLogo(attachment);
            educationCenterRepo.save(educationCenter);

            return new ApiResponse("Avatar o'zgartirildi!",true);
        }
        else {
            return new ApiResponse("Fayl bo'sh bo'lmasligi kerak!",false);
        }
    }

    @Override
    public ApiResponse changeCenterHeader(User user, MultipartFile file) throws IOException {
        if(!file.isEmpty()){
            EducationCenter educationCenter = user.getEducationCenter();
            String originalFilename = file.getOriginalFilename();
            long size = file.getSize();
            String contentType = file.getContentType();
            Attachment attachment;
            AttachmentContent attachmentContent;
            if(educationCenter.getHeaderImage()!=null){
                attachment = educationCenter.getHeaderImage();
                attachmentContent = educationCenter.getHeaderImage().getAttachmentContent();
            }
            else {
                attachment = new Attachment();
                attachmentContent = new AttachmentContent();
            }
            attachment.setOriginalName(originalFilename);
            attachment.setContentType(contentType);
            attachment.setSize(size);
            attachment.setTypeName(FileTypeName.IMAGE);


            attachmentContent.setBytes(file.getBytes());

            attachment.setAttachmentContent(attachmentContent);

            educationCenter.setHeaderImage(attachment);
            educationCenterRepo.save(educationCenter);

            return new ApiResponse("Avatar o'zgartirildi!",true);
        }
        else {
            return new ApiResponse("Fayl bo'sh bo'lmasligi kerak!",false);
        }
    }
}

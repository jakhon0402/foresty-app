package uz.platform.forestyapp.service;

import org.springframework.web.multipart.MultipartFile;
import uz.platform.forestyapp.entity.EducationCenter;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.CenterEditDto;
import uz.platform.forestyapp.payload.UserEditDto;
import uz.platform.forestyapp.payload.response.HomeRes;

import java.io.IOException;

public interface UserService {
    ApiResponse getCurrentSystemUser(User user);

    HomeRes getHomeData(User user);

    ApiResponse editProfile(User user, UserEditDto userDto);

    ApiResponse updateAvatar(User user, MultipartFile file) throws IOException;

    EducationCenter getCenterData(User user);

    ApiResponse editCenterData(User user, CenterEditDto centerEditDto);

    ApiResponse changeCenterLogo(User user, MultipartFile file) throws IOException;

    ApiResponse changeCenterHeader(User user, MultipartFile file) throws IOException;
}

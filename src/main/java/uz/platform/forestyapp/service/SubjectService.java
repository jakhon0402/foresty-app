package uz.platform.forestyapp.service;

import uz.platform.forestyapp.entity.Room;
import uz.platform.forestyapp.entity.Subject;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.SubjectDto;

import java.util.List;
import java.util.UUID;

public interface SubjectService {
    ApiResponse addSubject(SubjectDto subjectDto, User currentUser);
    ApiResponse editSubject(UUID id,SubjectDto subjectDto,User currentUser);
    ApiResponse deleteSubject(UUID id,User currentUser);
    List<Subject> getPrivateSubjects(UUID educationCenterId);


    List<Subject> getForestySubjects();

    ApiResponse getTopSubjects(User user);
}

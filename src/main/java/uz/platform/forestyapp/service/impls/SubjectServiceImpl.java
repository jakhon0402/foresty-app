package uz.platform.forestyapp.service.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.Group;
import uz.platform.forestyapp.entity.Subject;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.entity.enums.ActivityName;
import uz.platform.forestyapp.entity.enums.PlanLimitName;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.SubjectDto;
import uz.platform.forestyapp.repository.EducationCenterRepo;
import uz.platform.forestyapp.repository.GroupRepo;
import uz.platform.forestyapp.repository.SubjectRepo;
import uz.platform.forestyapp.service.SubjectService;
import uz.platform.forestyapp.service.utils.ActivityService;
import uz.platform.forestyapp.service.utils.PlanLimitService;
import uz.platform.forestyapp.utils.SubjectCount;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SubjectServiceImpl implements SubjectService {
    @Autowired
    EducationCenterRepo educationCenterRepo;

    @Autowired
    PlanLimitService planLimitService;

    @Autowired
    ActivityService activityService;

    @Autowired
    SubjectRepo subjectRepo;

    @Autowired
    GroupRepo groupRepo;

    @Override
    public ApiResponse addSubject(SubjectDto subjectDto, User currentUser) {
        if(planLimitService.checkPlanLimit(currentUser.getEducationCenter().getId(), PlanLimitName.SUBJECT)){
            return new ApiResponse("Fanlar soni limitdan kupayib ketdi!",false);
        }
        if(subjectRepo.existsByName(subjectDto.getName())){
            return new ApiResponse("Bunday nomlik fan mavjud!",false);
        }
        Subject subject = Subject.builder()
                .name(subjectDto.getName())
                .color(subjectDto.getColor())
                .isPrivate(true)
                .educationCenter(educationCenterRepo.findById(currentUser.getEducationCenter().getId()).get())
                .build();
        subjectRepo.save(subject);
        String subjectName = subject.getName();
        activityService.addActivity(subjectName,"", ActivityName.ADD_SUBJECT,currentUser);
        return new ApiResponse("Fan saqlandi!",true);
    }

    @Override
    public ApiResponse editSubject(UUID id, SubjectDto subjectDto, User currentUser) {
        Optional<Subject> optionalSubject = subjectRepo.findById(id);
        if(optionalSubject.isEmpty()){
            return new ApiResponse("Bunday idlik fan mavjud emas!",false);
        }
        Subject subject = optionalSubject.get();
        if(!subject.getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())){
            return new ApiResponse("Bunday idlik fan joriy uquv markazga tegishli emas!",false);
        }
        if(subjectRepo.existsByNameAndIdNot(subjectDto.getName(),id)){
            return new ApiResponse("Bunday nomlik fan mavjud!",false);
        }
        subject.setName(subjectDto.getName());
        subject.setColor(subjectDto.getColor());
        subjectRepo.save(subject);
        String subjectName = subject.getName();
        activityService.addActivity(subjectName,"", ActivityName.EDIT_SUBJECT,currentUser);
        return new ApiResponse("Fan tahrirlandi!",true);
    }

    @Override
    public ApiResponse deleteSubject(UUID id, User currentUser) {
        Optional<Subject> optionalSubject = subjectRepo.findById(id);
        if(optionalSubject.isEmpty()){
            return new ApiResponse("Bunday idlik fan mavjud emas!",false);
        }
        Subject subject = optionalSubject.get();
        if(!subject.isPrivate()) return new ApiResponse("Xatolik!",false);
        if(!subject.getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())){
            return new ApiResponse("Bunday idlik fan joriy uquv markazga tegishli emas!",false);
        }
        String subjectName = subject.getName();
        activityService.addActivity(subjectName,"", ActivityName.DELETE_STUDENT,currentUser);
        subjectRepo.delete(subject);
        return new ApiResponse("Xona o'chirildi!",true);
    }

    @Override
    public List<Subject> getPrivateSubjects(UUID educationCenterId) {
        return subjectRepo.findAllByEducationCenterId(educationCenterId);
    }


    @Override
    public List<Subject> getForestySubjects() {
        return subjectRepo.findAllByIsPrivate(false);
    }

    @Override
    public ApiResponse getTopSubjects(User user) {
        List<Object> topSubjects = subjectRepo.findTopSubjectsByEducationCenterId(user.getEducationCenter().getId(), PageRequest.of(0,5));
        return new ApiResponse(topSubjects,true);
    }


}

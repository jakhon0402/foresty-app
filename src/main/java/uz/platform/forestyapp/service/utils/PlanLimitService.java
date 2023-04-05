package uz.platform.forestyapp.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.EducationCenter;
import uz.platform.forestyapp.entity.Plan;
import uz.platform.forestyapp.entity.enums.*;
import uz.platform.forestyapp.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlanLimitService {
    @Autowired
    EducationCenterRepo educationCenterRepo;

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    TeacherRepo teacherRepo;

    @Autowired
    StudentRepo studentRepo;

    @Autowired
    GroupRepo groupRepo;

    @Autowired
    SubjectRepo subjectRepo;

    @Autowired
    RoomRepo roomRepo;

    public boolean checkPlanLimit(UUID educationCenterId, PlanLimitName planLimitName) {
        Optional<EducationCenter> optionalEducationCenter = educationCenterRepo.findById(educationCenterId);
        Plan plan = optionalEducationCenter.get().getCurrentPlan().getPlan();
        if(planLimitName.equals(PlanLimitName.EMPLOYEE)){
            return employeeRepo.countByEmployeeEducationCenterIdAndStatusIn(educationCenterId, List.of(new EmployeeStatusName[]{EmployeeStatusName.NEW, EmployeeStatusName.ACTIVE})) >= plan.getEmployeesLimit();
        }
        else if(planLimitName.equals(PlanLimitName.TEACHER)){
            return teacherRepo.countByTeacherEducationCenterIdAndStatusIn(educationCenterId, List.of(new TeacherStatusName[]{TeacherStatusName.ACTIVE})) >= plan.getTeachersLimit();
        }
        else if(planLimitName.equals(PlanLimitName.STUDENT)){
            return studentRepo.countByStudentEducationCenterIdAndStatusIn(educationCenterId, List.of(new StudentStatusName[]{StudentStatusName.NEW, StudentStatusName.ACTIVE})) >= plan.getStudentsLimit();
        }
        else if(planLimitName.equals(PlanLimitName.GROUP)){
            return groupRepo.countByEducationCenterIdAndStatusIn(educationCenterId, List.of(new GroupStatusName[]{GroupStatusName.NEW, GroupStatusName.ACTIVE})) >= plan.getGroupsLimit();
        }
        else if(planLimitName.equals(PlanLimitName.SUBJECT)){
            return subjectRepo.countByEducationCenterId(educationCenterId) >= plan.getSubjectsLimit();
        }
        else if(planLimitName.equals(PlanLimitName.ROOM)){
            return roomRepo.countByEducationCenterId(educationCenterId) >= plan.getRoomsLimit();
        }
        return true;
    }
}

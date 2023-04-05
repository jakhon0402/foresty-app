package uz.platform.forestyapp.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.*;
import uz.platform.forestyapp.entity.enums.*;
import uz.platform.forestyapp.repository.EmployeePaymentRepo;
import uz.platform.forestyapp.repository.EmployeeRepo;
import uz.platform.forestyapp.repository.GroupRepo;
import uz.platform.forestyapp.repository.GroupStudentRepo;
import uz.platform.forestyapp.repository.studentPayment.GroupPaymentRepo;
import uz.platform.forestyapp.repository.studentPayment.StudentPaymentRepo;
import uz.platform.forestyapp.repository.teacherPayment.TeacherGroupPaymentRepo;
import uz.platform.forestyapp.repository.teacherPayment.TeacherPaymentRepo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DailyTask {

    @Autowired
    GroupRepo groupRepo;

    @Autowired
    StudentPaymentRepo studentPaymentRepo;

    @Autowired
    GroupPaymentRepo groupPaymentRepo;

    @Autowired
    TeacherGroupPaymentRepo teacherGroupPaymentRepo;

    @Autowired
    TeacherPaymentRepo teacherPaymentRepo;

    @Autowired
    GroupStudentRepo groupStudentRepo;

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    EmployeePaymentRepo employeePaymentRepo;



//    @Scheduled(cron = "0 22 0 * * *")
//    @Scheduled(fixedRate = 10000)
    private void newStatusGroupProcess(){
        log.info("newStatusGroupProcess running");
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        ZonedDateTime dateTime = ZonedDateTime.of(2023,2,28,5,0,0,9000,zoneId);
        List<Group> newGroups = groupRepo.findAllByStatusAndCheckPaymentDate(GroupStatusName.NEW.ordinal(),dateTime);
        if(newGroups.size()==0) return;

        for (Group newGroup : newGroups) {
            List<GroupStudent> groupStudents = groupStudentRepo.findAllByGroupId(newGroup.getId());
            if(groupStudents.size()==0) continue;

            if(newGroup.getMonthType().equals(GroupMonthType.DEFINITE)){
                List<GroupPayment> groupPayments = new ArrayList<>();
                List<StudentPayment> studentPayments = new ArrayList<>();

                TeacherGroupPayment teacherGroupPayment = TeacherGroupPayment.builder()
                        .group(newGroup)
                        .status(GroupPaymentStatus.PROCESS)
                        .build();

                List<TeacherPayment> teacherPayments = new ArrayList<>();

                for (int i=0;i<newGroup.getDuration();i++){
                    ZonedDateTime fromDate = newGroup.getStartDate().plusDays(i* 30L);
                    ZonedDateTime toDate = newGroup.getStartDate().plusDays((i+1)* 30L);
                    //TALABALAR UCHUN
                    GroupPayment groupPayment = GroupPayment.builder()
                            .month(i+1)
                            .status(GroupPaymentStatus.PROCESS)
                            .fromDate(fromDate)
                            .toDate(toDate)
                            .group(newGroup)
                            .build();

                    TeacherPayment teacherPayment = TeacherPayment.builder()
                            .month(i+1)
                            .teacherGroupPayment(teacherGroupPayment)
                            .paymentAmount(newGroup.getPrice()*groupStudents.size()*newGroup.getAgreement()/100)
                            .status(PaymentStatusName.PROCESS)
                            .fromDate(fromDate)
                            .toDate(toDate)
                            .paidAmount(0L)
                            .debtor(newGroup.getPrice()*groupStudents.size()*newGroup.getAgreement()/100)
                            .build();

                    teacherPayments.add(teacherPayment);

                    addStudentPayment(groupStudents, studentPayments, groupPayment);
                    groupPayments.add(groupPayment);
                }
                groupStudentRepo.saveAll(groupStudents);
                groupPaymentRepo.saveAll(groupPayments);
                studentPaymentRepo.saveAll(studentPayments);

                teacherGroupPaymentRepo.save(teacherGroupPayment);
                teacherPaymentRepo.saveAll(teacherPayments);
            }

            else if(newGroup.getMonthType().equals(GroupMonthType.INDEFINITE)){
                List<StudentPayment> studentPayments = new ArrayList<>();
                GroupPayment groupPayment = GroupPayment.builder()
                        .month(1)
                        .fromDate(newGroup.getStartDate())
                        .toDate(newGroup.getStartDate().plusDays(30L))
                        .status(GroupPaymentStatus.PROCESS)
                        .group(newGroup)
                        .build();

                addStudentPayment(groupStudents, studentPayments, groupPayment);

                TeacherGroupPayment teacherGroupPayment = TeacherGroupPayment.builder()
                        .group(newGroup)
                        .status(GroupPaymentStatus.PROCESS)
                        .build();
                TeacherPayment teacherPayment = TeacherPayment.builder()
                        .month(1)
                        .fromDate(newGroup.getStartDate())
                        .toDate(newGroup.getStartDate().plusDays(30L))
                        .status(PaymentStatusName.PROCESS)
                        .teacherGroupPayment(teacherGroupPayment)
                        .paymentAmount(newGroup.getPrice()*groupStudents.size()*newGroup.getAgreement()/100)
                        .paidAmount(0L)
                        .debtor(newGroup.getPrice()*groupStudents.size()*newGroup.getAgreement()/100)
                        .build();

                groupStudentRepo.saveAll(groupStudents);
                groupPaymentRepo.save(groupPayment);
                studentPaymentRepo.saveAll(studentPayments);

                teacherGroupPaymentRepo.save(teacherGroupPayment);
                teacherPaymentRepo.save(teacherPayment);
            }
            newGroup.setStatus(GroupStatusName.ACTIVE);
            groupRepo.save(newGroup);
        }
    }

//    @Scheduled(fixedRate = 10000)
//    @Scheduled(cron = "0 0 1 * * *")
    private void activeStatusGroupProcess(){
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
//        ZonedDateTime dateTime = ZonedDateTime.now(zoneId);
        ZonedDateTime dateTime = ZonedDateTime.of(2023,7,28,5,0,0,9000,zoneId);

        List<Group> activeGroups = groupRepo.findAllByStatusAndCheckMonth(GroupStatusName.ACTIVE.ordinal(),dateTime);
        activeGroups.forEach(el -> log.info(el.getName()));
        if(activeGroups.size()==0) return;

        for (Group activeGroup : activeGroups) {
            List<GroupStudent> groupStudents = groupStudentRepo.findAllByStatusAndGroupId(StudentStatusName.ACTIVE,activeGroup.getId());
            if(groupStudents.size()==0) continue;

            //UTGAN OY BUYICHA MALUMOTLARNI UZGARTIRAMIZ
            Optional<GroupPayment> optionalGroupPayment = groupPaymentRepo.findByMonthAndGroupId(activeGroup.getCurrentMonth(), activeGroup.getId());
            if(optionalGroupPayment.isEmpty()) continue;
            GroupPayment groupPayment = optionalGroupPayment.get();

            List<StudentPayment> studentPayments = studentPaymentRepo.findAllByStatusAndGroupPaymentMonthAndGroupPaymentGroupId(PaymentStatusName.PROCESS, activeGroup.getCurrentMonth(),activeGroup.getId());
            if(studentPayments.size()==0){
                groupPayment.setStatus(GroupPaymentStatus.COMPLETED);
            }
            else {
                groupPayment.setStatus(GroupPaymentStatus.UNCOMPLETED);
                for (StudentPayment studentPayment : studentPayments) {
                    studentPayment.setStatus(PaymentStatusName.UNPAID);
                }
                studentPaymentRepo.saveAll(studentPayments);
            }
            groupPaymentRepo.save(groupPayment);

            Optional<TeacherPayment> optionalTeacherPayment = teacherPaymentRepo.findByMonthAndTeacherGroupPaymentGroupId(activeGroup.getCurrentMonth(), activeGroup.getId());
            TeacherPayment teacherPayment = optionalTeacherPayment.get();
            if(teacherPayment.getStatus().equals(PaymentStatusName.PROCESS)) {
                teacherPayment.setStatus(PaymentStatusName.UNPAID);
                teacherPaymentRepo.save(teacherPayment);
            }

            //AGAR GURUH NING DAVOMIYLIGI ANIQ BULMASA KEYINGI OY UCHUN MALUMOTLAR KIRITILADI!
            if(activeGroup.getMonthType().equals(GroupMonthType.INDEFINITE)){
                if(activeGroup.getCurrentMonth()>=12){
                    activeGroup.setStatus(GroupStatusName.COMPLETED);
                    Optional<TeacherGroupPayment> optionalTeacherGroupPaymentCompleted = teacherGroupPaymentRepo.findByGroupId(activeGroup.getId());
                    TeacherGroupPayment teacherGroupPayment = optionalTeacherGroupPaymentCompleted.orElseThrow();
                    teacherGroupPayment.setStatus(GroupPaymentStatus.UNCOMPLETED);
                    teacherGroupPaymentRepo.save(teacherGroupPayment);
                    groupRepo.save(activeGroup);
                    continue;
                }
                ZonedDateTime fromDate = activeGroup.getStartDate().plusDays(activeGroup.getCurrentMonth()* 30L);
                ZonedDateTime toDate = activeGroup.getStartDate().plusDays((activeGroup.getCurrentMonth()+1)* 30L);
                GroupPayment nextGroupPayment = GroupPayment.builder()
                        .group(activeGroup)
                        .status(GroupPaymentStatus.PROCESS)
                        .fromDate(fromDate)
                        .toDate(toDate)
                        .month(activeGroup.getCurrentMonth()+1)
                        .build();
                List<StudentPayment> nexStudentPayments = new ArrayList<>();
                addStudentPayment(groupStudents,nexStudentPayments,nextGroupPayment);
                groupPaymentRepo.save(nextGroupPayment);
                studentPaymentRepo.saveAll(nexStudentPayments);

                Optional<TeacherGroupPayment> optionalTeacherGroupPayment = teacherGroupPaymentRepo.findByGroupId(activeGroup.getId());
                TeacherPayment nextTeacherPayment = TeacherPayment.builder()
                        .month(activeGroup.getCurrentMonth()+1)
                        .teacherGroupPayment(optionalTeacherGroupPayment.get())
                        .status(PaymentStatusName.PROCESS)
                        .fromDate(fromDate)
                        .toDate(toDate)
                        .paymentAmount(activeGroup.getPrice()*groupStudents.size()*activeGroup.getAgreement()/100)
                        .paidAmount(0L)
                        .debtor(activeGroup.getPrice()*groupStudents.size()*activeGroup.getAgreement()/100)
                        .build();
                teacherPaymentRepo.save(nextTeacherPayment);
            }

            else if(activeGroup.getMonthType().equals(GroupMonthType.DEFINITE)){
                if(dateTime.isAfter(activeGroup.getStartDate().plusDays(activeGroup.getDuration()*30L))){
                    activeGroup.setStatus(GroupStatusName.COMPLETED);
                    Optional<TeacherGroupPayment> optionalTeacherGroupPaymentCompleted = teacherGroupPaymentRepo.findByGroupId(activeGroup.getId());
                    TeacherGroupPayment teacherGroupPayment = optionalTeacherGroupPaymentCompleted.orElseThrow();
                    teacherGroupPayment.setStatus(GroupPaymentStatus.UNCOMPLETED);
                    teacherGroupPaymentRepo.save(teacherGroupPayment);
                    groupRepo.save(activeGroup);
                    continue;
                }
            }
            activeGroup.setCurrentMonth(activeGroup.getCurrentMonth()+1);
            groupRepo.save(activeGroup);
        }
    }

//    @Scheduled(fixedRate = 10000)
    private void completedStatusGroupProcess(){
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
//        ZonedDateTime dateTime = ZonedDateTime.now(zoneId);
        ZonedDateTime dateTime = ZonedDateTime.of(2023,8,28,5,0,0,9000,zoneId);

        List<Group> completedGroups = groupRepo.findAllByStatusAndCheckMonth(GroupStatusName.FINISH.ordinal(), dateTime);

        if(completedGroups.size()==0) return;

        for (Group activeGroup : completedGroups) {
            List<GroupStudent> groupStudents = groupStudentRepo.findAllByStatusAndGroupId(StudentStatusName.ACTIVE,activeGroup.getId());
            if(groupStudents.size()==0) continue;

            //UTGAN OY BUYICHA MALUMOTLARNI UZGARTIRAMIZ
            Optional<GroupPayment> optionalGroupPayment = groupPaymentRepo.findByMonthAndGroupId(activeGroup.getCurrentMonth(), activeGroup.getId());
            if(optionalGroupPayment.isEmpty()) continue;
            GroupPayment groupPayment = optionalGroupPayment.get();

            List<StudentPayment> studentPayments = studentPaymentRepo.findAllByStatusAndGroupPaymentMonthAndGroupPaymentGroupId(PaymentStatusName.PROCESS, activeGroup.getCurrentMonth(),activeGroup.getId());
            if(studentPayments.isEmpty()){
                groupPayment.setStatus(GroupPaymentStatus.COMPLETED);
            }
            else {
                groupPayment.setStatus(GroupPaymentStatus.UNCOMPLETED);
                for (StudentPayment studentPayment : studentPayments) {
                    studentPayment.setStatus(PaymentStatusName.UNPAID);
                }
                studentPaymentRepo.saveAll(studentPayments);
            }
            groupPaymentRepo.save(groupPayment);

            Optional<TeacherPayment> optionalTeacherPayment = teacherPaymentRepo.findByMonthAndTeacherGroupPaymentGroupId(activeGroup.getCurrentMonth(), activeGroup.getId());
            TeacherPayment teacherPayment = optionalTeacherPayment.get();
            if(teacherPayment.getStatus().equals(PaymentStatusName.PROCESS)) {
                teacherPayment.setStatus(PaymentStatusName.UNPAID);
                teacherPaymentRepo.save(teacherPayment);
            }
            for (GroupStudent groupStudent : groupStudents) {
                groupStudent.setStatus(StudentStatusName.GRADUATED);
            }
            groupStudentRepo.saveAll(groupStudents);
            activeGroup.setStatus(GroupStatusName.COMPLETED);
            groupRepo.save(activeGroup);
        }
    }

    @Scheduled(fixedRate = 10000)
//    @Scheduled(cron = "0 0 2 * * *")
    private void newEmployeeProcess(){
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        ZonedDateTime dateTime = ZonedDateTime.now(zoneId);

        List<Employee> employees = employeeRepo.findAllNewByStatus(EmployeeStatusName.NEW.ordinal(),dateTime);
        if(employees.isEmpty()) return;

        for (Employee employee : employees) {
//            if(dateTime.isBefore(employee.getJobStartsDate().plusDays(employee.getTestDaysCount()))) continue;

            ZonedDateTime fromDate = employee.getJobStartsDate();
            ZonedDateTime toDate = employee.getJobStartsDate().plusDays(30L);

            int i1 = employee.getTestDaysCount() / 30;

            List<EmployeePayment> employeePayments = new ArrayList<>();

            for(int i=0;i<=i1;i++){
                EmployeePayment employeePayment = EmployeePayment.builder()
                        .employee(employee)
                        .month(i+1L)
                        .fromDate(employee.getJobStartsDate().plusDays(i* 30L))
                        .toDate(employee.getJobStartsDate().plusDays((i+1)*30L))
                        .amount(employee.getCurrentSalary())
                        .paidAmount(0)
                        .debtor(employee.getCurrentSalary())
//                        .status(PaymentStatusName.PROCESS)
                        .build();
                if(i==i1){
                    employeePayment.setStatus(PaymentStatusName.PROCESS);
                } else {
                    employeePayment.setStatus(PaymentStatusName.UNPAID);
                }
                employeePayments.add(employeePayment);
            }

            employeePaymentRepo.saveAll(employeePayments);
            employee.setStatus(EmployeeStatusName.ACTIVE);
            employee.setWorkedMonthCount((long) i1);
            employeeRepo.save(employee);
        }
    }

    @Scheduled(fixedRate = 10000)
//    @Scheduled(cron = "0 0 3 * * *")
    private void activeEmployeeProcess(){
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        ZonedDateTime dateTime = ZonedDateTime.now(zoneId);

        List<Employee> employees = employeeRepo.findAllActiveByStatus(EmployeeStatusName.ACTIVE.ordinal(),dateTime);
        if(employees.size()==0) return;

        for (Employee employee : employees) {
//            if(dateTime.isBefore(employee.getJobStartsDate().plusDays((employee.getWorkedMonthCount()+1)*30))) continue;

            ZonedDateTime fromDate = employee.getJobStartsDate().plusDays((employee.getWorkedMonthCount()+1)*30);
            ZonedDateTime toDate = employee.getJobStartsDate().plusDays((employee.getWorkedMonthCount()+2)*30);

            Optional<EmployeePayment> optionalEmployeePayment = employeePaymentRepo.findByMonthAndEmployeeId(employee.getWorkedMonthCount() + 1, employee.getId());
            if(optionalEmployeePayment.isEmpty()) continue;

            EmployeePayment previousEmployeePayment = optionalEmployeePayment.get();
            if(previousEmployeePayment.getStatus().equals(PaymentStatusName.PROCESS)){
                previousEmployeePayment.setStatus(PaymentStatusName.UNPAID);
                employeePaymentRepo.save(previousEmployeePayment);
            }

            EmployeePayment employeePayment = EmployeePayment.builder()
                    .employee(employee)
                    .month(previousEmployeePayment.getMonth()+1)
                    .fromDate(fromDate)
                    .toDate(toDate)
                    .amount(employee.getCurrentSalary())
                    .paidAmount(0)
                    .debtor(employee.getCurrentSalary())
                    .status(PaymentStatusName.PROCESS)
                    .build();
            employee.setWorkedMonthCount(employee.getWorkedMonthCount()+1);
            employeeRepo.save(employee);
            employeePaymentRepo.save(employeePayment);
        }
    }

    @Scheduled(fixedRate = 10000)
//    @Scheduled(cron = "0 0 3 * * *")
    private void completedEmployeeProcess(){
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        ZonedDateTime dateTime = ZonedDateTime.now(zoneId).plusDays(60);

        List<Employee> employees = employeeRepo.findAllActiveByStatus(EmployeeStatusName.FINISH.ordinal(),dateTime);
        if(employees.isEmpty()) return;

        for (Employee employee : employees) {

            Optional<EmployeePayment> optionalEmployeePayment = employeePaymentRepo.findByMonthAndEmployeeId(employee.getWorkedMonthCount() + 1, employee.getId());
            if(optionalEmployeePayment.isEmpty()) continue;

            EmployeePayment previousEmployeePayment = optionalEmployeePayment.get();
            if(previousEmployeePayment.getStatus().equals(PaymentStatusName.PROCESS)){
                previousEmployeePayment.setStatus(PaymentStatusName.UNPAID);
                employeePaymentRepo.save(previousEmployeePayment);
            }


            employee.setStatus(EmployeeStatusName.COMPLETED);
            employeeRepo.save(employee);
        }
    }
    

    private void addStudentPayment(List<GroupStudent> groupStudents, List<StudentPayment> studentPayments, GroupPayment groupPayment) {
        for (GroupStudent groupStudent : groupStudents) {
            String studentFullName = groupStudent.getStudent().getStudent().getFirstName() + " " + groupStudent.getStudent().getStudent().getLastName();
            StudentPayment studentPayment = StudentPayment.builder()
                    .studentId(groupStudent.getStudent().getId())
                    .studentFullName(studentFullName)
                    .phoneNumber(groupStudent.getStudent().getStudent().getPhoneNumber())
                    .paymentAmount(groupStudent.getGroup().getPrice())
                    .studentStatus(StudentStatusName.ACTIVE)
                    .groupPayment(groupPayment)
                    .paidAmount(0L)
                    .debtor(groupStudent.getGroup().getPrice())
                    .status(PaymentStatusName.PROCESS)
                    .build();
            Student student = groupStudent.getStudent();
            student.setStatus(StudentStatusName.ACTIVE);
            studentPayments.add(studentPayment);
            groupStudent.setStatus(StudentStatusName.ACTIVE);
        }
    }


}

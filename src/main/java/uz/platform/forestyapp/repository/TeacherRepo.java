package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.platform.forestyapp.entity.Teacher;
import uz.platform.forestyapp.entity.enums.TeacherStatusName;
import uz.platform.forestyapp.payload.response.PaymentTeachers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeacherRepo extends JpaRepository<Teacher, UUID> {
    long countByTeacherEducationCenterIdAndStatusIn(UUID id, Collection<TeacherStatusName> statusNames);

    List<Teacher> findAllByTeacherEducationCenterId(UUID id);

    @Query(value = "select t as teacher," +
            "(select count(g) from groups as g where g.teacher.id=t.id) as groupsCount," +
            "(select sum(gs.group.price*gs.group.agreement/100) from GroupStudent as gs where gs.group.teacher.id=t.id and gs.group.status<>3) as monthlySalary," +
            "(select count(distinct tp.teacherGroupPayment.id) from TeacherPayment as tp where tp.status=2 and tp.teacherGroupPayment.group.teacher.id=t.id) as debtorsCount from Teacher as t where t.teacher.educationCenter.id=?1")
    List<PaymentTeachers> getPaymentTeachers(UUID id);
}

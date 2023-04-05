package uz.platform.forestyapp.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.platform.forestyapp.entity.Group;
import uz.platform.forestyapp.entity.GroupPayment;
import uz.platform.forestyapp.entity.enums.GroupStatusName;
import uz.platform.forestyapp.payload.response.PaymentGroup;
import uz.platform.forestyapp.payload.response.PaymentGroups;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public interface GroupRepo extends JpaRepository<Group, UUID> {
    long countByEducationCenterIdAndStatusIn(UUID id, Collection<GroupStatusName> statusNames);

    List<Group> findAllByStatus(GroupStatusName status);


    @Query(value = "SELECT * FROM groups  WHERE status = :status AND start_date + current_month * interval '30 days' < :date ;",nativeQuery = true)
    List<Group> findAllByStatusAndCheckMonth(@Param(value = "status") Integer status,@Param(value = "date") ZonedDateTime date);

    @Query(value = "SELECT * FROM groups  WHERE status = :status AND payment_starts_date < :date ;",nativeQuery = true)
    List<Group> findAllByStatusAndCheckPaymentDate(@Param(value = "status") Integer status,@Param(value = "date") ZonedDateTime date);

    List<Group> findAllByTeacherIdAndStatus(UUID teacherId,GroupStatusName status);
    List<Group> findAllByTeacherId(UUID teacherId);

    List<Group> findAllByEducationCenterId(UUID id);

    @Query(value = "SELECT gs.group,count(gs.student.id) as cnt FROM GroupStudent as gs left join gs.group where gs.group.educationCenter.id = ?1 GROUP BY gs.group order by cnt desc")
    List<Object> findTopGroupsByEducationCenterIdQuery(UUID id, Pageable pageable);

    @Query(value = "select distinct g.id as id,  g.name as name, g.subject as subject,g.price as price,g.status as status,  " +
            "       (SELECT COUNT (distinct sp.studentId) FROM StudentPayment sp where sp.status=2 AND sp.groupPayment.group.id=g.id) as debtorsCount, (SELECT COUNT(distinct gs) FROM GroupStudent gs WHERE gs.group.id = g.id) as studentsCount " +
            "        from groups as g left join GroupPayment p on g.id = p.group.id where g.educationCenter.id= ?1")
    List<PaymentGroups> getGroupsPayment(UUID id);

    @Query(value = "select distinct g as groupData, " +
            "       (SELECT COUNT (distinct sp.studentId) FROM StudentPayment sp where sp.status=2 AND sp.groupPayment.group=g.id) as debtorsCount, (SELECT COUNT(gs) FROM GroupStudent gs WHERE gs.group.id = g.id) as studentsCount " +
            "        from groups as g where g.id= ?1")
    PaymentGroup getGroupPayment(UUID id);
}

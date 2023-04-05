package uz.platform.forestyapp.repository.teacherPayment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.platform.forestyapp.entity.TeacherGroupPayment;
import uz.platform.forestyapp.payload.response.TeacherGroupPaymentImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeacherGroupPaymentRepo extends JpaRepository<TeacherGroupPayment, UUID> {
    Optional<TeacherGroupPayment> findByGroupId(UUID groupId);

    List<TeacherGroupPayment> findAllByGroupTeacherId(UUID id);

    @Query(value = "select tgp as teacherGroupPayment,(select count(gs) from GroupStudent gs where gs.group.id=tgp.group.id) as studentsCount from TeacherGroupPayment tgp where tgp.group.teacher.id=?1")
    List<TeacherGroupPaymentImpl> getTeacherGroups(UUID id);

    void deleteByGroupId(UUID id);

    void deleteAllByGroupTeacherId(UUID id);
}

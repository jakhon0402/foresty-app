package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.platform.forestyapp.entity.Group;
import uz.platform.forestyapp.entity.GroupStudent;
import uz.platform.forestyapp.entity.enums.StudentStatusName;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupStudentRepo extends JpaRepository<GroupStudent, UUID> {
    List<GroupStudent> findAllByGroupId(UUID groupId);

    List<GroupStudent> findAllByStatusAndGroupId(StudentStatusName status,UUID groupID);

    List<GroupStudent> findAllByStudentId(UUID id);

    @Query("select gs.group from GroupStudent gs where gs.student.id=?1")
    List<Group> findGroupsByStudentId(UUID id);

    Optional<GroupStudent> findByGroupIdAndStudentId(UUID groupId,UUID studentId);

    Long countAllByGroupId(UUID id);

    void deleteAllByGroupId(UUID groupId);



}

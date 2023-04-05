package uz.platform.forestyapp.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.Student;
import uz.platform.forestyapp.entity.enums.StudentStatusName;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface StudentRepo extends JpaRepository<Student, UUID> {
    List<Student> findAllByStudentEducationCenterId(UUID educationId, Pageable pageable);

    List<Student> findAllByStudentEducationCenterId(UUID id);

    long countByStudentEducationCenterIdAndStatusIn(UUID id, Collection<StudentStatusName> statusNames);
}

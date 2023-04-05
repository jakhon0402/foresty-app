package uz.platform.forestyapp.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.platform.forestyapp.entity.Subject;
import uz.platform.forestyapp.utils.SubjectCount;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubjectRepo extends JpaRepository<Subject, UUID> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name,UUID id);
    List<Subject> findAllByEducationCenterId(UUID educationCenterId);

    long countByEducationCenterId(UUID id);

    boolean existsByIsPrivate(boolean isPrivate);

    List<Subject> findAllByIsPrivate(boolean isPrivate);

    List<Subject> findAllByEducationCenterIdAndIsPrivate(UUID id,boolean isPrivate);

    @Query(value = "SELECT g.subject,count(g.subject) as cnt FROM groups as g left join g.subject subject where g.educationCenter.id = ?1 GROUP BY g.subject order by cnt desc")
    List<Object> findTopSubjectsByEducationCenterId(UUID id, Pageable pageable);

}

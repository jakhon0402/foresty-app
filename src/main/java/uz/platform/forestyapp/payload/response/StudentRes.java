package uz.platform.forestyapp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uz.platform.forestyapp.entity.Student;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class StudentRes {
    List<Student> students;
    Integer currentPage;
    Long pagesCount;
}

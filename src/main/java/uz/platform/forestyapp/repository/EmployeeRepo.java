package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.platform.forestyapp.entity.Employee;
import uz.platform.forestyapp.entity.Group;
import uz.platform.forestyapp.entity.enums.EmployeeStatusName;
import uz.platform.forestyapp.payload.response.PaymentEmployees;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface EmployeeRepo extends JpaRepository<Employee, UUID> {
    long countByEmployeeEducationCenterIdAndStatusIn(UUID id, Collection<EmployeeStatusName> status);
    List<Employee> findAllByStatus(EmployeeStatusName status);

    List<Employee> findAllByEmployeeEducationCenterId(UUID id);

    @Query("select e.id as id, e.employee.firstName as firstName, e.employee.lastName as lastName,e.status as employeeStatus, e.employee.role.roleName as roleName, e.currentSalary as salary, (select ep.status from EmployeePayment as ep where ep.employee.id=e.id and ep.month = (select max(epp.month) from EmployeePayment as epp where epp.employee.id = ep.employee.id)) as status from Employee as e where e.employee.educationCenter.id = ?1")
    List<PaymentEmployees> getPaymentEmployeesByEduCenterId(UUID id);

    @Query(value = "SELECT * FROM Employee WHERE status = :status AND job_starts_date +  test_days_count * INTERVAL '1 day' < :date ;",nativeQuery = true)
    List<Employee> findAllNewByStatus(@Param(value = "status") Integer status, @Param(value = "date") ZonedDateTime date);

    @Query(value = "SELECT * FROM Employee WHERE status = :status AND job_starts_date + ((worked_month_count + 1) * interval '30 days') < :date ;",nativeQuery = true)
    List<Employee> findAllActiveByStatus(@Param(value = "status") Integer status, @Param(value = "date") ZonedDateTime date);
}

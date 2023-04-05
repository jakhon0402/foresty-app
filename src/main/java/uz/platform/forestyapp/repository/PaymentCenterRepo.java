package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.platform.forestyapp.entity.PaymentCenter;
import uz.platform.forestyapp.payload.response.Finance;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface PaymentCenterRepo extends JpaRepository<PaymentCenter, UUID> {
    List<PaymentCenter> findAllByEducationCenterId(UUID id);

    @Query("select p from PaymentCenter as p where p.createdAt >=?1 and p.createdAt <= ?2 and p.educationCenter.id = ?3")
    List<PaymentCenter> getAllPaymentsByEduCenterId(Timestamp fromDate, Timestamp toDate, UUID id);

    @Query("select (select sum(p.amount) from PaymentCenter p where p.type = 'INCOME' and p.educationCenter.id = :eduId) as totalIncome," +
            "(select sum(p.amount) from PaymentCenter p where p.type = 'EXPENSE' and p.educationCenter.id = :eduId) as totalExpense," +
            "(select coalesce( sum(p1.amount),0) from PaymentCenter p1 where cast(p1.createdAt as TIMESTAMP ) <= :yesterday and p1.type = 'INCOME' and p1.educationCenter.id = :eduId) as totalIncomeYesterday," +
            "(select coalesce( sum(p2.amount),0) from PaymentCenter p2 where cast(p2.createdAt as TIMESTAMP ) <= :yesterday and p2.type = 'EXPENSE' and p2.educationCenter.id = :eduId) as totalExpenseYesterday," +
            "(select count( distinct sp.studentId) from StudentPayment sp where sp.status=2 AND sp.groupPayment.group.educationCenter.id=:eduId) as groupDebtors," +
            "(select count (distinct tp.teacherGroupPayment.group.teacher.id) from TeacherPayment tp where tp.status=1 and tp.teacherGroupPayment.group.educationCenter.id=:eduId) as teacherDebtors," +
            "(select count (distinct ep.employee.id) from EmployeePayment ep where ep.status=2 and ep.employee.employee.educationCenter.id=:eduId) as employeeDebtors")
    Finance getFinanceData(@Param("eduId") UUID eduId, @Param("yesterday") Timestamp yesterday);
}

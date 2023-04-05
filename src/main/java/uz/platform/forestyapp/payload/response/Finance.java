package uz.platform.forestyapp.payload.response;

public interface Finance {
    Long getTotalIncome();
    Long getTotalExpense();

    Long getTotalIncomeYesterday();
    Long getTotalExpenseYesterday();

    Long getGroupDebtors();
    Long getTeacherDebtors();
    Long getEmployeeDebtors();
}

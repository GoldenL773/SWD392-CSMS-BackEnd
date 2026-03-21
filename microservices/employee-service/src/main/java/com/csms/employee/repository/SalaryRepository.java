package com.csms.employee.repository;

import com.csms.employee.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

    @Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByEmployeeId(Long employeeId);
    List<Salary> findByStatus(String status);
    List<Salary> findByEmployeeIdAndPeriodStartAndPeriodEnd(Long employeeId, LocalDate periodStart, LocalDate periodEnd);
}

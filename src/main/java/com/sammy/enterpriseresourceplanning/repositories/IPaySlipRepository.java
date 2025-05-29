package com.sammy.enterpriseresourceplanning.repositories;

import com.sammy.enterpriseresourceplanning.enums.EPaySlipStatus;
import com.sammy.enterpriseresourceplanning.models.PaySlip;
import com.sammy.enterpriseresourceplanning.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IPaySlipRepository extends JpaRepository<PaySlip, UUID> {
    List<PaySlip> findByEmployee(User employee);
    List<PaySlip> findByEmployeeAndStatus(User employee, EPaySlipStatus status);
    List<PaySlip> findByStatus(EPaySlipStatus status);
    Optional<PaySlip> findByEmployeeAndMonthAndYear(User employee, Integer month, Integer year);
    List<PaySlip> findByMonthAndYear(Integer month, Integer year);
}
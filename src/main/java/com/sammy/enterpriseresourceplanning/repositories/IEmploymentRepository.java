package com.sammy.enterpriseresourceplanning.repositories;

import com.sammy.enterpriseresourceplanning.models.Employment;
import com.sammy.enterpriseresourceplanning.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IEmploymentRepository extends JpaRepository<Employment, UUID> {
    Optional<Employment> findByCode(String code);
    List<Employment> findByEmployee(User employee);
    Optional<Employment> findByEmployeeAndCode(User employee, String code);
}
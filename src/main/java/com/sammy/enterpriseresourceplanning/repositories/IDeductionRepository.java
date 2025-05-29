package com.sammy.enterpriseresourceplanning.repositories;

import com.sammy.enterpriseresourceplanning.models.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IDeductionRepository extends JpaRepository<Deduction, UUID> {
    Optional<Deduction> findByCode(String code);
    Optional<Deduction> findByName(String name);
}
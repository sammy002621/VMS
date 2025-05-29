package com.sammy.enterpriseresourceplanning.repositories;

import com.sammy.enterpriseresourceplanning.enums.ERole;
import com.sammy.enterpriseresourceplanning.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findRoleByName(ERole name);

    boolean existsByName(String name);
}

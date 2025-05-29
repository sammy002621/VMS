package com.sammy.enterpriseresourceplanning.services;


import com.sammy.enterpriseresourceplanning.dtos.request.role.CreateRoleDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.role.RoleResponseDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.role.RolesResponseDTO;
import com.sammy.enterpriseresourceplanning.enums.ERole;
import com.sammy.enterpriseresourceplanning.models.Role;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IRoleService {
    public Role getRoleById(UUID roleId);

    public Role getRoleByName(ERole roleName);

    public void createRole(ERole roleName);

    public RoleResponseDTO createRole(CreateRoleDTO createRoleDTO);

    public RolesResponseDTO getRoles(Pageable pageable);

    public void deleteRole(UUID roleId);

    public boolean isRolePresent(ERole roleName);
}
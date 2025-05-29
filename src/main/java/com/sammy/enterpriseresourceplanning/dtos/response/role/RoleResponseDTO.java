package com.sammy.enterpriseresourceplanning.dtos.response.role;


import com.sammy.enterpriseresourceplanning.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RoleResponseDTO {
    private Role role;
}
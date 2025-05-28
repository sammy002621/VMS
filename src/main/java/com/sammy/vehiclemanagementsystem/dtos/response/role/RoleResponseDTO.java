package com.sammy.vehiclemanagementsystem.dtos.response.role;


import com.sammy.vehiclemanagementsystem.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RoleResponseDTO {
    private Role role;
}
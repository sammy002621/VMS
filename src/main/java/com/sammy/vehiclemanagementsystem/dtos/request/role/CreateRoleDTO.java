package com.sammy.vehiclemanagementsystem.dtos.request.role;


import com.sammy.vehiclemanagementsystem.enums.ERole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreateRoleDTO {
    @Schema(example = "ADMIN", description = "Role name")
    private ERole name;
}

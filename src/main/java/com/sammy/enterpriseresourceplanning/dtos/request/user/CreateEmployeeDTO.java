package com.sammy.enterpriseresourceplanning.dtos.request.user;

import com.sammy.enterpriseresourceplanning.dtos.request.auth.RegisterUserDTO;
import com.sammy.enterpriseresourceplanning.enums.ERole;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Getter
@Setter
public class CreateEmployeeDTO extends RegisterUserDTO {
    @NotNull(message = "Role cannot be null")
    private ERole role;
    
    public CreateEmployeeDTO(RegisterUserDTO registerUserDTO, ERole role) {
        this.setFirstName(registerUserDTO.getFirstName());
        this.setLastName(registerUserDTO.getLastName());
        this.setEmail(registerUserDTO.getEmail());
        this.setPhoneNumber(registerUserDTO.getPhoneNumber());
        this.setPassword(registerUserDTO.getPassword());
        this.setNationalId(registerUserDTO.getNationalId());
        this.role = role;
    }
}
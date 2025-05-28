package com.sammy.vehiclemanagementsystem.dtos.request.auth;


import com.sammy.vehiclemanagementsystem.annotations.password.ValidPassword;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class PasswordResetDTO {
    @Email
    private String email;
    private String resetCode;

    @ValidPassword(message = "Password should be strong")
    private String newPassword;
}
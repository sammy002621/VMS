package com.sammy.enterpriseresourceplanning.dtos.request.auth;

import com.sammy.enterpriseresourceplanning.annotations.user.password.ValidPassword;
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
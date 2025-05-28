package com.sammy.vehiclemanagementsystem.dtos.request.auth;
import com.sammy.vehiclemanagementsystem.annotations.user.password.ValidPassword;
import lombok.Data;

@Data
public class PasswordUpdateDTO {
    private String oldPassword;
    @ValidPassword(message = "Password should be strong")
    private String newPassword;
}
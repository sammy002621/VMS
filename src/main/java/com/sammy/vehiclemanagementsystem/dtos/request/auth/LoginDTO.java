package com.sammy.vehiclemanagementsystem.dtos.request.auth;

import com.sammy.vehiclemanagementsystem.annotations.user.password.ValidPassword;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    @Email
    private String email;
    @ValidPassword(message = "Password should be strong")
    private String password;
}

package com.sammy.enterpriseresourceplanning.dtos.request.auth;


import com.sammy.enterpriseresourceplanning.annotations.user.nationalID.ValidNationalID;
import com.sammy.enterpriseresourceplanning.annotations.user.password.ValidPassword;
import com.sammy.enterpriseresourceplanning.annotations.user.phone.ValidRwandanPhone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
public class RegisterUserDTO {


    @Schema(example = "John")
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @Schema(example = "Doe")
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @Schema(example = "example@gmail.com")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(example = "0788671061")
    @NotBlank(message = "Phone number cannot be blank")
    @ValidRwandanPhone
    private String phoneNumber;

    @Schema(example = "1234567890123456")
    @NotBlank(message = "National ID cannot be blank")
    @ValidNationalID
    private String nationalId;

    @Schema(example = "password@123")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @ValidPassword(message = "Password should be strong")
    private String password;
}

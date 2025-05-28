package com.sammy.vehiclemanagementsystem.dtos.response.user;


import com.sammy.vehiclemanagementsystem.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private User user;
}
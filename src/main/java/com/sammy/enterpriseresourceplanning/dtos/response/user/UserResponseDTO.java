package com.sammy.enterpriseresourceplanning.dtos.response.user;


import com.sammy.enterpriseresourceplanning.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private User user;
}
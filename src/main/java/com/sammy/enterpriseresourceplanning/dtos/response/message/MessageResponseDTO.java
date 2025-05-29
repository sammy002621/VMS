package com.sammy.enterpriseresourceplanning.dtos.response.message;

import com.sammy.enterpriseresourceplanning.dtos.response.user.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDTO {
    private UUID id;
    private UserResponseDTO employee;
    private String content;
    private Integer month;
    private Integer year;
    private boolean sent;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
}
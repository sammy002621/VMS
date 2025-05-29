package com.sammy.enterpriseresourceplanning.dtos.request.deduction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeductionRequestDTO {
    
    private String code;
    
    @NotBlank(message = "Deduction name is required")
    private String name;
    
    @NotNull(message = "Percentage is required")
    @Positive(message = "Percentage must be positive")
    private Double percentage;
}
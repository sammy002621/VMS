package com.sammy.enterpriseresourceplanning.dtos.response.deduction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeductionResponseDTO {
    private UUID id;
    private String code;
    private String name;
    private Double percentage;
}
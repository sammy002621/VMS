package com.sammy.vehiclemanagementsystem.services;

import com.sammy.vehiclemanagementsystem.models.PlateNumber;

import java.util.List;
import java.util.UUID;

public interface IPlateNumberService {
    PlateNumber generateAndAssignPlateNumberToOwner(UUID ownerId);

    List<PlateNumber> getAllPlateNumbers();
}

package com.sammy.vehiclemanagementsystem.services.impl;

import com.sammy.vehiclemanagementsystem.enums.EPlateStatus;
import com.sammy.vehiclemanagementsystem.exceptions.NotFoundException;
import com.sammy.vehiclemanagementsystem.models.Owner;
import com.sammy.vehiclemanagementsystem.models.PlateNumber;
import com.sammy.vehiclemanagementsystem.repositories.IOwnerRepository;
import com.sammy.vehiclemanagementsystem.repositories.IPlateNumberRepository;
import com.sammy.vehiclemanagementsystem.services.IPlateNumberService;
import com.sammy.vehiclemanagementsystem.utils.helpers.PlateNumberGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlateNumberServiceImpl implements IPlateNumberService {

    private final IPlateNumberRepository plateNumberRepository;
    private final IOwnerRepository ownerRepository;
    private final PlateNumberGenerator plateNumberGenerator;

    @Override
    @Transactional
    public PlateNumber generateAndAssignPlateNumberToOwner(UUID ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner not found with ID: " + ownerId));

        String plateValue = plateNumberGenerator.generateNextPlateNumber();

        PlateNumber plateNumber = PlateNumber.builder()
                .owner(owner)
                .plateNumber(plateValue)
                .plateStatus(EPlateStatus.AVAILABLE)
                .issuedDate(LocalDateTime.now())
                .build();

        return plateNumberRepository.save(plateNumber);
    }

    @Override
    public List<PlateNumber> getAllPlateNumbers() {
        return plateNumberRepository.findAll();
    }
}

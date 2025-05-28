package com.sammy.vehiclemanagementsystem.services.impl;

import com.sammy.vehiclemanagementsystem.dtos.request.vehicle.TransferVehicleDTO;
import com.sammy.vehiclemanagementsystem.dtos.response.vehicle.VehicleOwnershipResponseDTO;
import com.sammy.vehiclemanagementsystem.enums.EPlateStatus;
import com.sammy.vehiclemanagementsystem.exceptions.AppException;
import com.sammy.vehiclemanagementsystem.exceptions.NotFoundException;
import com.sammy.vehiclemanagementsystem.models.Owner;
import com.sammy.vehiclemanagementsystem.models.OwnershipRecord;
import com.sammy.vehiclemanagementsystem.models.PlateNumber;
import com.sammy.vehiclemanagementsystem.models.Vehicle;
import com.sammy.vehiclemanagementsystem.repositories.IOwnerRepository;
import com.sammy.vehiclemanagementsystem.repositories.IOwnershipRecordRepository;
import com.sammy.vehiclemanagementsystem.repositories.IPlateNumberRepository;
import com.sammy.vehiclemanagementsystem.repositories.IVehicleRepository;
import com.sammy.vehiclemanagementsystem.services.IOwnershipRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnershipRecordServiceImpl implements IOwnershipRecordService {

    private final IOwnershipRecordRepository ownershipRecordRepository;
    private final IOwnerRepository ownerRepository;
    private final IPlateNumberRepository plateNumberRepository;
    private final IVehicleRepository vehicleRepository;


    @Override
    public void transferVehicleOwnership(TransferVehicleDTO dto) {
        try{
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new NotFoundException("Vehicle not found with ID: " + dto.getVehicleId()));

            Owner newOwner = ownerRepository.findById(dto.getNewOwnerId())
                    .orElseThrow(() -> new NotFoundException("Owner not found with ID: " + dto.getNewOwnerId()));

            PlateNumber newPlate = plateNumberRepository.findById(dto.getNewPlateId())
                    .orElseThrow(() -> new NotFoundException("Plate number not found with ID: " + dto.getNewPlateId()));

            if(!newPlate.getOwner().getId().equals(newOwner.getId())){
                throw new IllegalStateException("New plate number does not belong to new owner");
            }

            if(newPlate.getPlateStatus() != EPlateStatus.AVAILABLE){
                throw new IllegalStateException("Plate number is already in use");
            }

            PlateNumber currentPlate = vehicle.getCurrentPlate();

            if(currentPlate != null){
                currentPlate.setPlateStatus(EPlateStatus.AVAILABLE);
                plateNumberRepository.save(currentPlate);
            }

            vehicle.setCurrentPlate(newPlate);
            vehicle.setPrice(dto.getPurchasePrice());
            newPlate.setPlateStatus(EPlateStatus.IN_USE);
            plateNumberRepository.save(newPlate);


            vehicle.setOwner(newOwner);
            vehicleRepository.save(vehicle);

            OwnershipRecord ownershipRecord = OwnershipRecord.builder()
                    .vehicle(vehicle)
                    .plateNumber(newPlate)
                    .owner(newOwner)
                    .purchasePrice(dto.getPurchasePrice())
                    .transferDate(LocalDateTime.now())
                    .build();

            ownershipRecordRepository.save(ownershipRecord);
        } catch (Exception e) {
            throw new AppException("Failed to transfer vehicle ownership: " + e.getMessage());
        }
    }

    @Override
    public List<VehicleOwnershipResponseDTO> getOwnershipHistoryByChassis(String chassisNumber) {
        List<OwnershipRecord> ownershipRecords = ownershipRecordRepository.findOwnershipRecordsByVehicle_ChassisNumber(chassisNumber);
        return ownershipRecords.stream()
                .sorted(Comparator.comparing(OwnershipRecord::getTransferDate).reversed())
                .map(record -> {
                    VehicleOwnershipResponseDTO dto = new VehicleOwnershipResponseDTO();
                    dto.setOwnerName(record.getOwner().getProfile().getFirstName() + " " + record.getOwner().getProfile().getLastName());
                    dto.setPlateNumber(record.getPlateNumber().getPlateNumber());
                    dto.setPurchasePrice(record.getPurchasePrice());
                    dto.setTransferDate(record.getTransferDate());
                    return dto;
                })
                .toList();
    }

    @Override
    public List<VehicleOwnershipResponseDTO> getOwnershipHistoryByPlate(String plateNumber) {
        List<OwnershipRecord> ownershipRecords = ownershipRecordRepository.findOwnershipRecordsByPlateNumber_PlateNumber(plateNumber);
        return ownershipRecords.stream()
                .sorted(Comparator.comparing(OwnershipRecord::getTransferDate).reversed())
                .map(record -> {
                    VehicleOwnershipResponseDTO dto = new VehicleOwnershipResponseDTO();
                    dto.setOwnerName(record.getOwner().getProfile().getFirstName() + " " + record.getOwner().getProfile().getLastName());
                    dto.setPlateNumber(record.getPlateNumber().getPlateNumber());
                    dto.setPurchasePrice(record.getPurchasePrice());
                    dto.setTransferDate(record.getTransferDate());
                    return dto;
                })
                .toList();
    }

}

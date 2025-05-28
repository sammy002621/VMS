package com.sammy.vehiclemanagementsystem.services.impl;

import com.sammy.vehiclemanagementsystem.dtos.request.vehicle.CreateVehicleDTO;
import com.sammy.vehiclemanagementsystem.dtos.request.vehicle.UpdateVehicleDTO;
import com.sammy.vehiclemanagementsystem.dtos.response.vehicle.VehicleResponseDTO;
import com.sammy.vehiclemanagementsystem.enums.EPlateStatus;
import com.sammy.vehiclemanagementsystem.exceptions.AppException;
import com.sammy.vehiclemanagementsystem.exceptions.BadRequestException;
import com.sammy.vehiclemanagementsystem.exceptions.NotFoundException;
import com.sammy.vehiclemanagementsystem.models.Owner;
import com.sammy.vehiclemanagementsystem.models.OwnershipRecord;
import com.sammy.vehiclemanagementsystem.models.PlateNumber;
import com.sammy.vehiclemanagementsystem.models.Vehicle;
import com.sammy.vehiclemanagementsystem.repositories.IOwnerRepository;
import com.sammy.vehiclemanagementsystem.repositories.IOwnershipRecordRepository;
import com.sammy.vehiclemanagementsystem.repositories.IPlateNumberRepository;
import com.sammy.vehiclemanagementsystem.repositories.IVehicleRepository;
import com.sammy.vehiclemanagementsystem.services.IVehicleService;
import com.sammy.vehiclemanagementsystem.utils.Mapper;
import com.sammy.vehiclemanagementsystem.utils.helpers.ChassisNumberGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements IVehicleService {

    private final IVehicleRepository vehicleRepository;
    private final IOwnerRepository ownerRepository;
    private final IPlateNumberRepository plateNumberRepository;
    private final IOwnershipRecordRepository ownershipRecordRepository;
    private final ChassisNumberGenerator chassisNumberGenerator;

    // let's create an owner and a plate number and then assign all those to a vehicle


    @Override
    @Transactional
    public VehicleResponseDTO createVehicle(CreateVehicleDTO dto) {
        try{

            Owner owner = ownerRepository.findById(dto.getOwnerId())
                    .orElseThrow(() -> new NotFoundException("Owner not found with ID: " + dto.getOwnerId()));

            PlateNumber plateNumber = plateNumberRepository.findById(dto.getPlateId())
                    .orElseThrow(() -> new NotFoundException("Plate number not found with ID: " + dto.getPlateId()));

            if(!plateNumber.getPlateStatus().equals(EPlateStatus.AVAILABLE)){
                throw new BadRequestException("Plate number is already in use");
            }

            if(!plateNumber.getOwner().getId().equals(owner.getId())){
                throw  new BadRequestException("Plate number does not belong to owner");
            }


            Vehicle vehicle = Mapper.getMapper().map(dto, Vehicle.class);

            chassisNumberGenerator.generateUniqueChassisNumberWithRetries(chassisNumber -> {
                vehicle.setChassisNumber(chassisNumber);
                vehicle.setOwner(owner);
                vehicle.setCurrentPlate(plateNumber);
                vehicleRepository.save(vehicle);
            });


            plateNumber.setPlateStatus(EPlateStatus.IN_USE);
            plateNumberRepository.save(plateNumber);

// after vehicle is created  ownership record of the vehicle is recorded
            OwnershipRecord ownershipRecord = OwnershipRecord.builder()
                    .vehicle(vehicle)
                    .owner(owner)
                    .plateNumber(plateNumber)
                    .purchasePrice(dto.getPrice())
                    .transferDate(LocalDateTime.now())
                    .build();

            ownershipRecordRepository.save(ownershipRecord);

            return Mapper.getMapper().map(vehicle, VehicleResponseDTO.class);
        }catch (Exception e){
            throw new AppException("Error creating vehicle: " + e.getMessage());
        }
    }

    @Override
    public VehicleResponseDTO getVehicle(UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));
        return Mapper.getMapper().map(vehicle, VehicleResponseDTO.class);
    }

    @Override
    public VehicleResponseDTO searchVehicle(String keyword) {
        Vehicle vehicle = vehicleRepository.findVehicleByCurrentPlate_PlateNumber(keyword)
                .or(() -> vehicleRepository.findVehicleByChassisNumber(keyword))
                .or(() -> vehicleRepository.findVehicleByOwner_Profile_NationalId(keyword))
                .or(() -> vehicleRepository.findVehicleByOwner_Profile_Email(keyword))
                .orElseThrow(() -> new NotFoundException("Vehicle not found with keyword: " + keyword));
        return Mapper.getMapper().map(vehicle, VehicleResponseDTO.class) ;
    }



    @Override
    public VehicleResponseDTO updateVehicle(UUID vehicleId, UpdateVehicleDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));

        try{
            Mapper.getMapper().map(dto, vehicle);
            vehicle = vehicleRepository.save(vehicle);
            return Mapper.getMapper().map(vehicle, VehicleResponseDTO.class);
        }catch (Exception e){
            throw new AppException("Error updating vehicle: " + e.getMessage());
        }

    }

    @Override
    public List<Page<VehicleResponseDTO>> getAllVehicles(Pageable pageable) {
        Page<Vehicle> page = vehicleRepository.findAll(pageable);
        return List.of(page.map(vehicle -> Mapper.getMapper().map(vehicle, VehicleResponseDTO.class)));
    }

    @Override
    public Page<VehicleResponseDTO> getAllVehiclesByOwnerId(UUID ownerId, Pageable pageable) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner not found"));

        Page<Vehicle> page = vehicleRepository.findByOwner(owner, pageable);

        return page.map(vehicle -> Mapper.getMapper().map(vehicle, VehicleResponseDTO.class));
    }


    @Override
    public void deleteVehicle(UUID vehicleId) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));
        vehicleRepository.delete(vehicle);

    }


}

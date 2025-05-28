package com.sammy.vehiclemanagementsystem.services;

import com.sammy.vehiclemanagementsystem.dtos.request.vehicle.TransferVehicleDTO;
import com.sammy.vehiclemanagementsystem.dtos.response.vehicle.VehicleOwnershipResponseDTO;

import java.util.List;

public interface IOwnershipRecordService {

    void transferVehicleOwnership(TransferVehicleDTO dto);
    List<VehicleOwnershipResponseDTO> getOwnershipHistoryByChassis(String chassisNumber);
    List<VehicleOwnershipResponseDTO> getOwnershipHistoryByPlate(String plateNumber);


}

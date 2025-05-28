package com.sammy.vehiclemanagementsystem.dtos.request.vehicle;

import com.sammy.vehiclemanagementsystem.annotations.vehicle.price.ValidPrice;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TransferVehicleDTO {
    private UUID vehicleId;
    private UUID newOwnerId;
    private UUID newPlateId;
    @ValidPrice
    private double purchasePrice;
}

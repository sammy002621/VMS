package com.sammy.vehiclemanagementsystem.dtos.request.vehicle;


import com.sammy.vehiclemanagementsystem.annotations.vehicle.manufacturedYear.ValidManufacturedYear;
import com.sammy.vehiclemanagementsystem.annotations.vehicle.price.ValidPrice;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateVehicleDTO {

    private UUID ownerId;
    private UUID plateId;
    private String manufacturer;
    @ValidManufacturedYear
    private int manufacturedYear;
    private String model;
    @ValidPrice
    private double price;
}

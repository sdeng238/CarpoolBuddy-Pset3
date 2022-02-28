package com.example.carpoolbuddy_pset3;

public class ElectricCar extends Vehicle{
    public ElectricCar()
    {

    }

    public ElectricCar(String owner, String ownerEmail, String model, int capacity, String vehicleID, boolean open, String vehicleType, double basePrice) {
        super(owner, ownerEmail, model, capacity, vehicleID, open, vehicleType, basePrice);
    }
}

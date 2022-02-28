package com.example.carpoolbuddy_pset3;

import java.io.Serializable;
import java.util.ArrayList;

public class Vehicle implements Serializable {
    public String owner;
    public String ownerEmail;
    public String model;
    public int capacity;
    public String vehicleID;
    public ArrayList<String> ridersUIDs;
    public boolean open;
    public String vehicleType;
    public double basePrice;

    public Vehicle()
    {

    }

    public Vehicle(String owner, String ownerEmail, String model, int capacity, String vehicleID, boolean open, String vehicleType, double basePrice) {
        this.owner = owner;
        this.ownerEmail = ownerEmail;
        this.model = model;
        this.capacity = capacity;
        this.vehicleID = vehicleID;
        this.ridersUIDs = new ArrayList<>();
        this.open = open;
        this.vehicleType = vehicleType;
        this.basePrice = basePrice;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getVehicleID()
    {
        return vehicleID;
    }

    public String getOwner() {
        return owner;
    }

    public String getModel() {
        return model;
    }

    public int getCapacity() {
        return capacity;
    }

    public ArrayList<String> getRidersUIDs() {
        return ridersUIDs;
    }

    public boolean isOpen() {
        return open;
    }

    public double getBasePrice() {
        return basePrice;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "owner='" + owner + '\'' +
                ", model='" + model + '\'' +
                ", capacity=" + capacity +
                ", vehicleID='" + vehicleID + '\'' +
                ", ridersUIDs=" + ridersUIDs +
                ", open=" + open +
                ", vehicleType='" + vehicleType + '\'' +
                ", basePrice=" + basePrice +
                '}';
    }
}

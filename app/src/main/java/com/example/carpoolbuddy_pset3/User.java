package com.example.carpoolbuddy_pset3;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class User implements Serializable {
    public String uid;
    public String name;
    public String email;
    public String userType;
    public double balance;
    public double priceMultiplier;
    public ArrayList<String> ownedVehicles;

    public User()
    {

    }

    public User(String name, String email, String userType) {
        this.uid = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.balance = 1000.0;
        this.ownedVehicles = new ArrayList<>();
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public double getPriceMultiplier() {
        return priceMultiplier;
    }

    public void setPriceMultiplier(double priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    public ArrayList<String> getOwnedVehicles() {
        return ownedVehicles;
    }

    public void setOwnedVehicles(ArrayList<String> ownedVehicles) {
        this.ownedVehicles = ownedVehicles;
    }

    public void addToOwnedVehicles(String vehicleID) {
        ownedVehicles.add(vehicleID);
    }
}

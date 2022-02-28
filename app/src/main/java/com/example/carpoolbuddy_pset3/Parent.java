package com.example.carpoolbuddy_pset3;

import android.net.Uri;

import java.util.ArrayList;

public class Parent extends User
{
    public Parent()
    {

    }

    public Parent(String name, String email, String userType) {
        super(name, email, userType);
        this.priceMultiplier = 1.0;
    }
}

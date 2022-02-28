package com.example.carpoolbuddy_pset3;

import android.net.Uri;

import java.util.ArrayList;

public class Alumni extends User
{
    public String graduateYear;

    public Alumni()
    {

    }

    public Alumni(String name, String email, String userType, String graduateYear) {
        super(name, email, userType);
        this.graduateYear = graduateYear;
        this.priceMultiplier = 1.0;
    }
}

package com.example.carpoolbuddy_pset3;

import android.net.Uri;

import java.util.ArrayList;

public class Teacher extends User
{
    public String inSchoolTitle;

    public Teacher()
    {

    }

    public Teacher(String name, String email, String userType, String inSchoolTitle) {
        super(name, email, userType);
        this.inSchoolTitle = inSchoolTitle;
        this.priceMultiplier = 0.5;
    }
}

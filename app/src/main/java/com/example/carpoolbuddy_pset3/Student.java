package com.example.carpoolbuddy_pset3;

import android.net.Uri;

import java.util.ArrayList;

public class Student extends User
{
    public String graduatingYear;

    public Student()
    {

    }

    public Student(String name, String email, String userType, String graduatingYear) {
        super(name, email, userType);
        this.graduatingYear = graduatingYear;
        this.priceMultiplier = 0.5;
    }
}

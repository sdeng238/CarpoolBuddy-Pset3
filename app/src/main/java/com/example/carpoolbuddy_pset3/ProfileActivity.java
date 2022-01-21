package com.example.carpoolbuddy_pset3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    public void goToVehiclesInfoActivity(View v)
    {
        Intent goToVehiclesInfoActivity = new Intent(this, VehiclesInfoActivity.class);
        startActivity(goToVehiclesInfoActivity);
    }

    public void goToAddVehicleActivity(View v)
    {
        Intent goToAddVehicleActivity = new Intent(this, AddVehicleActivity.class);
        startActivity(goToAddVehicleActivity);
    }
}


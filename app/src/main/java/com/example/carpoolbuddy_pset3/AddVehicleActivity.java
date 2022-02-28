package com.example.carpoolbuddy_pset3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.UUID;

public class AddVehicleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore firestore;

    private EditText modelField;
    private EditText capacityField;

    private String selectedVehicleType;
    private Spinner vehicleTypeSpinner;

    private EditText basePriceField;

    private Switch openSwitch;
    private boolean open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        modelField = findViewById(R.id.editTextModel);
        capacityField = findViewById(R.id.editTextCapacity);

        vehicleTypeSpinner = findViewById(R.id.vehicleTypeSpinner);
        ArrayAdapter<CharSequence> vehicleTypeSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.vehicle_types, android.R.layout.simple_spinner_item);
        vehicleTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(vehicleTypeSpinnerAdapter);
        vehicleTypeSpinner.setOnItemSelectedListener(this);

        basePriceField = findViewById(R.id.editTextBasePrice);

        openSwitch = (Switch) findViewById(R.id.openSwitch);
        //if user checks the open switch, the vehicle is opened
        openSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b)
            {
                Toast.makeText(getBaseContext(), "Vehicle opened", Toast.LENGTH_SHORT).show();
                open = true;
            }
            else
            {
                Toast.makeText(getBaseContext(), "Vehicle closed", Toast.LENGTH_SHORT).show();
                open = false;
            }
        });
    }

    //checks if the vehicle info is valid
    public boolean formValid(String modelString, int capacityInt, double basePriceDouble)
    {
        if((modelString != null) && (capacityInt > 0) && (basePriceDouble > 0))
        {
            return true;
        }

        return false;
    }

    public void addNewVehicle(View v)
    {
        String modelString = modelField.getText().toString();
        int capacityInt = Integer.valueOf(capacityField.getText().toString());
        double basePriceDouble = Double.valueOf(basePriceField.getText().toString());

        //fetches current user from firebase
        firestore.collection("users/currUserID/currentUsers").whereEqualTo("email", mUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    Log.d("FETCH USER", "fetchUser:success");

                    for(DocumentSnapshot ds : task.getResult().getDocuments())
                    {
                        User currUser = ds.toObject(User.class);

                        if(formValid(modelString, capacityInt, basePriceDouble))
                        {
                            //creates Vehicle object of respective vehicleTypes
                            if(selectedVehicleType.equals("Electric car"))
                            {
                                ElectricCar newVehicle = new ElectricCar(currUser.getName(), mUser.getEmail(), modelString, capacityInt, UUID.randomUUID().toString(), open, "electric car", basePriceDouble);
                                checkAddNewVehicle(newVehicle);
                            }
                            else if(selectedVehicleType.equals("Fossil fuel car"))
                            {
                                FossilFuelCar newVehicle = new FossilFuelCar(currUser.getName(), mUser.getEmail(), modelString, capacityInt, UUID.randomUUID().toString(), open, "fossil fuel car", basePriceDouble);
                                checkAddNewVehicle(newVehicle);
                            }
                            else if(selectedVehicleType.equals("Motorcycle"))
                            {
                                Motorcycle newVehicle = new Motorcycle(currUser.getName(), mUser.getEmail(), modelString, capacityInt, UUID.randomUUID().toString(), open, "motorcycle", basePriceDouble);
                                checkAddNewVehicle(newVehicle);
                            }
                        }
                        else
                        {
                            Toast.makeText(getBaseContext(), "Information invalid", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else
                {
                    Log.w("FETCH USER", "fetchUser:failure" + task.getException());
                }
            }
        });
    }

    //vehicle's type is set as the selected type in the spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        selectedVehicleType = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }

    //saves and updates firebase documents with newly created vehicle
    public void checkAddNewVehicle(Vehicle newVehicle)
    {
        //check if vehicle is null
        if(newVehicle != null)
        {
            //add vehicle to current users' owned vehicles collection
            firestore.collection("users/currUserID/ownedVehicles").add(newVehicle).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    Log.d("ADD NEW VEHICLE TO CURR USER", "addNewVehicleToCurrUserID:success");
                }
                else
                {
                    Log.w("ADD NEW VEHICLE TO CURR USER", "addNewVehicleToCurrUserID:failure", task.getException());
                    Toast.makeText(getBaseContext(), "Add new vehicle to current user failed!", Toast.LENGTH_SHORT);
                }
            });

            //add vehicle to ownedVehicles ArrayList in user
            firestore.collection("users/currUserID/currentUsers").whereEqualTo("email", mUser.getEmail()).get().addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    Log.d("ADD NEW VEHICLE TO ARRAYLIST", "addNewVehicleToArrayList:success");

                    for(DocumentSnapshot ds : task.getResult().getDocuments())
                    {
                        firestore.collection("users/currUserID/currentUsers").document(ds.getId()).update("ownedVehicles", FieldValue.arrayUnion(newVehicle.getVehicleID()));
                    }
                }
                else
                {
                    Log.w("ADD NEW VEHICLE TO ARRAYLIST", "addNewVehicleToArrayList:failure", task.getException());
                }
            });

            //add vehicle to vehicles collection
            firestore.collection("vehicles").add(newVehicle).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    Log.d("ADD NEW VEHICLE", "addNewVehicle:success");

                    goToVehiclesInfoActivity();
                }
                else
                {
                    Log.w("ADD NEW VEHICLE", "addNewVehicle:failure", task.getException());
                    Toast.makeText(getBaseContext(), "Add new vehicle failed!", Toast.LENGTH_SHORT);
                }
            });
        }
        else
        {
            Toast.makeText(getBaseContext(), "Add new vehicle failed!", Toast.LENGTH_SHORT);
        }
    }

    //brings user to vehiclesInfoActivity
    public void goToVehiclesInfoActivity()
    {
        Intent goToVehiclesInfoActivity = new Intent(this, VehiclesInfoActivity.class);
        startActivity(goToVehiclesInfoActivity);
    }
}
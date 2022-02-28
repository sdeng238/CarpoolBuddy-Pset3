package com.example.carpoolbuddy_pset3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

public class VehicleProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser mUser;

    //current vehicle being shown
    private Vehicle vehicleInfo;

    private ImageView vehicleProfileImageView;
    private int[] vehicleImageResources;

    private TextView priceTextView;
    private TextView vehiclePriceTextView;
    private TextView vehicleModelTextView;
    private TextView vehicleMaxCapacityTextView;
    private TextView vehicleOwnerTextView;
    private Button bookRideButton;

    private Switch vehicleOpenSwitch;
    private boolean open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_profile);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();

        vehicleInfo = (Vehicle) getIntent().getSerializableExtra("vehicle");

        vehicleProfileImageView = (ImageView) findViewById(R.id.vehicleProfileImageView);
        int[] vehicleImageResources = new int[3];
        //save image resource ints of vehicle images into array
        vehicleImageResources[0] = getResources().getIdentifier("@drawable/electric_car", null, this.getPackageName());
        vehicleImageResources[1] = getResources().getIdentifier("@drawable/fossil_fuel_car", null, this.getPackageName());
        vehicleImageResources[2] = getResources().getIdentifier("@drawable/motorcycle", null, this.getPackageName());

        priceTextView = findViewById(R.id.priceTextView);
        vehiclePriceTextView = findViewById(R.id.vehiclePriceTextView);
        vehicleModelTextView = findViewById(R.id.vehicleModelTextView);
        vehicleMaxCapacityTextView = findViewById(R.id.vehicleMaxCapacityTextView);
        vehicleOwnerTextView = findViewById(R.id.vehicleOwnerTextView);
        bookRideButton = findViewById(R.id.bookRideButton);

        vehicleModelTextView.setText(vehicleInfo.getModel());
        vehicleMaxCapacityTextView.setText(Integer.toString(vehicleInfo.getCapacity()));
        vehicleOwnerTextView.setText(vehicleInfo.getOwner());

        //display image according to vehicleType
        if(vehicleInfo.getVehicleType().equals("electric car"))
        {
            vehicleProfileImageView.setImageResource(vehicleImageResources[0]);
        }
        else if(vehicleInfo.getVehicleType().equals("fossil fuel car"))
        {
            vehicleProfileImageView.setImageResource(vehicleImageResources[1]);
        }
        else if(vehicleInfo.getVehicleType().equals("motorcycle"))
        {
            vehicleProfileImageView.setImageResource(vehicleImageResources[2]);
        }

        //if vehicle is already full, don't show bookRideButton
        if(vehicleInfo.getCapacity() == 0)
        {
            bookRideButton.setVisibility(View.INVISIBLE);
        }

        vehicleOpenSwitch = (Switch) findViewById(R.id.vehicleOpenSwitch);

        //display vehicle open status according to whether switch is checked or not
        if(vehicleInfo.isOpen())
        {
            vehicleOpenSwitch.setChecked(true);
            vehicleOpenSwitch.setText("Open");
        }
        else
        {
            vehicleOpenSwitch.setChecked(false);
            vehicleOpenSwitch.setText("Closed");
        }

        vehicleOpenSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b)
            {
                //if switch is on, update vehicle open to true and display "open"
                Toast.makeText(getBaseContext(), "Vehicle opened", Toast.LENGTH_SHORT).show();
                open = true;
                vehicleOpenSwitch.setText("Open");
                firestore.collection("vehicles").whereEqualTo("vehicleID", vehicleInfo.getVehicleID()).get().addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        for(DocumentSnapshot ds : task.getResult().getDocuments())
                        {
                            firestore.collection("vehicles").document(ds.getId()).update("open", true);
                        }
                    }
                });
            }
            else
            {
                //if switch is off, update vehicle open to false and display "closed"
                Toast.makeText(getBaseContext(), "Vehicle closed", Toast.LENGTH_SHORT).show();
                open = false;
                vehicleOpenSwitch.setText("Closed");
                firestore.collection("vehicles").whereEqualTo("vehicleID", vehicleInfo.getVehicleID()).get().addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        for(DocumentSnapshot ds : task.getResult().getDocuments())
                        {
                            firestore.collection("vehicles").document(ds.getId()).update("open", false);
                        }
                    }
                });
            }
        });

        setUpButtons();
    }

    public void setUpButtons()
    {
        //fetch current user
        firestore.collection("users/currUserID/currentUsers").whereEqualTo("email", mUser.getEmail()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                Log.d("FETCH CURRENT USER", "fetchCurrentUser:success");

                for(DocumentSnapshot ds : task.getResult().getDocuments())
                {
                    User currUser = ds.toObject(User.class);
                    boolean isOwner = false;

                    //check if user's ownedVehicles ArrayList contains current vehicle's id
                    for(String vehicleID : currUser.getOwnedVehicles())
                    {
                        if(vehicleInfo.getVehicleID().equals(vehicleID))
                        {
                            isOwner = true;
                        }
                    }

                    if(isOwner)
                    {
                        //if user is owner, don't show bookRideButton
                        bookRideButton.setVisibility(View.INVISIBLE);
                        priceTextView.setVisibility(View.INVISIBLE);
                        vehiclePriceTextView.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        //if user is not owner, don't show vehicleOpenSwitch
                        vehicleOpenSwitch.setVisibility(View.INVISIBLE);

                        //show correct price depending on user's role
                        if(currUser.getUserType().equals("student"))
                        {
                            Student currStudent = ds.toObject(Student.class);
                            vehiclePriceTextView.setText(Double.toString(vehicleInfo.getBasePrice()*currStudent.getPriceMultiplier()));
                        }
                        else if(currUser.getUserType().equals("teacher"))
                        {
                            Teacher currTeacher = ds.toObject(Teacher.class);
                            vehiclePriceTextView.setText(Double.toString(vehicleInfo.getBasePrice()*currTeacher.getPriceMultiplier()));
                        }
                        else if(currUser.getUserType().equals("alumni"))
                        {
                            Alumni currAlumni = ds.toObject(Alumni.class);
                            vehiclePriceTextView.setText(Double.toString(vehicleInfo.getBasePrice()*currAlumni.getPriceMultiplier()));
                        }
                        else if(currUser.getUserType().equals("parent"))
                        {
                            Parent currParent = ds.toObject(Parent.class);
                            vehiclePriceTextView.setText(Double.toString(vehicleInfo.getBasePrice()*currParent.getPriceMultiplier()));
                        }
                    }

                    //when bookRide button is clicked, call bookRide method
                    bookRideButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bookRide(currUser);
                        }
                    });

                    break;
                }
            }
            else
            {
                Log.w("FETCH CURRENT USER", "fetchCurrentUser:failure", task.getException());
            }
        });
    }

    public void bookRide(User currUser)
    {
        //get bookingPrice of the current vehicle
        double bookingPrice = Double.valueOf(vehiclePriceTextView.getText().toString());

        //fetch current vehicle
        firestore.collection("vehicles").whereEqualTo("vehicleID", vehicleInfo.getVehicleID()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                Log.d("FETCH VEHICLE", "fetchVehicle:success");

                for(DocumentSnapshot ds : task.getResult().getDocuments())
                {
                    //decrease vehicle capacity by 1
                    firestore.collection("vehicles").document(ds.getId()).update("capacity", vehicleInfo.getCapacity() - 1);
                    //add user's uid to ridersUID ArrayList in vehicle
                    firestore.collection("vehicles").document(ds.getId()).update("ridersUIDs", FieldValue.arrayUnion(currUser.getUid()));

                    Toast.makeText(this, "Vehicle booked!", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Log.w("FETCH VEHICLE", "fetchVehicle:failure", task.getException());
                Toast.makeText(this, "Vehicle booking failed!", Toast.LENGTH_SHORT).show();
            }
        });

        //fetch current user
        firestore.collection("users/currUserID/currentUsers").whereEqualTo("email", mUser.getEmail()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                Log.d("FETCH USER", "fetchUser:success");

                for(DocumentSnapshot ds : task.getResult().getDocuments())
                {
                    //decrease user balance by price of booking
                    firestore.collection("users/currUserID/currentUsers").document(ds.getId()).update("balance", currUser.getBalance() - bookingPrice);
                }
            }
            else
            {
                Log.w("FETCH USER", "fetchUser:failure", task.getException());
                Toast.makeText(getBaseContext(), "Vehicle booking failed!", Toast.LENGTH_SHORT);
            }
        });

        //fetch vehicle's owner
        firestore.collection("users/currUserID/currentUsers").whereEqualTo("name", vehicleInfo.getOwner()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                Log.d("FETCH USER", "fetchUser:success");

                for(DocumentSnapshot ds : task.getResult().getDocuments())
                {
                    User ownerUser = ds.toObject(User.class);

                    //update user balance
                    firestore.collection("users/currUserID/currentUsers").document(ds.getId()).update("balance", ownerUser.getBalance() + bookingPrice);
                }

                //bring user back to UserProfileActivity
                startActivity(new Intent(getBaseContext(), UserProfileActivity.class));
            }
            else
            {
                Log.w("FETCH USER", "fetchUser:failure", task.getException());
                Toast.makeText(getBaseContext(), "Vehicle booking failed!", Toast.LENGTH_SHORT);
            }
        });
    }
}
package com.example.carpoolbuddy_pset3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

public class VehiclesInfoActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser mUser;

    private TextView userBalanceTextView;

    private RecyclerView vehiclesRecView;
    private ArrayList<Vehicle> vehiclesData;

    private int[] vehicleImageResources;
    private int calmingGreenColour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicles_info);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();

        userBalanceTextView = findViewById(R.id.userBalanceTextView);

        vehiclesRecView = findViewById(R.id.vehiclesRecView);

        vehiclesData = new ArrayList<>();

        //fetch current user
        firestore.collection("users/currUserID/currentUsers").whereEqualTo("email", mUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(DocumentSnapshot ds : task.getResult().getDocuments())
                    {
                        User currUser = ds.toObject(User.class);
                        //display user's balance
                        userBalanceTextView.setText(Double.toString(currUser.getBalance()));
                    }
                }
            }
        });

        int[] vehicleImageResources = new int[3];
        //save image resource ints of vehicle images to array
        vehicleImageResources[0] = getResources().getIdentifier("@drawable/electric_car", null, this.getPackageName());
        vehicleImageResources[1] = getResources().getIdentifier("@drawable/fossil_fuel_car", null, this.getPackageName());
        vehicleImageResources[2] = getResources().getIdentifier("@drawable/motorcycle", null, this.getPackageName());

        //get calming green's color int and save to calmingGreenColour
        calmingGreenColour = getResources().getColor(R.color.calming_green);

        //create ArrayLists for green vehicles and non-green vehicles respectively
        ArrayList<Vehicle> greenVehicles = new ArrayList<>();
        ArrayList<Vehicle> pollutingVehicles = new ArrayList<>();

        //fetch user's vehicles
        //add vehicles owned by user regardless if it is open or closed
        firestore.collection("vehicles").whereEqualTo("ownerEmail", mUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(DocumentSnapshot ds : task.getResult().getDocuments())
                    {
                        Vehicle currVehicle = ds.toObject(Vehicle.class);
                        //add electric and non-electric vehicles into separate ArrayLists
                        if(currVehicle.getVehicleType().equals("electric car"))
                        {
                            greenVehicles.add(currVehicle);
                        }
                        else
                        {
                            pollutingVehicles.add(currVehicle);
                        }
                    }

                    //fetch open vehicles
                    firestore.collection("vehicles").whereEqualTo("open", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            if(task.isSuccessful())
                            {
                                Log.d("FETCH VEHICLES", "fetchVehicles:success");

                                for(DocumentSnapshot ds : task.getResult().getDocuments())
                                {
                                    Vehicle currVehicle = ds.toObject(Vehicle.class);

                                    //if vehicle is not owned by user
                                    if(!currVehicle.getOwnerEmail().equals(mUser.getEmail()))
                                    {
                                        //add electric and non-electric vehicles into separate ArrayLists
                                        if(currVehicle.getVehicleType().equals("electric car"))
                                        {
                                            greenVehicles.add(currVehicle);
                                        }
                                        else
                                        {
                                            pollutingVehicles.add(currVehicle);
                                        }
                                    }
                                }

                                //join two ArrayLists so electric vehicles appear first in vehiclesRecView
                                vehiclesData.addAll(greenVehicles);
                                vehiclesData.addAll(pollutingVehicles);

                                //make and set adapter
                                VehiclesAdapter myAdapter = new VehiclesAdapter(vehiclesData, vehicleImageResources, greenVehicles.size(), calmingGreenColour);
                                vehiclesRecView.setAdapter(myAdapter);
                                //controls where things are placed
                                vehiclesRecView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                            }
                            else
                            {
                                Log.w("FETCH VEHICLES", "fetchVehicles:failure", task.getException());
                                Toast.makeText(getBaseContext(), "Fetch vehicles failed!", Toast.LENGTH_SHORT);
                            }
                        }
                    });
                }
            }
        });
    }

    public void back(View v)
    {
        startActivity(new Intent(this, UserProfileActivity.class));
    }

    public static class VehiclesAdapter extends RecyclerView.Adapter<VehicleViewHolder>
    {
        ArrayList<Vehicle> mData;
        int[] imageResources;
        int greenNum;
        int greenColour;

        public VehiclesAdapter(ArrayList data, int[] vehicleImageResources, int numOfGreenVehicles, int calmingGreenColour)
        {
            mData = data;
            imageResources = vehicleImageResources;
            greenNum = numOfGreenVehicles;
            greenColour = calmingGreenColour;
        }

        @NonNull
        @Override
        public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_row_view, parent, false);

            VehicleViewHolder holder = new VehicleViewHolder(myView, mData);

            return holder;
        }

        @Override
        //tell each holder what to display
        public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position)
        {
            //display info of the current vehicle
            holder.modelNameText.setText(mData.get(position).getModel());
            holder.ownerNameText.setText(mData.get(position).getOwner());
            holder.numberOfSeatsLeftText.setText(String.valueOf(mData.get(position).getCapacity()));

            //display image of the current vehicle type
            if(mData.get(position).getVehicleType().equals("electric car"))
            {
                holder.vehicleImageView.setImageResource(imageResources[0]);
            }
            else if(mData.get(position).getVehicleType().equals("fossil fuel car"))
            {
                holder.vehicleImageView.setImageResource(imageResources[1]);
            }
            else if(mData.get(position).getVehicleType().equals("motorcycle"))
            {
                holder.vehicleImageView.setImageResource(imageResources[2]);
            }

            //set view's background to calming green colour if it displays green vehicle
            if(position < greenNum)
            {
                holder.cardView.setCardBackgroundColor(greenColour);
            }
        }

        @Override
        //tell how many ViewHolders to create
        public int getItemCount()
        {
            return mData.size();
        }
    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView modelNameText;
        protected TextView ownerNameText;
        protected TextView numberOfSeatsLeftText;
        protected ImageView vehicleImageView;
        protected CardView cardView;

        private ArrayList<Vehicle> vehiclesData;

        public VehicleViewHolder(@NonNull View itemView, ArrayList<Vehicle> data)
        {
            super(itemView);

            modelNameText = itemView.findViewById(R.id.modelNameTextView);
            ownerNameText = itemView.findViewById(R.id.ownerNameTextView);
            numberOfSeatsLeftText = itemView.findViewById(R.id.seatsLeftNumberTextView);
            vehicleImageView = (ImageView) itemView.findViewById(R.id.vehicleImageView);
            cardView = itemView.findViewById(R.id.CardView);

            vehiclesData = data;

            //opens VehicleProfileActivity when row is clicked
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToVehicleProfileActivity = new Intent(v.getContext(), VehicleProfileActivity.class);
                    //pass in current Vehicle object in Intent
                    goToVehicleProfileActivity.putExtra("vehicle", (Serializable) vehiclesData.get(getAdapterPosition()));
                    v.getContext().startActivity(goToVehicleProfileActivity);
                }
            });
        }
    }
}
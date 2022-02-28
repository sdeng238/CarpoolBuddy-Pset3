package com.example.carpoolbuddy_pset3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class UserProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore firestore;

    private TextView userNameTextView;
    private TextView profileUserBalanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        userNameTextView = findViewById(R.id.userNameTextView);
        profileUserBalanceTextView = findViewById(R.id.profileUserBalanceTextView);

        //fetch current user
        firestore.collection("users/currUserID/currentUsers").whereEqualTo("email", mUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                for(DocumentSnapshot ds : task.getResult().getDocuments())
                {
                    User currUser = ds.toObject(User.class);
                    //say hello to user
                    userNameTextView.setText(currUser.getName());
                    //display user's balance
                    profileUserBalanceTextView.setText(Double.toString(currUser.getBalance()));
                }
            }
        });
    }

    //bring user to VehiclesInfoActivity
    public void goToVehiclesInfoActivity(View v)
    {
        Intent goToVehiclesInfoActivity = new Intent(this, VehiclesInfoActivity.class);
        startActivity(goToVehiclesInfoActivity);
    }

    //bring user to AddVehicleActivity
    public void goToAddVehicleActivity(View v)
    {
        Intent goToAddVehicleActivity = new Intent(this, AddVehicleActivity.class);
        startActivity(goToAddVehicleActivity);
    }

    //signs user out
    public void signOut(View v)
    {
        FirebaseAuth.getInstance().signOut();

        Intent goToAuthActivity = new Intent(this, AuthActivity.class);
        startActivity(goToAuthActivity);
    }
}


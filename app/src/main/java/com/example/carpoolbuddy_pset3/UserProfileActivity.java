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

/**
 * This class is the app's home page. It displays information like the user's name, balance and allows
 * users to navigate to AddVehicleActivity and VehiclesInfoActivity via buttons. It also provides a
 * signOutButton for the user to sign out of their account.
 *
 * @author Shirley Deng
 * @version 1.0
 */

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

    /**
     * This method is the OnClick method for vehicleInfoButton. It brings the user to VehiclesInfoActivity.
     *
     * @param v The view in which vehicleInfoButton is displayed in.
     */
    //bring user to VehiclesInfoActivity
    public void goToVehiclesInfoActivity(View v)
    {
        Intent goToVehiclesInfoActivity = new Intent(this, VehiclesInfoActivity.class);
        startActivity(goToVehiclesInfoActivity);
    }

    /**
     * This method is the OnClick method for addVehicleButton. It brings the user to AddVehicleActivity.
     *
     * @param v The view in which addVehicleButton is displayed in.
     */
    //bring user to AddVehicleActivity
    public void goToAddVehicleActivity(View v)
    {
        Intent goToAddVehicleActivity = new Intent(this, AddVehicleActivity.class);
        startActivity(goToAddVehicleActivity);
    }

    /**
     * This method is the OnClick method for signOutButton. It signs the user out of their Firebase
     * authentication account and brings them back to AuthActivity.
     *
     * @param v The view in which signOutButton is displayed in.
     */
    //signs user out
    public void signOut(View v)
    {
        FirebaseAuth.getInstance().signOut();

        Intent goToAuthActivity = new Intent(this, AuthActivity.class);
        startActivity(goToAuthActivity);
    }
}


package com.example.carpoolbuddy_pset3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void signOut(View v)
    {
        FirebaseAuth.getInstance().signOut();

        Intent goToAuthActivity = new Intent(this, AuthActivity.class);
        startActivity(goToAuthActivity);
    }

    public void goToProfileActivity(View v)
    {
        Intent goToProfileActivity = new Intent(this, ProfileActivity.class);
        startActivity(goToProfileActivity);
    }
}
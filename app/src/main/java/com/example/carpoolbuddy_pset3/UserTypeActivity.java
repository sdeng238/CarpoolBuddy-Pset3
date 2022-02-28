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
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class UserTypeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore firestore;

    private String selected;
    private Spinner sUserType;

    private EditText graduatingYearEditText;
    private EditText graduateYearEditText;
    private EditText inSchoolTitleEditText;
    private EditText nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        sUserType = findViewById(R.id.userTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sUserType.setAdapter(adapter);
        sUserType.setOnItemSelectedListener(this);

        graduatingYearEditText = findViewById(R.id.graduatingYearEditText);
        graduateYearEditText = findViewById(R.id.graduateYearEditText);
        inSchoolTitleEditText = findViewById(R.id.inSchoolTitleEditText);
        nameEditText = findViewById(R.id.nameEditText);
    }

    public void saveUserToFirebase(View v)
    {
        //create object of userType and add object to firebase
        if(selected.equals("Student"))
        {
            Student newUser = new Student(nameEditText.getText().toString(), mUser.getEmail(), selected.toLowerCase(), graduatingYearEditText.getText().toString());
            firestore.collection("users/currUserID/currentUsers").add(newUser);
        }
        else if(selected.equals("Teacher"))
        {
            Teacher newUser = new Teacher(nameEditText.getText().toString(), mUser.getEmail(), selected.toLowerCase(), inSchoolTitleEditText.getText().toString());
            firestore.collection("users/currUserID/currentUsers").add(newUser);
        }
        else if(selected.equals("Parent"))
        {
            Parent newUser = new Parent(nameEditText.getText().toString(), mUser.getEmail(), selected.toLowerCase());
            firestore.collection("users/currUserID/currentUsers").add(newUser);
        }
        else if(selected.equals("Alumni"))
        {
            Alumni newUser = new Alumni(nameEditText.getText().toString(), mUser.getEmail(), selected.toLowerCase(), graduateYearEditText.getText().toString());
            firestore.collection("users/currUserID/currentUsers").add(newUser);
        }

        //bring user to UserProfileActivity
        startActivity(new Intent(getBaseContext(), UserProfileActivity.class));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        selected = adapterView.getItemAtPosition(i).toString();

        //display and hide EditTexts according to selected role
        if(selected.equals("Student"))
        {
            inSchoolTitleEditText.setVisibility(View.INVISIBLE);
            graduateYearEditText.setVisibility(View.INVISIBLE);
            graduatingYearEditText.setVisibility(View.VISIBLE);
        }
        else if(selected.equals("Teacher"))
        {
            graduatingYearEditText.setVisibility(View.INVISIBLE);
            graduateYearEditText.setVisibility(View.INVISIBLE);
            inSchoolTitleEditText.setVisibility(View.VISIBLE);
        }
        else if(selected.equals("Parent"))
        {
            graduatingYearEditText.setVisibility(View.INVISIBLE);
            graduateYearEditText.setVisibility(View.INVISIBLE);
            inSchoolTitleEditText.setVisibility(View.INVISIBLE);
        }
        else if(selected.equals("Alumni"))
        {
            graduatingYearEditText.setVisibility(View.INVISIBLE);
            inSchoolTitleEditText.setVisibility(View.INVISIBLE);
            graduateYearEditText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }
}
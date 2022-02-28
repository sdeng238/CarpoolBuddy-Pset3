package com.example.carpoolbuddy_pset3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.lang.reflect.Array;

/**
 * This class authenticates the user and allows them to sign in/sign up.
 *
 * @author Shirley Deng
 * @version 1.0
 */

public class AuthActivity extends AppCompatActivity {

    //authenticate and get users
    private FirebaseAuth mAuth;
    //save things to database
    private FirebaseFirestore firestore;

    private EditText emailField;
    private EditText passwordField;

    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        emailField = findViewById(R.id.editTextEmail);
        passwordField = findViewById(R.id.editTextPassword);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                googleSignIn();
            }
        });
    }

    /**
     * This method creates and starts a sign in intent for Google Automatic sign in.
     */
    private void googleSignIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * This method is provided by Google sign in integration.
     * It gets the GoogleSignInAccount object after the user signs in via Google automatic sign in.
     *
     * @param requestCode The code that is requested to Google.
     * @param resultCode The code that Google returned for the request.
     * @param data The intent that brings the user to the Google sign in page.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /**
     * This method takes in the GoogleSignInAccount and converts it into a GoogleSignInAccount object.
     * Using that object, it is able to get the email address that was just signed into and checks if it is a
     * CIS email, if yes, it signs the user in using Firebase email and password authentication.
     *
     * @param completedTask The account that has just been signed into via Google sign in.
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try
        {
            Log.d("GOOGLE SIGN IN", "googleSignInResult:success");
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            if(account != null)
            {
                //check if user document already exists in firebase
                firestore.collection("users/currUserID/currentUsers").whereEqualTo("email", account.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if(task.getResult().getDocuments().size() == 0)
                            {
                                //sign up if not already exist
                                //only allow CIS email to sign up
                                if(account.getEmail().contains("cis") && account.getEmail().contains("edu"))
                                {
                                    signUpWithEmailAndPassword(account.getEmail(), "123456");
                                }
                                else
                                {
                                    Toast.makeText(getBaseContext(), "You are unable to sign up because you are not a CIS user", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                //sign in if already exist
                                signInWithEmailAndPassword(account.getEmail(), "123456");
                            }
                        }
                        else
                        {
                            Toast.makeText(getBaseContext(), "Cannot fetch users!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        catch (ApiException e)
        {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GOOGLE SIGN IN", "googleSignInResult:failed code=" + e.getStatusCode());
        }
    }

    /**
     * This method takes in the user's email address and password entered and
     * signs them in via Firebase email and password authentication.
     *
     * @param email The user's email address.
     * @param password The user's password.
     */
    public void signInWithEmailAndPassword(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful())
            {
                //directly bring user to UserProfileActivity
                startActivity(new Intent(this, UserProfileActivity.class));
            }
            else
            {
                Log.w("SIGN IN", "signInWithEmail:failure", task.getException());
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method takes in the user's email address and password entered and
     * creates their account using Firebase email and password authentication.
     *
     * @param email The user's email address.
     * @param password The user's password.
     */
    public void signUpWithEmailAndPassword(String email, String password)
    {
        //creates user
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

            }
        });

        //signs user in
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    //bring user to UserTypeActivity to fill out school roles and corresponding info
                    startActivity(new Intent(getBaseContext(), UserTypeActivity.class));
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Cannot sign in!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This method is the OnClick method of the signInButton. It takes in the user's email address
     * and password entered in the corresponding text boxes and calls the signInWithEmailAndPassword
     * method to sign the user in.
     *
     * @param v The context of the view in which signInButton is displayed in.
     */
    public void signIn(View v)
    {
        String emailString = emailField.getText().toString();
        String passwordString = passwordField.getText().toString();

        signInWithEmailAndPassword(emailString, passwordString);
    }

    /**
     * This method is the OnClick method of the signUpButton. It takes in the user's email address
     * and password entered in the corresponding text boxes and checks if the user's email is a CIS
     * email before calling the signUpWithEmailAndPassword method to create the user's Firebase
     * authentication account.
     *
     * @param v The context of the view in which signUpButton is displayed in.
     */
    public void signUp(View v)
    {
        String emailString = emailField.getText().toString();
        String passwordString = passwordField.getText().toString();

        //only allow cis email to sign up
        if(emailString.contains("cis") && emailString.contains("edu"))
        {
            signUpWithEmailAndPassword(emailString, passwordString);
        }
        else
        {
            Toast.makeText(this, "You are unable to sign up because you are not a CIS user", Toast.LENGTH_SHORT).show();
        }
    }
}
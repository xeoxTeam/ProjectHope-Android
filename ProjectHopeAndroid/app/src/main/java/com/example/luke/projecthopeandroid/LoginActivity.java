package com.example.luke.projecthopeandroid;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
//Login activity
public class LoginActivity extends AppCompatActivity {
    //Declaration and Initialisation
    private FirebaseAuth mAuth;                                                         //Firebase authenticator
    private FirebaseAuth.AuthStateListener mAuthListener;                               //Firebase authenticator Listener
    private ArrayList<String> errors;                                                   //Error array list
    private static final String TAG = "Storage#MainActivity";                           //Tag for log

    @Override
    protected void onCreate(Bundle savedInstanceState) {                                //On create method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences myPrefs = getSharedPreferences("PREFERENCE", MODE_PRIVATE);       //Getting shared preference for anonymous login
        String userID = myPrefs.getString("userID", "");                                    //Getting user id
        errors = new ArrayList<>();                                                         //Initialising error array
        if(!userID.equals("")){                                                             //Checking if first time login
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("userID", userID);
            startActivity(intent);                                                  //Login automatically
        }

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        getSupportActionBar().hide();                                                       //Hiding action bar
    }
    //Method to login
    public void loginClick(View view){
        //Declration and initailisation
        EditText emailEdit = (EditText)findViewById(R.id.emailEdit);                       //Email
        EditText passwordEdit = (EditText)findViewById(R.id.passwordEdit);                  //Password

        if(checkInputs()) {                                                                 //Verifying inputs
            String email = emailEdit.getText().toString();                                  //Fetching email and password
            String password = passwordEdit.getText().toString();
            Toast.makeText(LoginActivity.this, "Checking login details", Toast.LENGTH_SHORT).show();    //Notifying user
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {                                 //If login failed
                                Toast.makeText(LoginActivity.this, "Auth Failed",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                checkIfEmailVerified();                                //Checking if emailed has been verified
                            }
                        }
                    });
        }
        else {
            showDialog();
        }
    }

    public void registerClick(View view){                                       //Opening register page
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void lostClick(View view){                                          //Method for lost password
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final EditText emailEdit = (EditText)findViewById(R.id.emailEdit);
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Are you sure you want to send a lost password email?")
                .setTitle("Warning!");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {                       //Sending lost password email on ok button
                if(!emailEdit.getText().toString().equals("")) {
                    String emailAddress = emailEdit.getText().toString();

                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(LoginActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkIfEmailVerified()                         //Method for checking if email is verified
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();  //Firebase user

        if (user.isEmailVerified())                                         //Checking if email is verified
        {
            // user is verified, so you can finish this activity or send user to activity which you want.

            Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("userID", user.getUid());
            //SharedPreferences myPrefs = getPreferences(MODE_PRIVATE);   //Using shared preferences to save the game
            SharedPreferences myPrefs = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = myPrefs.edit();
            prefsEditor.putString("userID", user.getUid());                  //Saving chronometer time
            prefsEditor.commit();
            startActivity(intent);
        }
        else
        {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            FirebaseAuth.getInstance().signOut();

            //restart this activity
            Toast.makeText(LoginActivity.this, "Failed logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public void showDialog(){                       //Message dialog for displaying error messages
        //Declaration and Initialisation
        String message = "";                        //Message

        for (int i = 0; i < errors.size(); i++) {   //Building message body
            message += errors.get(i) + "\n";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this); //Instantiate an AlertDialog.Builder with its constructor

        builder.setMessage(message)
                .setTitle("Error!");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean checkInputs(){           //Method that validates all inputs
        //Declaration and initialisation
        EditText emailEdit = (EditText)findViewById(R.id.emailEdit);
        EditText passwordEdit = (EditText)findViewById(R.id.passwordEdit);
        boolean passed = true;

        if(errors.size() > 0){              //Empty error array
            errors.clear();
        }

        if(emailEdit.getText().toString().isEmpty()){        //Check name
            errors.add("Please input an email");
            passed = false;
        }

        if(passwordEdit.getText().toString().isEmpty()){    //Check strength value
            errors.add("Please input a password");
            passed = false;
        }

        return passed;
    }
}

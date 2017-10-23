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

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ArrayList<String> errors;                                                   //Error array list

    private static final String TAG = "Storage#MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences myPrefs = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String userID = myPrefs.getString("userID", "");
        errors = new ArrayList<>();
        if(!userID.equals("")){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
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
        getSupportActionBar().hide();
    }

    public void loginClick(View view){
        EditText emailEdit = (EditText)findViewById(R.id.emailEdit);
        EditText passwordEdit = (EditText)findViewById(R.id.passwordEdit);
        if(checkInputs()) {
            String email = emailEdit.getText().toString();
            String password = passwordEdit.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Auth Failed",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                checkIfEmailVerified();
                            }
                        }
                    });
        }
        else {
            showDialog();
        }
    }

    public void registerClick(View view){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void lostClick(View view){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        EditText emailEdit = (EditText)findViewById(R.id.emailEdit);
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

    private void checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
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

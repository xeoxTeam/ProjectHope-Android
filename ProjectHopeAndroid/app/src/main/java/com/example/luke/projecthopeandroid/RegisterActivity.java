package com.example.luke.projecthopeandroid;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "Storage#MainActivity";
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(); //Firebase reference
    private ArrayList<String> errors;                                                   //Error array list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        errors = new ArrayList<>();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //sendVerificationEmail();
                    Toast.makeText(RegisterActivity.this, "Sign in",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(RegisterActivity.this, "Sign out",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        getSupportActionBar().hide();
    }

    public void register(View view){
        EditText emailEdit = (EditText)findViewById(R.id.emailEdit);
        EditText passwordEdit = (EditText)findViewById(R.id.passwordEdit);
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        if(checkInputs()) {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Failed To Register User, Please try again.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Please check email to verify account", Toast.LENGTH_SHORT).show();
                                sendVerificationEmail();
                            }
                        }
                    });
        }
        else{
            showDialog();
        }

    }

    private void sendVerificationEmail()
    {
        final EditText nameEdit = (EditText)findViewById(R.id.nameEdit);
        final EditText surnameEdit = (EditText)findViewById(R.id.surnameEdit);
        final EditText emailEdit = (EditText)findViewById(R.id.emailEdit);
        final EditText cellEdit = (EditText)findViewById(R.id.cellEdit);
        //final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent
                            mRootRef.child("User").child(user.getUid()).child("Name").setValue(nameEdit.getText().toString());
                            mRootRef.child("User").child(user.getUid()).child("Surname").setValue(surnameEdit.getText().toString());
                            mRootRef.child("User").child(user.getUid()).child("Credits").setValue(0);
                            mRootRef.child("User").child(user.getUid()).child("Email").setValue(emailEdit.getText().toString());
                            mRootRef.child("User").child(user.getUid()).child("Cell").setValue(cellEdit.getText().toString());
                            Toast.makeText(RegisterActivity.this, "Verification email sent",
                                    Toast.LENGTH_SHORT).show();

                            // after email is sent just logout the user and finish this activity
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
        finish();
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
        EditText nameEdit = (EditText)findViewById(R.id.nameEdit);
        EditText surnameEdit = (EditText)findViewById(R.id.surnameEdit);
        EditText emailEdit = (EditText)findViewById(R.id.emailEdit);
        EditText cellEdit = (EditText)findViewById(R.id.cellEdit);
        boolean passed = true;

        if(errors.size() > 0){              //Empty error array
            errors.clear();
        }

        if(nameEdit.getText().toString().isEmpty()){        //Check name
            errors.add("Please input a name");
            passed = false;
        }

        if(surnameEdit.getText().toString().isEmpty()){    //Check strength value
            errors.add("Please input a surname");
            passed = false;
        }

        if(emailEdit.getText().toString().isEmpty()){        //Check gold value
            errors.add("Please input an email");
            passed = false;
        }

        if(cellEdit.getText().toString().isEmpty()){        //Check silver value
            errors.add("Please input a cell phone number");
            passed = false;
        }

        return passed;
    }
}

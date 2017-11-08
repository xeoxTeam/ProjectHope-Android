package com.example.luke.projecthopeandroid;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
//Activity for updating user profile
public class UpdateProfActivity extends AppCompatActivity {
    //Declaration and Initialisation
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(); //Firebase reference
    private ArrayList<String> errors;                                                   //Error array list
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {        //On create method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        userID = getIntent().getStringExtra("userID");          //Fetching user id
        getUser();                                              //Fetching user details
        errors = new ArrayList<>();                             //Initialising error array
        getSupportActionBar().hide();                           //Hiding support bar
    }

    public void getUser(){                                      //Method for retrieving user details from firebase
        DatabaseReference mConditionRef = mRootRef.child("User").child(userID);
        mConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> list = dataSnapshot.getChildren();
                ArrayList<DataSnapshot> list1 = new ArrayList<DataSnapshot>();
                EditText nameEdit = (EditText)findViewById(R.id.nameEdit);
                EditText surnameEdit = (EditText)findViewById(R.id.surnameEdit);
                EditText cellEdit = (EditText)findViewById(R.id.cellEdit);
                for(DataSnapshot ds:list){
                    list1.add(ds);
                }

                nameEdit.setText(list1.get(3).getValue().toString());
                surnameEdit.setText(list1.get(4).getValue().toString());
                cellEdit.setText(list1.get(0).getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void register(View view){                            //Method to save user details
        EditText nameEdit = (EditText)findViewById(R.id.nameEdit);
        EditText surnameEdit = (EditText)findViewById(R.id.surnameEdit);
        EditText cellEdit = (EditText)findViewById(R.id.cellEdit);

        if(checkInputs()) {
            mRootRef.child("User").child(userID).child("Cell").setValue(cellEdit.getText().toString());
            mRootRef.child("User").child(userID).child("Name").setValue(nameEdit.getText().toString());
            mRootRef.child("User").child(userID).child("Surname").setValue(surnameEdit.getText().toString());

            Toast.makeText(UpdateProfActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            showDialog();
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
        EditText nameEdit = (EditText)findViewById(R.id.nameEdit);
        EditText surnameEdit = (EditText)findViewById(R.id.surnameEdit);
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

        if(cellEdit.getText().toString().isEmpty()){        //Check silver value
            errors.add("Please input a cell phone number");
            passed = false;
        }

        return passed;
    }
}

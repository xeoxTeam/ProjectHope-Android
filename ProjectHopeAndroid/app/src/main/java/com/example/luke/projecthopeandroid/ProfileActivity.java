package com.example.luke.projecthopeandroid;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(); //Firebase reference
    private String beneCredits;
    private static final int bed = 3;
    private static final int hotShave = 3;
    private static final int food = 3;
    private static final int hotShower = 3;
    private TextView bedText;
    private TextView shaveText;
    private TextView foodText;
    private TextView showerText;
    private String uid;
    private String userCredits;
    private String beneID;
    private String name;
    private String uniqueKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String age = getIntent().getStringExtra("age");      //Get shop list name
        String bio = getIntent().getStringExtra("bio");      //Get shop list name
        beneCredits = getIntent().getStringExtra("credits");      //Get shop list name
        name = getIntent().getStringExtra("name");      //Get shop list name
        uid = getIntent().getStringExtra("uid");      //Get shop list name
        beneID = getIntent().getStringExtra("beneID");
        uniqueKey = getIntent().getStringExtra("uniqueKey");
        userCredits = "Loading";
        getUser();
        loadPic();
        TextView nameEdit = (TextView)findViewById(R.id.nameText);
        TextView ageEdit = (TextView)findViewById(R.id.ageText);
        TextView bioEdit = (TextView)findViewById(R.id.bioText);
        TextView availableCredText = (TextView)findViewById(R.id.available);
        availableCredText.setText("Available credits for donation: " + userCredits);

        nameEdit.setText(name);
        ageEdit.setText(age);
        bioEdit.setText(bio);

        bedText = (TextView)findViewById(R.id.bedText);
        shaveText = (TextView)findViewById(R.id.shaveText);
        foodText = (TextView)findViewById(R.id.foodText);
        showerText = (TextView)findViewById(R.id.showerText);

        bedText.setText(bedText.getText() + (0 + ""));
        shaveText.setText(shaveText.getText() + (0 + ""));
        foodText.setText(foodText.getText() + (0 + ""));
        showerText.setText(showerText.getText() + (0 + ""));

        Date currentTime = Calendar.getInstance().getTime();
        SearchBenefactor temp = new SearchBenefactor(name, currentTime.toString(), 0, beneID);
        mRootRef.child("Search").child(uid).child(beneID).setValue(temp);

        final EditText creditsEdit = (EditText)findViewById(R.id.creditsEdit);

        creditsEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateRates();
/*                if((!creditsEdit.getText().toString().equals("")) && Integer.parseInt(userCredits) > Integer.parseInt(creditsEdit.getText().toString())) {
                    updateRates();
                }
                else {
                    displayError();
                    creditsEdit.setText("");
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void loadPic(){
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference islandRef = mStorageRef.child("Benefactor/" + beneID + ".jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
                ImageView profPic = (ImageView) findViewById(R.id.profileImage);
                profPic.setImageBitmap(bitmap);
                // Data for "images/island.jpg" is returns, use this as needed
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public void displayError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Error");
        builder.setMessage("Not enough credits");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    public void updateRates(){
        EditText creditEdit = (EditText)findViewById(R.id.creditsEdit);
        int credits;
        if(creditEdit.getText().toString().equals("")){
            credits = 0;
        }
        else {

            credits = Integer.parseInt(creditEdit.getText().toString());
        }
        bedText.setText("Bed  = " + (credits / bed  + ""));
        shaveText.setText("Hot Shave  = " + (credits / hotShave + ""));
        foodText.setText("Food  = " + (credits / food + ""));
        showerText.setText("Hot Shower  = " + (credits / hotShower + ""));
    }

    public void getUser(){
        DatabaseReference mConditionRef = mRootRef.child("User").child(uid);
        mConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> list = dataSnapshot.getChildren();
                ArrayList<DataSnapshot> list1 = new ArrayList<DataSnapshot>();

                for(DataSnapshot ds:list){
                    list1.add(ds);
                }

                userCredits = list1.get(1).getValue().toString();
                TextView availableCredText = (TextView)findViewById(R.id.available);
                availableCredText.setText("Available credits for donation: "+ userCredits);
/*                for(DataSnapshot ds : list){
                    ds.getValue()
*//*                    ShoppingList tempList = ds.getValue(ShoppingList.class);
                    index = tempShopList.size();
                    tempShopList.add(tempList);
                    ((ShoppingListViewAdapter) mAdapter).addItem(tempList, index);*//*
                }*/
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void donate(View view){
        EditText creditEdit = (EditText)findViewById(R.id.creditsEdit);
        if(!creditEdit.getText().toString().equals("") && (Integer.parseInt(creditEdit.getText().toString()) <= Integer.parseInt(userCredits))){
            Date currentTime = Calendar.getInstance().getTime();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String key = database.getReference("Donation").push().getKey();
            mRootRef.child("Donation").child(key).child("UserID").setValue(uid);
            mRootRef.child("Donation").child(key).child("UserBenefactorID").setValue(beneID);
            mRootRef.child("Donation").child(key).child("Credits").setValue(Integer.parseInt(creditEdit.getText().toString()));
            mRootRef.child("Donation").child(key).child("Date").setValue(currentTime.toString());

            mRootRef.child("User").child(uid).child("Credits").setValue(Integer.parseInt(userCredits) - Integer.parseInt(creditEdit.getText().toString()));
            mRootRef.child("SearchBenefactor").child(beneID).child(uniqueKey).child("Credits").setValue(Integer.parseInt(beneCredits) + Integer.parseInt(creditEdit.getText().toString()));

            SearchBenefactor temp = new SearchBenefactor(name, currentTime.toString(), Integer.parseInt(creditEdit.getText().toString()), beneID);
            mRootRef.child("Search").child(uid).child(beneID).setValue(temp);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Thank you!");
            builder.setMessage("You have successfully donated " + creditEdit.getText().toString() + " credits");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            AlertDialog alert1 = builder.create();
            alert1.show();
        }
        else{
            displayError();
        }
    }

    public void rates(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rates");
        builder.setMessage("Bed = 3\nHot shave = 1\nFood = 2\nHot shower = 1");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        AlertDialog alert1 = builder.create();
        alert1.show();
    }
}

package com.example.luke.projecthopeandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddBenefactorActivity extends AppCompatActivity {
    private String uid;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(); //Firebase reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_benefactor);
        uid = getIntent().getStringExtra("userID");
        getSupportActionBar().setTitle("Find a benefactor");
    }

    public void searchClick(View view){
        //myRef =  database.getReference("Reminders/"+mAuth.getCurrentUser().getUid());
        //DatabaseReference mConditionRef = mRootRef.child("Benfactor").child(uid);
        DatabaseReference mConditionRef = mRootRef.child("Benefactor");//.child(benefactorID);
        EditText nameText = (EditText)findViewById(R.id.nameEdit);

        String queryId = nameText.getText().toString();

        Query query = mConditionRef.orderByChild("FirstName").equalTo(queryId);    //Query that will look for a Record that has the same Movie ID.

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    Toast.makeText(AddBenefactorActivity.this, "Benefactor not found!", Toast.LENGTH_SHORT).show();
                }
                else {
                    ArrayList<String> benefactor = new ArrayList<String>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        benefactor.add(ds.child("Age").getValue().toString());
                        benefactor.add(ds.child("Bio").getValue().toString());
                        benefactor.add(ds.child("Credits").getValue().toString());
                        benefactor.add(ds.child("FirstName").getValue().toString());
                        benefactor.add(ds.child("LastName").getValue().toString());
                        benefactor.add(ds.child("uniqueKey").getValue().toString());
                    }
                    Intent intent = new Intent(AddBenefactorActivity.this, ProfileActivity.class);
                    intent.putExtra("age", benefactor.get(0));
                    intent.putExtra("bio", benefactor.get(1));
                    intent.putExtra("credits", benefactor.get(2));
                    intent.putExtra("name", benefactor.get(3) + " " + benefactor.get(4));
                    intent.putExtra("uid", uid);
                    intent.putExtra("beneID", benefactor.get(5).substring(4, benefactor.get(5).length()));
                    intent.putExtra("uniqueKey", benefactor.get(5).substring(4, benefactor.get(5).length()));
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

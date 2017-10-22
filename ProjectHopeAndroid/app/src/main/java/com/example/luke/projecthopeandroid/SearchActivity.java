package com.example.luke.projecthopeandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    private ArrayList<SearchBenefactor> searchBenefactors;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(); //Firebase reference
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchBenefactors = new ArrayList<>();
        uid = getIntent().getStringExtra("uid");
        getBenefactors();
/*        SearchBenefactor temp = new SearchBenefactor("Sizwe Lopo", "29 January 2010", 40, "5rtyguhi");
        searchBenefactors.add(temp);*/

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SearchAdapter(searchBenefactors);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void getBenefactors(){
        final ArrayList<SearchBenefactor> tempSearchBenefactors = new ArrayList<>();
        DatabaseReference mConditionRef = mRootRef.child("Search").child(uid);
        mConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int index;
                Iterable<DataSnapshot> list = dataSnapshot.getChildren();
                for(DataSnapshot ds : list){
                    SearchBenefactor temp = ds.getValue(SearchBenefactor.class);
                    index = tempSearchBenefactors.size();
                    tempSearchBenefactors.add(temp);
                    ((SearchAdapter) mAdapter).addItem(temp, index);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((SearchAdapter) mAdapter).setOnItemClickListener(new SearchAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //Log.i(LOG_TAG, " Clicked on Item " + position);

                fetchBenefactor(searchBenefactors.get(position).getBeneID());
            }
        });
    }

    public void fetchBenefactor(final String benefactorID){
        DatabaseReference mConditionRef = mRootRef.child("Benefactor").child(benefactorID);
        mConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> list = dataSnapshot.getChildren();
                String age = "";
                String bio = "";
                String credits = "";
                String name = "";
                String key = "";
                for(DataSnapshot ds: list){
                    age = ds.child("Age").getValue().toString();
                    bio = ds.child("Bio").getValue().toString();
                    credits = ds.child("Credits").getValue().toString();
                    name = ds.child("FirstName").getValue().toString() + ds.child("LastName").getValue().toString();
                    key = ds.getKey();
                }



/*                for(DataSnapshot ds:list){
                    list1.add(ds);
                }*/

                Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                intent.putExtra("age", age);
                intent.putExtra("bio", bio);
                intent.putExtra("credits", credits);
                intent.putExtra("name", name);
                intent.putExtra("uid", uid);
                intent.putExtra("beneID", benefactorID);
                intent.putExtra("uniqueKey", key);
                //intent.putExtra("shopListName", shoppingLists.get(position).getName());

                startActivity(intent);

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

}

package com.example.luke.projecthopeandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<SearchBenefactor> searchBenefactors;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(); //Firebase reference
    private String uid;
    private MaterialSearchView searchView;                                              //Search view
    private ArrayList<SearchBenefactor> searchList;                    //Filtered array list when searching

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchBenefactors = new ArrayList<>();
        uid = getIntent().getStringExtra("uid");
        getBenefactors();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SearchAdapter(searchBenefactors);
        mRecyclerView.setAdapter(mAdapter);

        searchView = (MaterialSearchView)findViewById(R.id.search_view);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() { //Search view listener
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {                                          //Reset card view when search view is closed
                repopulateCardView("Normal");
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {    //Seach view query listener (When typing into the search view)
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {                              //Searching as you type (Searches through anime name
                if(newText != null && !newText.isEmpty()){                                  //If the search text is not null or empty
                    searchList = new ArrayList<SearchBenefactor>();                                    //Search array list
                    for(int i = 0; i < searchBenefactors.size(); i++){               //Looping through every anime object in the array list
                        if(searchBenefactors.get(i).getFullName().contains(newText)){    //If the anime name contains the word of search text, add it to the search array list
                            searchList.add(searchBenefactors.get(i));
                        }
                    }
                    repopulateCardView("Search");                                           //Repopulate list with search array list
                }
                else{
                    repopulateCardView("Normal");                                           //If there is no text in the search view then reset view
                }
                return true;
            }

        });
    }

    //Method that repopulates the card view. The passed the parameter determines which array list must be read.
    //Search means use the search array list. Normal means use the standard array list
    public void repopulateCardView(String type){
        if(!type.equals("Search")) {                    //Dont use current array list if search
            getBenefactors();
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);             //Recycle view
        mRecyclerView.setHasFixedSize(true);                                            //Setting the recycle view to a fixed size
        mLayoutManager = new LinearLayoutManager(this);                                 //Instantiating a layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);                                 //Assigning the layour manager to the recycle view
        if(!type.equals("Search")) {
            mAdapter = new SearchAdapter(searchBenefactors);   //Use current array list
        }
        else {
            mAdapter = new SearchAdapter(searchList);                 //Use search array list
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    public void getBenefactors(){
        final ArrayList<SearchBenefactor> tempSearchBenefactors = new ArrayList<>();
        if(searchBenefactors.size() > 0){
            searchBenefactors = new ArrayList<>();
        }
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
                    searchBenefactors = tempSearchBenefactors;
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
        searchView.closeSearch();                           //Closing search
        ((SearchAdapter) mAdapter).setOnItemClickListener(new SearchAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
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

                Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                intent.putExtra("age", age);
                intent.putExtra("bio", bio);
                intent.putExtra("credits", credits);
                intent.putExtra("name", name);
                intent.putExtra("uid", uid);
                intent.putExtra("beneID", benefactorID);
                intent.putExtra("uniqueKey", key);

                startActivity(intent);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

}

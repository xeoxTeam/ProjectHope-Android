package com.example.luke.projecthopeandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ZXingScannerView.ResultHandler{
    private ZXingScannerView mScannerView;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(); //Firebase reference
    private String userCredits;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences myPrefs = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String firstTime = myPrefs.getString("firstTime", "");
        if(firstTime.equals("")){
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        uid = getIntent().getStringExtra("userID");      //Get shop list name
        getSupportActionBar().setTitle("");
    }

    public void getUser(final String credits, final String key, final String voucherID){
        DatabaseReference mConditionRef = mRootRef.child("User").child(uid);
        mConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> list = dataSnapshot.getChildren();
                ArrayList<DataSnapshot> list1 = new ArrayList<DataSnapshot>();

                for(DataSnapshot ds:list){
                    list1.add(ds);
                }

                String userCredits = list1.get(1).getValue().toString();
                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(); //Firebase reference
                mRootRef.child("Voucher").child(voucherID).child(key).child("Credits").setValue(0);
                mRootRef.child("User").child(uid).child("Credits").setValue(Integer.parseInt(userCredits) + Integer.parseInt(credits));

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Voucher Loaded!");
                builder.setMessage("You have successfully loaded " + credits + " credits into your profile.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mScannerView.startCamera();
                        mScannerView.resumeCameraPreview(MainActivity.this);
                    }
                });
                android.support.v7.app.AlertDialog alert1 = builder.create();
                alert1.show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void startScan(View view){
        QrScanner();
    }

    public void QrScanner(){


        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera

    }

    @Override
    public void onPause() {
        super.onPause();
        if(mScannerView != null) {
            mScannerView.stopCamera();           // Stop camera on pause
        }
    }

    public void onResume(){
        super.onResume();
        if(mScannerView != null){
            mScannerView.startCamera();
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)



        if(rawResult.getText().startsWith("hope")){
            mScannerView.stopCamera();
            String benefactorID = rawResult.getText().substring(4);
            fetchBenefactor(benefactorID);
        }
        else if(rawResult.getText().startsWith("voucher")){
            mScannerView.stopCamera();
            String voucherID = rawResult.getText().substring(7);
            fetchVoucher(voucherID);
        }

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }

    public void fetchVoucher(final String voucherID){
        DatabaseReference mConditionRef = mRootRef.child("Voucher").child(voucherID);
        mConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> list = dataSnapshot.getChildren();
                String credits = "";
                String key = "";
                for(DataSnapshot ds: list){
                    credits = ds.child("Credits").getValue().toString();
                    key = ds.getKey();
                }
                if(credits.equals("") || credits.equals("0")){
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("This voucher has expired!");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mScannerView.startCamera();
                            mScannerView.resumeCameraPreview(MainActivity.this);
                        }
                    });
                    android.support.v7.app.AlertDialog alert1 = builder.create();
                    alert1.show();
                }
                else{
                    getUser(credits, key, voucherID);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

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

                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawerz = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawerz != null) {
            if (drawerz.isDrawerOpen(GravityCompat.START)) {
                drawerz.closeDrawer(GravityCompat.START);
            } else {
                //super.onBackPressed();
            }
        }
        else if(mScannerView != null){
            mScannerView.stopCamera();
            finish();
            Intent settingsIntent = new Intent(MainActivity.this, MainActivity.class);
            settingsIntent.putExtra("userID", uid);
            startActivity(settingsIntent);
/*            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);*/
        }
        else {
            //super.onBackPressed();
        }
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            SharedPreferences myPrefs = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
            //SharedPreferences myPrefs = getPreferences("PREFERENCE", MODE_PRIVATE);   //Using shared preferences to save the game
            SharedPreferences.Editor prefsEditor = myPrefs.edit();
            prefsEditor.clear();
            prefsEditor.commit();
            finish();
        }
        else if(id == R.id.nav_edit){
            Intent intent = new Intent(MainActivity.this, UpdateProfActivity.class);
            intent.putExtra("userID", uid);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

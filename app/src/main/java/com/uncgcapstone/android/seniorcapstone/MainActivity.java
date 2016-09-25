package com.uncgcapstone.android.seniorcapstone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog.*;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.squareup.leakcanary.LeakCanary;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mabbas007.tagsedittext.TagsEditText;

public class MainActivity extends CoreActivity {

    private final String TAG = "MainActivity";

    public FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseUser user;
    Toolbar toolbar;
    PrimaryDrawerItem homeItem;
    SecondaryDrawerItem item1, item2, item3, logOutItem, settingsItem;
    AccountHeader headerResult;
    Drawer result;
    SharedPreferences mSharedPreferences;
    String emailString = "";
    FirebaseDatabase database;
    DatabaseReference myRef, countRef;
    FragmentManager fm;
    FirebaseStorage storage;
    String downloadUrl = null;
    Menu menu;
    JSONParser jsonParser = new JSONParser();
    public String search = "";
    OnProgressListener mOnProgressListener;
    OnFailureListener mOnFailureListener;
    OnPausedListener mOnPausedListener;
    OnSuccessListener mOnSuccessListener;


    // url to create new product
    private String url_create_product = "http://3661590e.ngrok.io/android_connect/create_recipe.php";

    // JSON Node names
    private final String TAG_SUCCESS = "success";







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef  = database.getReference("post");
        countRef = database.getReference("count");

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        emailString = mSharedPreferences.getString("email", "");


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        homeItem = new PrimaryDrawerItem().withIdentifier(0).withName("Home").withSelectable(false).withIcon(R.drawable.home);
        item1 = new SecondaryDrawerItem().withIdentifier(1).withName("My Recipes").withSelectable(false).withBadge("19").withIcon(R.drawable.book);
        item2 = new SecondaryDrawerItem().withIdentifier(2).withName("Pantry").withSelectable(false).withIcon(R.drawable.pantry);
        item3 = new SecondaryDrawerItem().withIdentifier(3).withName("Meal Schedule").withSelectable(false).withIcon(R.drawable.calendar1);
        logOutItem = new SecondaryDrawerItem().withIdentifier(4).withName("Log Out").withSelectable(false).withIcon(R.drawable.logout);
        settingsItem = new SecondaryDrawerItem().withIdentifier(5).withName("Settings").withSelectable(false).withIcon(R.drawable.settings);

        mAuth = FirebaseAuth.getInstance(); //Gets shared instance of firebase auth object
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };
        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.background_gradient_vertical)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        new ProfileDrawerItem().withEmail(emailString)
                        .withIcon(getResources().getDrawable(R.drawable.usericon_small))
                                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withToolbar(toolbar)
                .addDrawerItems(
                        homeItem,
                        new DividerDrawerItem(),
                        item1,
                        item2,
                        item3,
                        new DividerDrawerItem(),
                        settingsItem,
                        logOutItem
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if(drawerItem.getIdentifier() == 0){
                            Fragment fragment = MainFragment.newInstance();
                            fm = getSupportFragmentManager();
                            fm.beginTransaction()
                                    .replace(R.id.fragment_container, fragment, "AddRecipeFragment")
                                    .addToBackStack(null)
                                    .commit();
                            return false;
                        }
                        else if(drawerItem.getIdentifier() == 1){
                            return false;
                        }
                        else if(drawerItem.getIdentifier() == 2){
                            return false;
                        }
                        else if(drawerItem.getIdentifier() == 3){
                            ScheduleFragment fragment = ScheduleFragment.newInstance();
                            fm = getSupportFragmentManager();
                            fm.beginTransaction()
                                    .replace(R.id.fragment_container, fragment, "ScheduleFragment")
                                    .addToBackStack(null)
                                    .commit();
                            toolbar.setTitle("Schedule");
                            return false;
                        }
                        else if(drawerItem.getIdentifier() == 4){
                            FirebaseAuth.getInstance().signOut();
                            Intent i = new Intent(MainActivity.this, LogInActivity.class);
                            startActivity(i);
                            finish();
                        }
                        return true;
                    }
                })
                .withAccountHeader(headerResult).withSelectedItem(-1).
                        build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        fm = getSupportFragmentManager();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = MainFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, "MainFragment").commit();

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onDestroy(){
super.onDestroy();
    }
 //Inflate spinner on toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //this.menu = menu;
       //getMenuInflater().inflate(R.menu.menu_main, menu);     // ADD REFRESH BUTTON
        // Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        // Create an ArrayAdapter using the string array and a default spinner layout
       // ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
               // R.array.spinnerstrings, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        //spinner.setAdapter(adapter); // set the adapter to provide layout of rows and content
        //spinner.setOnItemSelectedListener(this); // set the listener, to perform actions based on item selection
        return true;
    }

    //@Override
    //public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

            //return super.onOptionsItemSelected(item);

    //}

    public FirebaseUser getUser(){
        return user;
    }

    public void post(final String servestext, final String preptext, final String cooktext, final String datetime, final Uri photoUri, final String uid, final String username, final String recipeName){
        final Uri picUri = photoUri;
        new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            Random random = new Random();
                            int rand = random.nextInt(200000);
                            StorageReference storageRef = storage.getReferenceFromUrl("gs://seniorcapstone-831a0.appspot.com");
                            StorageReference imagesRef = storageRef.child("images/" +  String.valueOf(rand));
                            UploadTask uploadTask = imagesRef.putFile(picUri);
                           StorageTask<UploadTask.TaskSnapshot> prog = uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    showProgressDialog();
                                    //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    //System.out.println("Upload is " + progress + "% done");
                                    //int currentprogress = (int) progress;
                                    //progressBar.setProgress(currentprogress);
                                }
                            });
                            StorageTask<UploadTask.TaskSnapshot> paus = uploadTask.addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                    hideProgressDialog();
                                    //System.out.println("Upload is paused");
                                }
                            });

                            StorageTask<UploadTask.TaskSnapshot> fail = uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    hideProgressDialog();
                                    // Handle unsuccessful uploads
                                }
                            });

                            StorageTask<UploadTask.TaskSnapshot> succ = uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                    downloadUrl = taskSnapshot.getDownloadUrl().toString();
                                    Log.d(TAG, "Photo URL: " + downloadUrl);
                                    Log.d(TAG, uid.toString() + " " + username.toString() + " " + recipeName.toString() + " " + datetime.toString() + " " + preptext.toString() + " " + cooktext.toString() + " " + servestext.toString());
                                    new CreateNewRecipe().execute(uid, username, recipeName, downloadUrl, datetime, preptext, cooktext, servestext);
                                    hideProgressDialog();

                                    toolbar.setTitle("Add a Recipe");
                                    //fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    //fm.beginTransaction().remove(fm.findFragmentById(R.id.fragment_container)).commit();
                                    Fragment fragment = MainFragment.newInstance();
                                    fm.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

                                    //getSupportFragmentManager().beginTransaction()
                                            //.replace(R.id.fragment_container, fragment, "MainFragment")
                                           // .addToBackStack(null).commit();
                                }
                            });
                                uploadTask.removeOnProgressListener((OnProgressListener) prog).removeOnFailureListener((OnFailureListener) fail).removeOnPausedListener((OnPausedListener) paus).removeOnSuccessListener((OnSuccessListener) succ);
                        } catch (Exception e) {

                        }
                    }
                });
            }
        }).start();
    }

    class CreateNewRecipe extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //((MainActivity)getActivity()).showProgressDialog();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String uid = args[0];
            String username = args[1];
            String recipeName = args[2];
            String url = args[3];
            String datetime = args[4];
            String preptime = args[5];
            String cooktime = args[6];
            String serves = args[7];
            Log.d(TAG, uid.toString() + " " + username.toString() + " " + recipeName.toString() + " " + datetime.toString() + " " + preptime.toString() + " " + cooktime.toString() + " " + serves.toString());


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", uid));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("recipename", recipeName));
            params.add(new BasicNameValuePair("url", url));

            params.add(new BasicNameValuePair("datetime", datetime));
            params.add(new BasicNameValuePair("preptime", preptime));
            params.add(new BasicNameValuePair("cooktime", cooktime));
            params.add(new BasicNameValuePair("serves", serves));



            // getting JSON Object
            // Note that create product url accepts POST method

            JSONObject json = jsonParser.makeHttpRequest(url_create_product,
                    "POST", params);


            // check log cat fro response
            //Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Log.d(TAG, "Success!");

                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            //((MainActivity)getActivity()).hideProgressDialog();
        }

    }
    /**
     *
     * Set options for Spinner on toolbar
     * implements AdapterView.OnItemSelectedListener
     */
/*
    public void onItemSelected(AdapterView<?> parent, View view,
                              int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        ((TextView)view).setText(null);
        if(pos == 0){
            mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt("query", 0);
            editor.commit();

            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container)).commit();
            Fragment fragment = MainFragment.newInstance();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, "MainFragment")
                    .addToBackStack(null).commit();

        }
        else if(pos == 1){
            mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt("query", 1);
            editor.commit();

            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container)).commit();
            Fragment fragment = MainFragment.newInstance();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, "MainFragment")
                    .addToBackStack(null).commit();

        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
*/
    public void setToolbar(String name){
        toolbar.setTitle(name);
    }
/*
    public void showOverflowMenu(boolean showMenu){
        if(menu == null)
            return;
        menu.setGroupVisible(R.id.main_menu_group, showMenu);

    }*/
    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

}

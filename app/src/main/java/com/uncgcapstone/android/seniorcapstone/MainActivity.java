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
import com.google.gson.JsonArray;
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


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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
    String downloadUrl1 = null;
    Menu menu;
    JSONParser jsonParser = new JSONParser();
    public String search = "";
    OnProgressListener mOnProgressListener;
    OnFailureListener mOnFailureListener;
    OnPausedListener mOnPausedListener;
    OnSuccessListener mOnSuccessListener;



    // url to create new product
    private String url_create_product = "http://63d42096.ngrok.io/android_connect/create_recipe.php";

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
        item1 = new SecondaryDrawerItem().withIdentifier(1).withName("My Favorites").withSelectable(false).withIcon(R.drawable.fave_star);
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
                            final SharedPreferences.Editor editor = mSharedPreferences.edit();
                            if(!(mSharedPreferences.contains("search"))) {
                                editor.putString("search", "");
                                editor.commit();
                            }
                            else{
                                editor.putString("search", "");
                                editor.commit();
                            }
                            Fragment fragment = MainFragment.newInstance();
                            fm = getSupportFragmentManager();
                            fm.beginTransaction()
                                    .replace(R.id.fragment_container, fragment, "MainFragment")
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
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, "MainFragmentInitial").commit();

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
        Log.d("OnStop Called", "In Main Activity");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("OnPauseCalled", "In Main Activity");
    }

    @Override
    public void onDestroy(){
super.onDestroy();
    }
 //Inflate spinner on toolbar

    public FirebaseUser getUser(){
        return user;
    }

    public void post(final String[] servestext, final String[] preptext, final String[] cooktext, final String[] datetime, final String[] photoUri, final String[] uid, final String[] username, final String[] recipeName, final String[] tagsfinal, final String[] ingredients, final String[] ingredients2, final String[] ingredients3, final String[] steps, final String[] ingredtags){
        final Uri picUri = Uri.parse(photoUri[0]);
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
                                    downloadUrl1 = taskSnapshot.getDownloadUrl().toString();
                                    String[] downloadUrl = {downloadUrl1};
                                    Log.d(TAG, "Photo URL: " + downloadUrl);
                                    Log.d(TAG, uid.toString() + " " + username.toString() + " " + recipeName.toString() + " " + datetime.toString() + " " + preptext.toString() + " " + cooktext.toString() + " " + servestext.toString());
                                    new CreateNewRecipe().execute(uid, username, recipeName, downloadUrl, datetime, preptext, cooktext, servestext, tagsfinal, ingredients, ingredients2, ingredients3, steps, ingredtags);
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
                            rand = 0;
                            storageRef = null;
                            imagesRef = null;
                            uploadTask = null;
                            downloadUrl1 = null;
                        } catch (Exception e) {

                        }
                    }
                });
            }
        }).start();
    }

    class CreateNewRecipe extends AsyncTask<String[], String[], String> {

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
        protected String doInBackground(String[]... args) {
            String[] uid1 = args[0];
            String uid = uid1[0];
            String[] username1 = args[1];
            String username = username1[0];
            String[] recipeName1 = args[2];
            String recipename = recipeName1[0];
            String[] url1 = args[3];
            String url = url1[0];
            String[] datetime1 = args[4];
            String datetime = datetime1[0];
            String[] preptime1 = args[5];
            String preptime = preptime1[0];
            String[] cooktime1 = args[6];
            String cooktime = cooktime1[0];
            String[] serves1 = args[7];
            String serves = serves1[0];
            String[] tags = args[8];
            String[] ingredients = args[9];
            String[] ingredients2 = args[10];
            String[] ingredients3 = args[11];
            String[] steps = args[12];
            String[] ingredtags = args[13];



            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", uid));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("recipename", recipename));
            params.add(new BasicNameValuePair("url", url));
            params.add(new BasicNameValuePair("datetime", datetime));
            params.add(new BasicNameValuePair("preptime", preptime));
            params.add(new BasicNameValuePair("cooktime", cooktime));
            params.add(new BasicNameValuePair("serves", serves));
            for(int i = 0; i < tags.length; i++){
                params.add(new BasicNameValuePair("tags[]", tags[i]));
            }
            for(int i = 0; i < ingredients.length; i++){
                params.add(new BasicNameValuePair("ingredients[]", ingredients[i]));
            }
            for(int i = 0; i < ingredients2.length; i++){
                params.add(new BasicNameValuePair("ingredients2[]", ingredients2[i]));
            }
            for(int i = 0; i < ingredients3.length; i++){
                params.add(new BasicNameValuePair("ingredients3[]", ingredients3[i]));
            }
            for(int i = 0; i < steps.length; i++){
                params.add(new BasicNameValuePair("steps[]", steps[i]));
            }
            for(int i = 0; i < ingredtags.length; i++){
                params.add(new BasicNameValuePair("ingredtags[]", ingredtags[i]));
            }



            // getting JSON Object
            // Note that create product url accepts POST method

            JSONObject json = jsonParser.makeHttpRequest(url_create_product,
                    "POST", params);

                args = null;
                params = null;

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);     // ADD REFRESH BUTTON
        MenuItem item = menu.findItem(R.id.spinner);
        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        spinner.setSelection(0, false);
        spinner.setTag(R.id.pos, 0);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinnerstrings, R.layout.spinner_style);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter); // set the adapter to provide layout of rows and content
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(spinner.getTag(R.id.pos).toString().equals(String.valueOf(position)))) {
                    if (position == 0) {
                        mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("query", "0");
                        editor.commit();
                        spinner.setTag(R.id.pos, 0);



                        Fragment fragment1 = MainFragment.newInstance();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment1, "MainFragment")
                                .addToBackStack(null).commit();

                    } else if (position == 1) {
                        mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("query", "1");
                        editor.commit();
                        spinner.setTag(R.id.pos, 1);

                        Fragment fragment2 = MainFragment.newInstance();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment2, "MainFragment")
                                .addToBackStack(null).commit();
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }); // set the listener, to perform actions based on item selection
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    return super.onOptionsItemSelected(item);

    }


    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void setToolbar(String name){
        toolbar.setTitle(name);
    }

    public void showOverflowMenu(boolean showMenu){
        if(menu == null)
            return;
        menu.setGroupVisible(R.id.mainmenugroup, showMenu);

    }
    public String getUID(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
    }

    @Override
    public void onBackPressed(){
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        if(!(mSharedPreferences.contains("search"))) {
            editor.putString("search", "");
            editor.commit();
        }
        else{
            editor.putString("search", "");
            editor.commit();
        }
        super.onBackPressed();
    }

}

package com.uncgcapstone.android.seniorcapstone.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
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
import com.uncgcapstone.android.seniorcapstone.data.Url;
import com.uncgcapstone.android.seniorcapstone.data.User;
import com.uncgcapstone.android.seniorcapstone.io.ApiClient;
import com.uncgcapstone.android.seniorcapstone.io.ApiInterface;
import com.uncgcapstone.android.seniorcapstone.R;
import com.uncgcapstone.android.seniorcapstone.fragments.MainFragment;


import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
    public String search = "";
    OnProgressListener mOnProgressListener;
    OnFailureListener mOnFailureListener;
    OnPausedListener mOnPausedListener;
    OnSuccessListener mOnSuccessListener;
    final int CHOOSE_PIC = 1;
    String tempUrlSet = "";
    boolean urlGet, hasLoadedImage = false;
    List<User> mUser;
    Gson gson = new Gson();
    JSONArray jsonuser = null;
    String tempUrl = "";




    // url to create new product
    private String url_create_product = "http://63d42096.ngrok.io/android_connect/create_recipe.php";
    private String url_set_profile_image = "http://63d42096.ngrok.io/android_connect/set_profile_image.php";
    private String url_get_profile_image = "http://63d42096.ngrok.io/android_connect_retro/get_profile_image.php";

    // JSON Node names
    private final String TAG_SUCCESS = "success";
    IProfile profile;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUser = new ArrayList<>();

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef  = database.getReference("post");
        countRef = database.getReference("count");

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
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
                    if(hasLoadedImage == false) {
                        getProfileImage();
                        hasLoadedImage = true;
                    }

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
                .withCompactStyle(true)
                .addProfiles(
                        profile = new ProfileDrawerItem().withEmail(emailString)
                        .withIcon(getResources().getDrawable(R.drawable.person))
                                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        getIntent.setType("image/*");

                        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("image/*");

                        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                        startActivityForResult(chooserIntent, CHOOSE_PIC);
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
                            Intent i = new Intent(MainActivity.this, BasicActivity.class);
                            startActivity(i);
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

                                    Retrofit retrofit = ApiClient.getClient();
                                    ApiInterface apiService = retrofit.create(ApiInterface.class);


                                    List<String> tagzTemp = new ArrayList<String>(Arrays.asList(tagsfinal));
                                    ArrayList<String> tagz = new ArrayList<>(tagsfinal.length);
                                    tagz.addAll(tagzTemp);

                                    List<String> ingredientzTemp =  new ArrayList<String>(Arrays.asList(ingredients));
                                    ArrayList<String> ingredientz = new ArrayList<>(ingredients.length);
                                    ingredientz.addAll(ingredientzTemp);

                                    List<String> ingredientz2Temp =  new ArrayList<String>(Arrays.asList(ingredients2));
                                    ArrayList<String> ingredientz2 = new ArrayList<>(ingredients2.length);
                                    ingredientz2.addAll(ingredientz2Temp);

                                    List<String> ingredientz3Temp =  new ArrayList<String>(Arrays.asList(ingredients3));
                                    ArrayList<String> ingredientz3 = new ArrayList<>(ingredients3.length);
                                    ingredientz3.addAll(ingredientz3Temp);

                                    List<String> stepzTemp =  new ArrayList<String>(Arrays.asList(steps));
                                    ArrayList<String> stepz = new ArrayList<>(steps.length);
                                    stepz.addAll(stepzTemp);

                                    List<String> ingredtagzTemp =  new ArrayList<String>(Arrays.asList(ingredtags));
                                    ArrayList<String> ingredtagz = new ArrayList<>(ingredtags.length);
                                    ingredtagz.addAll(ingredtagzTemp);

                                    Call<Void> call = apiService.createRecipe(getUID().toString(), username[0], recipeName[0], downloadUrl1, datetime[0], preptext[0], cooktext[0], servestext[0], tagz, ingredientz, ingredientz2, ingredientz3, stepz, ingredtagz);
                                    call.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            hideProgressDialog();
                                            toolbar.setTitle("Add a Recipe");
                                            Fragment fragment = MainFragment.newInstance();
                                            fm.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Log.d("Error", t.toString());
                                            hideProgressDialog();
                                            toolbar.setTitle("Add a Recipe");
                                            Fragment fragment = MainFragment.newInstance();
                                            fm.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                                        }
                                    });


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_PIC) {
            if (resultCode == RESULT_OK) {
                showProgressDialog();
                final Uri photoUri = data.getData();
                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    Random random = new Random();
                                    int rand = random.nextInt(200000);
                                    StorageReference storageRef = storage.getReferenceFromUrl("gs://seniorcapstone-831a0.appspot.com");
                                    StorageReference imagesRef = storageRef.child("profilepics/" +  String.valueOf(rand));
                                    UploadTask uploadTask = imagesRef.putFile(photoUri);
                                    StorageTask<UploadTask.TaskSnapshot> prog = uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                            showProgressDialog();
                                        }
                                    });
                                    StorageTask<UploadTask.TaskSnapshot> paus = uploadTask.addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                            hideProgressDialog();
                                        }
                                    });

                                    StorageTask<UploadTask.TaskSnapshot> fail = uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            hideProgressDialog();
                                        }
                                    });

                                    StorageTask<UploadTask.TaskSnapshot> succ = uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                            downloadUrl1 = taskSnapshot.getDownloadUrl().toString();

                                            Retrofit retrofit = ApiClient.getClient();
                                            ApiInterface apiService = retrofit.create(ApiInterface.class);

                                            Call<Void> call = apiService.setProfileImage(getUID().toString(), downloadUrl1);
                                            call.enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    hideProgressDialog();
                                                    profile.withIcon(downloadUrl1);
                                                    headerResult.updateProfile(profile);
                                                }

                                                @Override
                                                public void onFailure(Call<Void> call, Throwable t) {
                                                    Log.d("Error", t.toString());
                                                    hideProgressDialog();
                                                }
                                            });

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
        }
    }

    public void getProfileImage(){
        Retrofit retrofit = ApiClient.getClient();
        ApiInterface apiService = retrofit.create(ApiInterface.class);

        Call<Url> call = apiService.getProfileImage(getUID());
        call.enqueue(new Callback<Url>() {
            @Override
            public void onResponse(Call<Url> call, Response<Url> response) {
                if (response.body().getUser() != null) {
                    String imageUrl = "";
                    mUser = response.body().getUser();
                    if(mUser.size() > 0) {
                        imageUrl = mUser.get(0).getUrl().toString();

                        mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("imageurl", imageUrl);
                        editor.commit();

                        profile.withIcon(mUser.get(0).getUrl().toString());
                        headerResult.updateProfile(profile);
                    }
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<Url> call, Throwable t) {
                Log.d("Error", t.toString());
            }
        });
    }


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
                        mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("query", "0");
                        editor.commit();
                        spinner.setTag(R.id.pos, 0);



                        Fragment fragment1 = MainFragment.newInstance();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment1, "MainFragment")
                                .addToBackStack(null).commit();

                    } else if (position == 1) {
                        mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("query", "1");
                        editor.commit();
                        spinner.setTag(R.id.pos, 1);

                        Fragment fragment2 = MainFragment.newInstance();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment2, "MainFragment")
                                .addToBackStack(null).commit();
                    }
                    else if(position == 2){
                        mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("query", "2");
                        editor.commit();
                        spinner.setTag(R.id.pos, 2);

                        Fragment fragment2 = MainFragment.newInstance();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment2, "MainFragment")
                                .addToBackStack(null).commit();
                    }
                    else if(position == 3){
                        mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("query", "3");
                        editor.commit();
                        spinner.setTag(R.id.pos, 3);

                        Fragment fragment2 = MainFragment.newInstance();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment2, "MainFragment")
                                .addToBackStack(null).commit();
                    }
                    else if(position == 4){
                        mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("query", "4");
                        editor.commit();
                        spinner.setTag(R.id.pos, 4);

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

    public void toast(String toast){
        SuperActivityToast.create(this, new Style(), Style.TYPE_STANDARD)
                .setText(toast)
                .setDuration(Style.DURATION_VERY_SHORT)
                .setFrame(Style.FRAME_STANDARD)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_ORANGE))
                .setAnimations(Style.ANIMATIONS_FLY).show();
    }

}

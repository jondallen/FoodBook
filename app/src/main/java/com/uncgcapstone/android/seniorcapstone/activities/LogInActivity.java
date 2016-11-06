package com.uncgcapstone.android.seniorcapstone.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.uncgcapstone.android.seniorcapstone.R;
import com.uncgcapstone.android.seniorcapstone.data.Url;
import com.uncgcapstone.android.seniorcapstone.data.User;
import com.uncgcapstone.android.seniorcapstone.io.ApiClient;
import com.uncgcapstone.android.seniorcapstone.io.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * This class is responsible for user log-in and is the launcher activity for the application
 * Upon signing in, the user is taken to the MainActivity
 */
public class LogInActivity extends CoreActivity {

    private static final String TAG = "LogInActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText emailText, passwordText; //Username and password fields
    private Button createButton, logInButton;
    private SignInButton googleButton;
    FirebaseUser user;
    SharedPreferences mSharedPreferences;
    GoogleApiClient mGoogleApiClient;
    final int GOOGLE_SIGN_IN = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);

        /*
        Used for graphical improvements (notification bar)
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        emailText = (EditText) findViewById(R.id.emailText); //Username
        passwordText = (EditText) findViewById(R.id.passwordText); //Password

        createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if(emailText.getText().toString().length() > 6 && passwordText.getText().toString().length() > 2){
                    showProgressDialog();
                    createUser(emailText.getText().toString(), passwordText.getText().toString());

                }
                else{
                    toast("To create an account, please enter a valid username and password.");
                }
            }
                                  });

        logInButton = (Button) findViewById(R.id.logInButton);
        logInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if(emailText.getText().toString().length() > 6 && passwordText.getText().toString().length() > 2) {
                    showProgressDialog();
                    signIn(emailText.getText().toString(), passwordText.getText().toString());
                }
                else{
                    toast("To log in, please enter a valid username and password.");
                }
            }
                                   });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public void createUser(final String email, String password){
        Retrofit retrofit = ApiClient.getClient();
        ApiInterface apiService = retrofit.create(ApiInterface.class);

        Call<Url> call = apiService.createUser(email, password);
        call.enqueue(new Callback<Url>() {
            @Override
            public void onResponse(Call<Url> call, Response<Url> response) {
                Log.d("TEST", response.body().getUid().toString());
                if (response.body() != null) {
                    if(response.body().getUid().equals("0")){
                        toast("Username is taken!");
                        hideProgressDialog();
                        Log.d("TEST", response.body().getUid().toString());
                    }
                    else if(response.body().getUid().equals("1")){
                        toast("Check your username and password input again!");
                        hideProgressDialog();
                        Log.d("TEST", response.body().getUid().toString());
                    }
                    else{
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("uid", response.body().getUid().toString()); //used to display username within the app
                        editor.commit();
                        mainPage(email);
                    }
                }
                else{
                    toast("Error!");
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<Url> call, Throwable t) {
                Log.d("Error", t.toString());
                toast("Error!");
                hideProgressDialog();

            }
        });
    }


    public void signIn(final String email, String password){
        Retrofit retrofit = ApiClient.getClient();
        ApiInterface apiService = retrofit.create(ApiInterface.class);

        Call<Url> call = apiService.signIn(email, password);
        call.enqueue(new Callback<Url>() {
            @Override
            public void onResponse(Call<Url> call, Response<Url> response) {
                Log.d("TEST", response.body().getUid().toString());
                if (response.body() != null) {
                    if(response.body().getUid().equals("0")){
                        toast("Invalid password or username!");
                        hideProgressDialog();
                        Log.d("TEST", response.body().getUid().toString());
                    }
                    else if(response.body().getUid().equals("1")){
                        toast("Missing fields!");
                        hideProgressDialog();
                        Log.d("TEST", response.body().getUid().toString());
                    }
                    else if(response.body().getUid().equals("2")){
                        toast("Username doesn't exist!");
                        hideProgressDialog();
                        Log.d("TEST", response.body().getUid().toString());
                    }
                    else{
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("uid", response.body().getUid().toString()); //used to display username within the app
                        editor.commit();
                        mainPage(email);
                    }
                }
                else{
                    toast("Error!");
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<Url> call, Throwable t) {
                Log.d("Error", t.toString());
                toast("Error!");
                hideProgressDialog();

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    /*
    Creates a username from email (strips out the @xxx.com part)
     */
    private String parseName(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    public void mainPage(final String email){
        mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("email", parseName(email)); //used to display username within the app
        if(!(mSharedPreferences.contains("query")))
            editor.putString("query", "0");                 //Used to initialize the feed to display latest recipes
        else
        editor.putString("query", "0");
        if(!(mSharedPreferences.contains("imageurl")))
            editor.putString("imageurl", "");
        else
            editor.putString("imageurl", "");
        editor.commit();
        hideProgressDialog();
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void toast(String toast){
        SuperActivityToast.create(this, new Style(), Style.TYPE_STANDARD)
                .setText(toast)
                .setDuration(Style.DURATION_SHORT)
                .setFrame(Style.FRAME_STANDARD)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_ORANGE))
                .setAnimations(Style.ANIMATIONS_FLY).show();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SuperActivityToast.onSaveState(outState);

    }
}


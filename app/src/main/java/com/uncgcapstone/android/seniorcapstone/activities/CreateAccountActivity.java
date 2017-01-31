package com.uncgcapstone.android.seniorcapstone.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.uncgcapstone.android.seniorcapstone.R;
import com.uncgcapstone.android.seniorcapstone.data.Url;
import com.uncgcapstone.android.seniorcapstone.io.ApiClient;
import com.uncgcapstone.android.seniorcapstone.io.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * This class is responsible for handling account creation
 */
public class CreateAccountActivity extends CoreActivity {

    EditText emailText1, passwordText1, passwordText2;
    Button createButton1, cancelButton;
    SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);

         /*
        Used for graphical improvements (notification bar)
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        emailText1 = (EditText) findViewById(R.id.emailText1);

        passwordText1 = (EditText) findViewById(R.id.passwordText1);

        /**
         * This listener is responsible for displaying if
         * the first password is equal to the second
         */
        passwordText1.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            if(s.length() == 0){
                passwordText1.setCompoundDrawables(null, null, null, null);
            }
                else if(s.length() > 0 && s.length() < 7){
                passwordText1.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.close1), null);
            }
                else{
                passwordText1.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.check1), null);
            }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        passwordText2 = (EditText) findViewById(R.id.passwordText2);

        /**
         * This listener is responsible for displaying if
         * the second password is equal to the first
         */
        passwordText2.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    passwordText2.setCompoundDrawables(null, null, null, null);
                }
                else if(s.length() > 0 && s.length() < 7){
                    passwordText2.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.close1), null);
                }
                else{
                    if(s.toString().equals(passwordText1.getText().toString()))
                    passwordText2.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.check1), null);
                    else
                        passwordText2.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.close1), null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        createButton1 = (Button) findViewById(R.id.createButton1);
        createButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailText1.getText().toString().length() > 6 && passwordText1.getText().toString().length() > 6 && passwordText2.getText().toString().length() > 6 && passwordText1.getText().toString().equals(passwordText2.getText().toString())){
                    showProgressDialog();
                    createUser(emailText1.getText().toString(), passwordText1.getText().toString());

                }
                else{
                    toast("To create an account, please enter a valid username and password.");
                }
            }
        });

        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateAccountActivity.this, LogInActivity.class);
                startActivity(i);
            }
        });
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

    /**
     * This method is used to create a user account in the database
     * @param email The user's e-mail
     * @param password The user's password
     */
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
                        Toast.makeText(CreateAccountActivity.this, "Account created!", Toast.LENGTH_SHORT).show();
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

    /**
     * This method is responsible for directing the user
     * to the main page upon account creation
     * @param email The e-mail address that they log in with
     */
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
        Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

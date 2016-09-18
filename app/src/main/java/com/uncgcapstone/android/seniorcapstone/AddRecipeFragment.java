package com.uncgcapstone.android.seniorcapstone;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddRecipeFragment extends Fragment {

    private ImageView addPicture;
    private TextView testText;
    private Button testButton;
    FirebaseDatabase database;
    DatabaseReference myRef;
    static final int PICK_PIC = 0;
    Uri photoUri = null;
    SharedPreferences mSharedPreferences;
    JSONParser jsonParser = new JSONParser();
    private final static String TAG = "AddRecipeFragment";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;
    FirebaseStorage storage;

    // url to create new product
    private static String url_create_product = "http://3661590e.ngrok.io/android_connect/create_recipe.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";


    public AddRecipeFragment() {
        // Required empty public constructor
    }

    public static AddRecipeFragment newInstance() {
        AddRecipeFragment fragment = new AddRecipeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance(); //Gets shared instance of firebase auth object
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = mAuth.getCurrentUser();
                if (user != null) {
                    //Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recipe_add, container, false);

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Add a Recipe");

        addPicture = (ImageView) v.findViewById(R.id.addPicture);
        addPicture.setOnClickListener(new View.OnClickListener(){
                                          @Override
                                          public void onClick(View view) {
                                              Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                              getIntent.setType("image/*");

                                              Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                              pickIntent.setType("image/*");

                                              Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                                              chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                                              startActivityForResult(chooserIntent, PICK_PIC);
                                          }
                                      });
        testText = (TextView) v.findViewById(R.id.testText);
        testButton = (Button) v.findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                String recipeName = testText.getText().toString();
                //testText.setText("");
                String uid = (String) ((MainActivity)getActivity()).getUid();
                mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);

                        String username = mSharedPreferences.getString("email", "");
                        ((MainActivity)getActivity()).post(photoUri, uid, username, recipeName); //SEPARATE THREAD???
            }
        });


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PIC) {
            if (resultCode == getActivity().RESULT_OK) {
                photoUri = data.getData();
                //b is the Bitmap

                //int bytes = photoUri.getByteCount();
                //ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
                //photoUri.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

                //byte[] array = buffer.array(); //Get the underlying array containing the data.
                Glide.with(this).load(photoUri).centerCrop().into(addPicture);
            } else {
            }
        }
    }
}




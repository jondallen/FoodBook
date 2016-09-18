package com.uncgcapstone.android.seniorcapstone;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeDetailFragment extends Fragment {

    private final static String TAG = "RecipeDetailFragment";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;

    FirebaseDatabase database;
    DatabaseReference myRef, userRef;
    FirebaseStorage storage;

    TextView detailRecipe, detailName;
    ImageView detailPicture;
    LikeButton detailThumb;
    TextView thumbsText;
    Query starsQuery;
    long likes = 0;
    boolean userLikes = false;
    ValueEventListener mValueEventListener;





    public RecipeDetailFragment() {
        // Required empty public constructor
    }

    public static RecipeDetailFragment newInstance() {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        final String ref = getArguments().getString("position");
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

        detailRecipe = (TextView) v.findViewById(R.id.detailRecipe);
        detailName=(TextView) v.findViewById(R.id.detailName);
        detailPicture = (ImageView) v.findViewById(R.id.detailPicture);
        thumbsText = (TextView) v.findViewById(R.id.thumbsText);

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef  = database.getReference("post").child(ref);
        mValueEventListener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!= null) {
                    detailRecipe.setText((String) dataSnapshot.child("recipe").getValue());
                    detailName.setText((String) dataSnapshot.child("name").getValue());
                    Glide.with(getActivity()).load((String) dataSnapshot.child("url").getValue()).centerCrop().into(detailPicture);
                    thumbsText.setText(String.valueOf((long) dataSnapshot.child("likes").getValue()));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        detailThumb = (LikeButton) v.findViewById(R.id.detailthumb);
        /*thumb.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                likeTransaction();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                likeTransaction();
            }
        });*/


        return v;
    }

    @Override
    public void onStop(){
        super.onStop();
        myRef.removeEventListener(mValueEventListener);
    }


}

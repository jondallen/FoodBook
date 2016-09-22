package com.uncgcapstone.android.seniorcapstone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;
    FirebaseStorage storage;

    FloatingActionButton fab;
    private final static String TAG = "MainFragment";
    String uid = "";
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> productsList;
    private static String url_all_recipes = "http://3661590e.ngrok.io/android_connect/get_all_recipes.php";
    private static final String TAG_SUCCESS = "success";
    JSONArray recipes = null;
    Recipe[] mRecipes;
    Gson gson = new Gson();
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    LinearLayoutManager mLinearLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LikeButton mLikeButtonThumb, mLikeButtonStar;
    

    public MainFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

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

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                new LoadAllProducts().execute();
            }
        });

        storage = FirebaseStorage.getInstance();
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 18) {
                    fab.hide();
                } else if (dy < 0 || mAdapter.getItemCount() == 0)
                    fab.show();
            }
        });

        //getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Hot Recipes");

        fab = (FloatingActionButton) v.findViewById(R.id.myFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddRecipeFragment fragment = AddRecipeFragment.newInstance();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
                ((MainActivity) getActivity()).setToolbar("Add a Recipe");
                ((MainActivity)getActivity()).showOverflowMenu(false);
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private class CardViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView cardTitle, cardUsername;
        private ImageView cardImage;
        public CardViewHolder(View itemView){
            super(itemView);

            cardTitle = (TextView) itemView.findViewById(R.id.cardTitle);
            cardUsername = (TextView) itemView.findViewById(R.id.cardUsername);
            cardImage = (ImageView) itemView.findViewById(R.id.cardImage);
            mLikeButtonThumb = (LikeButton) itemView.findViewById(R.id.thumb);
            mLikeButtonStar = (LikeButton) itemView.findViewById(R.id.star);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // You now for sure this is an ItemView.
                    Toast.makeText(view.getContext(), "ROW PRESSED = " +
                            String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT)
                            .show();
                    Toast.makeText(view.getContext(),
                            mRecipes[getAdapterPosition()].getRecipename().toString(),
                            Toast.LENGTH_SHORT).show();
                }
            });

            mLikeButtonThumb.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    Toast.makeText(likeButton.getContext(), "ITEM PRESSED = " +
                            String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    Toast.makeText(likeButton.getContext(), "ITEM PRESSED = " +
                            String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT)
                            .show();
                }
            });

            mLikeButtonStar.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    Toast.makeText(likeButton.getContext(), "ITEM PRESSED = " +
                            String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    Toast.makeText(likeButton.getContext(), "ITEM PRESSED = " +
                            String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT)
                            .show();
                }
            });

            //itemView.setOnClickListener(this);
            //mLikeButton.setOnClickListener(this);

        }
        /*
        @Override
        public void onClick(View v) {

            if (v.getId() == mLikeButton.getId()){
                Toast.makeText(v.getContext(), "ITEM PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                Toast.makeText(v.getContext(), mRecipes[getAdapterPosition()].getRecipename().toString(), Toast.LENGTH_SHORT).show();
            }
        }*/

        public void bindCard(String username, String recipename, String url) {
            cardUsername.setText(username);
            cardTitle.setText(recipename);
            Glide.with(getActivity()).load(url).centerCrop().into(cardImage);
        }
    }


    private class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
        private Recipe[] recipez;

        public CardAdapter(Recipe[] s){
            recipez = s;
        }

        @Override
        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.card_view_recycler_view, parent, false);
            return new CardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CardViewHolder holder, int position){
            String username = recipez[position].getUsername();
            String recipename = recipez[position].getRecipename();
            String url = recipez[position].getUrl();
            holder.bindCard(username, recipename, url);
        }

        @Override
        public int getItemCount(){
            return recipez.length;
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        //getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mAuth.addAuthStateListener(mAuthListener);
        ((MainActivity)getActivity()).showOverflowMenu(true);
        new LoadAllProducts().execute();
        ((MainActivity)getActivity()).showProgressDialog();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        ((MainActivity)getActivity()).showOverflowMenu(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();
        //getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ((MainActivity)getActivity()).showOverflowMenu(true);
    }




/*
    private class CardViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mTextView, mTextView2, mTextView3, mTextView4;
        private ImageView mImageView, star;
        public CardViewHolder(View itemView){
            super(itemView);
            view = itemView;
            //Might need to be moved to onbindviewholder ?
            view.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    //What happens when you click the cardview
                }
            });
            mCardView = (CardView) itemView.findViewById(R.id.cardview);
            mTextView = (TextView) itemView.findViewById(R.id.cardTitle);
            mTextView2 = (TextView) itemView.findViewById(R.id.cardUsername);
            mTextView3 = (TextView) itemView.findViewById(R.id.cardTime);
            mTextView4 = (TextView) itemView.findViewById(R.id.cardQuantity);
            mImageView = (ImageView) itemView.findViewById(R.id.cardImage);
            star = (ImageView) itemView.findViewById(R.id.cardStar);
        }
        public void bindCard(String recipeName, String url, String username, String time, String feeds) {
            //prevents text overflow
            if(recipeName.length() > 31){
                recipeName = recipeName.substring(0,27) + "...";
            }
            mTextView.setText(recipeName.toString());
            Glide.with(getActivity()).load(url).centerCrop().into(mImageView);
            mTextView2.setText(username);
            mTextView3.setText(time);
            mTextView4.setText(feeds);


        }
    }*/




/*
    private class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
        private List<String> mList, mList1, mList2, mList3, mList4, mList5;

        public CardAdapter(List<String> list, List<String> list1, List<String> list2, List<String> list3, List<String> list4, List<String> list5){
            mList = list;
            mList1 = list1;
            mList2 = list2;
            mList3 = list3;
            mList4 = list4;
            mList5 = list5;

        }

        @Override
        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.card_view_recycler_view, parent, false);
            return new CardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CardViewHolder holder, int position){
            String recipeName = mList.get(position);
            String url = mList1.get(position);
            String username = mList2.get(position);
            String cookTime = mList3.get(position);
            String feeds = mList4.get(position);
            holder.star.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                ImageView star = (ImageView) view.findViewById(R.id.cardStar);
                                                   star.setImageResource(R.drawable.star_lit);
                                               }
                                           });
            holder.bindCard(recipeName, url, username, cookTime, feeds);
        }

        @Override
        public int getItemCount(){
            return mList.size();
        }
    }
*/
    //Swipe to refresh - Code for managing the refresh itself
    void refreshItems(){
        // Load items
        // ...

        // Load complete
        onItemsLoadComplete();
    }

    //Swipe to refresh - what to do after the refreshing is complete
    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

        // Stop refresh animation

    }



    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            Type listType = new TypeToken<List<Recipe>>(){}.getType();
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_recipes, "GET", params);
            Type collectionType = new TypeToken<Collection<Recipe>>(){}.getType();
            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {

                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    recipes = json.getJSONArray("Recipes");
                    int count = recipes.length();
                    mRecipes = new Recipe[count];
                    // looping through All Products
                    for (int i = 0; i < recipes.length(); i++) {
                        JSONObject c = recipes.getJSONObject(i);
                        Log.d(TAG, c.toString());
                        Recipe recipe = gson.fromJson(c.toString(), Recipe.class);
                        mRecipes[i] = new Recipe(recipe.getPostId(), recipe.getUid().toString(), recipe.getUsername().toString(), recipe.getRecipename().toString(), recipe.getUrl().toString());

                           // Log.d(TAG, recipe.getUid().toString() + " " +  recipe.getRecipename().toString() + " " + recipe.getUsername().toString() + " " + recipe.getUrl().toString());

                        // Storing each json item in variable
                        //String id = c.getString(TAG_PID);
                        //String name = c.getString(TAG_NAME);

                        // creating new HashMap
                        //HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                       // map.put(TAG_PID, id);
                       // map.put(TAG_NAME, name);

                        // adding HashList to ArrayList
                        //productsList.add(map);
                    }
                } else {
                    // no products found

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
            // dismiss the dialog after getting all products
           // pDialog.dismiss();
            // updating UI from Background Thread
            ((MainActivity)getActivity()).runOnUiThread(new Runnable() {
                public void run() {

                    mAdapter = new CardAdapter(mRecipes);
                    mRecyclerView.setAdapter(mAdapter);
                    mSwipeRefreshLayout.setRefreshing(false);
                    ((MainActivity)getActivity()).hideProgressDialog();
                }
            });

        }

    }

    }


package com.uncgcapstone.android.seniorcapstone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import static com.bumptech.glide.load.engine.DiskCacheStrategy.RESULT;


public class MainFragment extends Fragment {


    FloatingActionButton fab;
    private final String TAG = "MainFragment";
    JSONParser jParser = new JSONParser();
    private String url_all_recipes = "http://3661590e.ngrok.io/android_connect/get_all_recipes.php";
    private String url_search_recipes = "http://3661590e.ngrok.io/android_connect/search_recipes.php";
    private String url_likes = "http://3661590e.ngrok.io/android_connect/likes.php";
    private String url_unlikes = "http://3661590e.ngrok.io/android_connect/unlikes.php";
    private final String TAG_SUCCESS = "success";
    JSONArray recipes = null;
    Recipe[] mRecipes;
    Gson gson = new Gson();
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    LinearLayoutManager mLinearLayoutManager;
    GridLayoutManager mGridLayoutManager;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RelativeLayout searchBarLayout;
    //TagsEditText searchBar;
    EditText searchBar;
    ImageView searchIcon;
    ProgressDialog mProgressDialog;
    private View v, cardViewHolderView;
    SharedPreferences mSharedPreferences;
    //LikeButton mLikeButtonThumb, mLikeButtonStar;
    

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

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_main, container, false);

        searchBarLayout = (RelativeLayout) v.findViewById(R.id.toolbar2);
        searchBar = (EditText) v.findViewById(R.id.searchbar);
        //searchBar.setTagsBackground(R.drawable.rounded_edittext_orange);
        //searchBar.setTextColor(getResources().getColor(black));
        searchBar.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (searchBar.getText().toString().length() > 0) {
                        mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("search", searchBar.getText().toString());
                        editor.commit();

                        Log.d(TAG, mSharedPreferences.getString("search", ""));

                        MainFragment fragment = MainFragment.newInstance();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                        /*SuperActivityToast.create(getActivity(), new Style(), Style.TYPE_BUTTON)
                                .setText(searchBar.getText().toString())
                                .setDuration(Style.DURATION_SHORT)
                                .setFrame(Style.FRAME_LOLLIPOP)
                                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_PURPLE))
                                .setAnimations(Style.ANIMATIONS_POP).show();*/

                    }
                }
                return false;
            }
        });

        searchIcon = (ImageView) v.findViewById(R.id.search_icon);

//##FIREBASE USER INITIALIZATION
/*
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
        }; */

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                new LoadAllProducts().execute();
            }
        });


        //mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        //////mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //////mStaggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        //////mRecyclerView.setHasFixedSize(true);
        //////mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        ////mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //mLinearLayoutManager.setReverseLayout(true);
        //mLinearLayoutManager.setStackFromEnd(true);


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 18)
                    fab.hide();
                 else if (dy < 0 || mAdapter.getItemCount() == 0)
                    fab.show();
            }
        });

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Latest Recipes");

        fab = (FloatingActionButton) v.findViewById(R.id.myFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddRecipeFragment fragment = AddRecipeFragment.newInstance();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();

            }
        });
        new LoadAllProducts().execute();
        showProgressDialog();
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private class CardViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView cardTitle, cardUsername, cardTime, feedsText, tag1, tag2, tag3, likesText;
        private ImageView cardImage;
        LikeButton mLikeButtonStar;
        ImageButton mLikeButtonThumb;
        public CardViewHolder(View itemView){
            super(itemView);

            cardTitle = (TextView) itemView.findViewById(R.id.cardTitle);
            cardUsername = (TextView) itemView.findViewById(R.id.cardUsername);
            cardImage = (ImageView) itemView.findViewById(R.id.cardImage);
            mLikeButtonThumb = (ImageButton) itemView.findViewById(R.id.thumb);
            mLikeButtonStar = (LikeButton) itemView.findViewById(R.id.star);
            cardTime = (TextView) itemView.findViewById(R.id.cardTime);
            feedsText = (TextView) itemView.findViewById(R.id.feedsText);
            tag1 = (TextView) itemView.findViewById(R.id.tag1);
            tag2 = (TextView) itemView.findViewById(R.id.tag2);
            tag3 = (TextView) itemView.findViewById(R.id.tag3);
            likesText = (TextView) itemView.findViewById(R.id.likesText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String postid = String.valueOf(mRecipes[getAdapterPosition()].getPostId());
                    String url = mRecipes[getAdapterPosition()].getUrl();
                    String recipename = mRecipes[getAdapterPosition()].getRecipename();
                    String servings = mRecipes[getAdapterPosition()].getServes();
                    String preptime = mRecipes[getAdapterPosition()].getPreptime();
                    String cooktime = mRecipes[getAdapterPosition()].getCooktime();
                    ((MainActivity)getActivity()).launchTest(postid, url, recipename, servings, preptime, cooktime);



                }
            });

            mLikeButtonThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = mRecipes[getAdapterPosition()].getLikes().toString();
                    int inte = Integer.parseInt(str);
                    if(inte == 0){
                            Log.d("Testing", "ACTIVATED!!!!");
                         ImageButton img = (ImageButton) v;
                        ((ImageButton) v).setImageResource(R.drawable.thumb_up);
                            final int pos = getAdapterPosition();
                            new Thread() {
                                @Override
                                public void run() {
                                    List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                                    params1.add(new BasicNameValuePair("userid", ((MainActivity) getActivity()).getUID()));
                                    params1.add(new BasicNameValuePair("postid", String.valueOf(mRecipes[pos].getPostId())));
                                    JSONObject json = jParser.makeHttpRequest(url_likes, "POST", params1);
                                }
                            }.start();
                            int total = Integer.parseInt(mRecipes[getAdapterPosition()].getLikestotal().toString());
                            total++;
                            mRecipes[getAdapterPosition()].setLikestotal(String.valueOf(total));
                            mRecipes[getAdapterPosition()].setLikes("1");
                            mAdapter.notifyDataSetChanged();
                        }
                    else {
                        Log.d("Testing", "DEACTIVATED!!!!");
                        ImageButton img = (ImageButton) v;
                        ((ImageButton) v).setImageResource(R.drawable.thumb);
                        final int pos = getAdapterPosition();

                        new Thread() {
                            @Override
                            public void run() {
                                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                                params1.add(new BasicNameValuePair("userid", ((MainActivity) getActivity()).getUID()));
                                params1.add(new BasicNameValuePair("postid", String.valueOf(mRecipes[pos].getPostId())));
                                JSONObject json = jParser.makeHttpRequest(url_unlikes, "POST", params1);
                            }
                        }.start();
                        int total = Integer.parseInt(mRecipes[getAdapterPosition()].getLikestotal().toString());
                        total--;
                        mRecipes[getAdapterPosition()].setLikestotal(String.valueOf(total));
                        mRecipes[getAdapterPosition()].setLikes("0");
                        mAdapter.notifyDataSetChanged();
                    }
                    }
            });
            //




            mLikeButtonStar.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {

                }

                @Override
                public void unLiked(LikeButton likeButton) {

                }
            });

            //itemView.setOnClickListener(this);
            //mLikeButton.setOnClickListener(this);

        }

        public void bindCard(String username, String recipename, String url, String cardtime, String feedstext, String tagText1, String tagText2, String tagText3, String likes, String likestext) {
            cardUsername.setText("Added by " + username);
            cardTitle.setText(recipename);
            cardTime.setText(cardtime);
            feedsText.setText(feedstext);
            int length1 = tagText1.length();
            int length2 = tagText2.length();
            int length3 = tagText3.length();
            int total = 0;
            if(length1 < 25 && length1 > 0) {
                tag1.setText(tagText1);
                tag1.setBackgroundResource(R.drawable.rounded_edittext_orange);
                total += length1;
            }
                if(total + length2 < 25 && length2 > 0) {
                    tag2.setText(tagText2);
                    tag2.setBackgroundResource(R.drawable.rounded_edittext_blue);
                    total += length2;
                }
                    if(total + length3 < 25 && length3 > 0){
                        tag3.setText(tagText3);
                        tag3.setBackgroundResource(R.drawable.rounded_edittext_green);
                    }

            Glide.with(MainFragment.this).load(url).centerCrop().diskCacheStrategy(RESULT).into(cardImage);

            likesText.setText(likestext);

        if(likes.equals("1")){
            mLikeButtonThumb.setActivated(true);
            mLikeButtonThumb.setImageResource(R.drawable.thumb_up);
        }
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
            cardViewHolderView = inflater.inflate(R.layout.card_view_recycler_view, parent, false);
            return new CardViewHolder(cardViewHolderView);
        }

        @Override
        public void onBindViewHolder(CardViewHolder holder, int position){
            String username = recipez[position].getUsername();
            String recipename = recipez[position].getRecipename();
            String url = recipez[position].getUrl();
            String cardtime = String.valueOf( Integer.parseInt(recipez[position].getCooktime()) + Integer.parseInt(recipez[position].getPreptime()));
            String feedstext = String.valueOf(recipez[position].getServes());
            String tag1 = recipez[position].getTag1();
            String tag2 = recipez[position].getTag2();
            String tag3 = recipez[position].getTag3();
            String likes = recipez[position].getLikes();
            String likestext = recipez[position].getLikestotal();
            holder.bindCard(username, recipename, url, cardtime, feedstext, tag1, tag2, tag3, likes, likestext);
        }

        @Override
        public int getItemCount(){
            if(recipez != null)
            return recipez.length;
            return 0;
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }



    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        mSwipeRefreshLayout.setOnRefreshListener(null);
        //mRecyclerView.addOnScrollListener(null);
        fab.setOnClickListener(null);
        mRecyclerView = null;
        mAdapter = null;
        mRecipes = null;
        fab = null;
        searchBar = null;
        searchIcon = null;
        //jParser = null;
        recipes = null;
        //gson = null;
        mStaggeredGridLayoutManager = null;
        mLinearLayoutManager = null;
        mGridLayoutManager = null;
        mSwipeRefreshLayout = null;
        mProgressDialog = null;
        searchBarLayout = null;
        v = null;
        cardViewHolderView = null;
        mSharedPreferences = null;

        /**
         * The below code is used to allow memory to be GC'd correctly upon leaving the fragment
         * It may or may not be actually necessary
         */
        new Thread(new Runnable() {
            public void run() {
                Glide.get(getContext()).clearDiskCache();
            }
        }).start();
        Glide.get(getContext()).clearMemory();
        /**
         *
         */
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d("OnStop Called", "In Main Fragment");
    }


    @Override
    public void onResume(){
        super.onResume();
        //searchBar.setTagsBackground(R.drawable.rounded_edittext_orange);
        //searchBar.setTextColor(getResources().getColor(black));
    }





    //Swipe to refresh - Code for managing the refresh itself
    void refreshItems(){
        onItemsLoadComplete();
    }

    //Swipe to refresh - what to do after the refreshing is complete
    void onItemsLoadComplete() {
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

            mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
            String search = mSharedPreferences.getString("search", "");
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("search", "");
            editor.commit();
            //search.toUpperCase();
            String[] searchArray = search.split(" ");
            if(!(search.equals(""))) {
                for (int i = 0; i < searchArray.length; i++) {
                    Log.d("For loop to make array", searchArray[i]);
                    params.add(new BasicNameValuePair("search[]", searchArray[i]));
                    params.add(new BasicNameValuePair("userid", ((MainActivity)getActivity()).getUID()));
                }
                String[] searchUnbroken = {search};
                for(int i = 0; i < searchUnbroken.length; i++) {
                    params.add(new BasicNameValuePair("searchUnbroken[]", searchUnbroken[i]));
                }
                JSONObject json1 = jParser.makeHttpRequest(url_search_recipes, "POST", params);


                try {
                    if (json1.getJSONArray("Recipes") != null) {
                        // products found
                        // Getting Array of Products
                        recipes = json1.getJSONArray("Recipes");
                        int count = recipes.length();
                        mRecipes = new Recipe[count];
                        // looping through All Products
                        for (int i = 0; i < recipes.length(); i++) {
                            JSONObject c = recipes.getJSONObject(i);
                            //Log.d(TAG, c.toString());
                            Recipe recipe = gson.fromJson(c.toString(), Recipe.class);
                            mRecipes[i] = new Recipe(recipe.getPostId(), recipe.getUid().toString(), recipe.getUsername().toString(), recipe.getRecipename().toString(), recipe.getUrl().toString(), recipe.getDatetime().toString()
                                    , recipe.getPreptime().toString(), recipe.getCooktime().toString(), recipe.getServes().toString(), recipe.getTag1().toString(), recipe.getTag2().toString(), recipe.getTag3().toString(), recipe.getLikes().toString(), recipe.getLikestotal().toString());
                        }
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listType = null;
                params = null;
            }
            else {

                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                params1.add(new BasicNameValuePair("userid", ((MainActivity)getActivity()).getUID()));

                JSONObject json = jParser.makeHttpRequest(url_all_recipes, "POST", params1);

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
                            //Log.d(TAG, c.toString());
                            Recipe recipe = gson.fromJson(c.toString(), Recipe.class);
                            mRecipes[i] = new Recipe(recipe.getPostId(), recipe.getUid().toString(), recipe.getUsername().toString(), recipe.getRecipename().toString(), recipe.getUrl().toString(), recipe.getDatetime().toString()
                                    , recipe.getPreptime().toString(), recipe.getCooktime().toString(), recipe.getServes().toString(), recipe.getTag1().toString(), recipe.getTag2().toString(), recipe.getTag3().toString(), recipe.getLikes().toString(), recipe.getLikestotal().toString());
                        }

                    } else {
                        // no products found

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listType = null;
                params = null;
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
            //(getActivity()).runOnUiThread(new Runnable() {
               //public void run() {
                    mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    mStaggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                    mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
                    mRecyclerView.setHasFixedSize(true);
                    mAdapter = new CardAdapter(mRecipes);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setItemAnimator(null);
                    mSwipeRefreshLayout.setRefreshing(false);
                    hideProgressDialog();
               }
            //});

       // }

    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    }


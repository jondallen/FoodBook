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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.like.LikeButton;
import com.like.OnLikeListener;


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
    private String url_all_recipes_likes = "http://3661590e.ngrok.io/android_connect/get_all_recipes_likes.php";
    private String url_search_recipes = "http://3661590e.ngrok.io/android_connect/search_recipes.php";
    private String url_search_recipes_likes = "http://3661590e.ngrok.io/android_connect/search_recipes_likes.php";
    private String url_likes = "http://3661590e.ngrok.io/android_connect/likes.php";
    private String url_unlikes = "http://3661590e.ngrok.io/android_connect/unlikes.php";
    private String url_favorites = "http://3661590e.ngrok.io/android_connect/favorites.php";
    private String url_unfavorites = "http://3661590e.ngrok.io/android_connect/unfavorites.php";
    private final String TAG_SUCCESS = "success";
    JSONArray recipes = null;
    List<Recipe> mRecipes;
    Gson gson = new Gson();
    RecyclerView mRecyclerView;
    CardAdapter mAdapter;
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
        ((MainActivity)getActivity()).showOverflowMenu(true);

        mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mSharedPreferences.edit();

        searchBarLayout = (RelativeLayout) v.findViewById(R.id.toolbar2);
        searchBar = (EditText) v.findViewById(R.id.searchbar);
        if(!(mSharedPreferences.contains("search"))) {
            editor.putString("search", "");
            editor.commit();
        }
        String str = mSharedPreferences.getString("search", "");
        searchBar.setText(str);

        //searchBar.setTagsBackground(R.drawable.rounded_edittext_orange);
        //searchBar.setTextColor(getResources().getColor(black));
        searchBar.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (searchBar.getText().toString().length() >= 0) {
                        editor.putString("search", searchBar.getText().toString());
                        editor.commit();

                        Log.d(TAG, mSharedPreferences.getString("search", ""));

                        MainFragment fragment = MainFragment.newInstance();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
                return false;
            }
        });

        //searchIcon = (ImageView) v.findViewById(R.id.search_icon);


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

        if(!(mSharedPreferences.getString("search", "").equals("")))
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Search: " + mSharedPreferences.getString("search", ""));
        else if(mSharedPreferences.getString("query", "").equals("0"))
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Latest Recipes");
        else
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Top Recipes");

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
        ImageView mLikeButtonThumb, mLikeButtonStar;
        LikeButton mLikeButton, mFavoriteButton;
        public CardViewHolder(View itemView){
            super(itemView);

            cardTitle = (TextView) itemView.findViewById(R.id.cardTitle);
            cardUsername = (TextView) itemView.findViewById(R.id.cardUsername);
            cardImage = (ImageView) itemView.findViewById(R.id.cardImage);
            //mLikeButtonThumb = (ImageView) itemView.findViewById(R.id.thumb);
            mLikeButton = (LikeButton) itemView.findViewById(R.id.thumb);
            mFavoriteButton = (LikeButton) itemView.findViewById(R.id.star);
            //mLikeButtonStar = (ImageView) itemView.findViewById(R.id.star);
            cardTime = (TextView) itemView.findViewById(R.id.cardTime);
            feedsText = (TextView) itemView.findViewById(R.id.feedsText);
            tag1 = (TextView) itemView.findViewById(R.id.tag1);
            tag2 = (TextView) itemView.findViewById(R.id.tag2);
            tag3 = (TextView) itemView.findViewById(R.id.tag3);
            likesText = (TextView) itemView.findViewById(R.id.likesText);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String postid = String.valueOf(mRecipes.get(getAdapterPosition()).getPostId());
                    String url = mRecipes.get(getAdapterPosition()).getUrl();
                    String recipename = mRecipes.get(getAdapterPosition()).getRecipename();
                    String servings = mRecipes.get(getAdapterPosition()).getServes();
                    String preptime = mRecipes.get(getAdapterPosition()).getPreptime();
                    String cooktime = mRecipes.get(getAdapterPosition()).getCooktime();
                    String likes = mRecipes.get(getAdapterPosition()).getLikes();
                    String favorites = mRecipes.get(getAdapterPosition()).getFavorites();
                    String userid = ((MainActivity) getActivity()).getUID().toString();
                    String adapterpos = String.valueOf(getAdapterPosition());
                    String likestotal = mRecipes.get(getAdapterPosition()).getLikestotal();
                    launchTest(postid, url, recipename, servings, preptime, cooktime, likes, favorites, userid, adapterpos, likestotal);



                }
            });

            mLikeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    String str = mRecipes.get(getAdapterPosition()).getLikes().toString();
                    int inte = Integer.parseInt(str);
                    if (inte == 0) {
                        toast("Liked " + mRecipes.get(getAdapterPosition()).getRecipename().toString());
                        mRecipes.get(getAdapterPosition()).setLikes("1");
                        //ImageView img = (ImageView) v;
                        //img.setImageResource(R.drawable.thumb_up);
                        final int pos = getAdapterPosition();
                        new Thread() {
                            @Override
                            public void run() {
                                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                                params1.add(new BasicNameValuePair("userid", ((MainActivity) getActivity()).getUID()));
                                params1.add(new BasicNameValuePair("postid", String.valueOf(mRecipes.get(pos).getPostId())));
                                JSONObject json = jParser.makeHttpRequest(url_likes, "POST", params1);
                            }
                        }.start();
                        int total = Integer.parseInt(mRecipes.get(getAdapterPosition()).getLikestotal().toString());
                        total++;
                        mRecipes.get(getAdapterPosition()).setLikestotal(String.valueOf(total));
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    toast("Unliked " + mRecipes.get(getAdapterPosition()).getRecipename().toString());
                    mRecipes.get(getAdapterPosition()).setLikes("0");
                    //ImageView img = (ImageView) v;
                    //img.setImageResource(R.drawable.thumb);
                    final int pos = getAdapterPosition();

                    new Thread() {
                        @Override
                        public void run() {
                            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                            params1.add(new BasicNameValuePair("userid", ((MainActivity) getActivity()).getUID()));
                            params1.add(new BasicNameValuePair("postid", String.valueOf(mRecipes.get(pos).getPostId())));
                            JSONObject json = jParser.makeHttpRequest(url_unlikes, "POST", params1);
                        }
                    }.start();
                    int total = Integer.parseInt(mRecipes.get(getAdapterPosition()).getLikestotal().toString());
                    total--;
                    mRecipes.get(getAdapterPosition()).setLikestotal(String.valueOf(total));
                    mAdapter.notifyDataSetChanged();
                }
            });

            mFavoriteButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    String str = mRecipes.get(getAdapterPosition()).getFavorites().toString();
                    int inte = Integer.parseInt(str);
                    if (inte == 0) {
                        toast("Favorited " + mRecipes.get(getAdapterPosition()).getRecipename().toString());
                        mRecipes.get(getAdapterPosition()).setFavorites("1");
                        //ImageView img = (ImageView) v;
                        //img.setImageResource(R.drawable.star_on);
                        final int pos = getAdapterPosition();
                        new Thread() {
                            @Override
                            public void run() {
                                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                                params1.add(new BasicNameValuePair("userid", ((MainActivity) getActivity()).getUID()));
                                params1.add(new BasicNameValuePair("postid", String.valueOf(mRecipes.get(pos).getPostId())));
                                JSONObject json = jParser.makeHttpRequest(url_favorites, "POST", params1);
                            }
                        }.start();
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                        toast("Unfavorited " + mRecipes.get(getAdapterPosition()).getRecipename().toString());
                        mRecipes.get(getAdapterPosition()).setFavorites("0");
                        //ImageView img = (ImageView) v;
                        //img.setImageResource(R.drawable.star);
                        final int pos = getAdapterPosition();

                        new Thread() {
                            @Override
                            public void run() {
                                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                                params1.add(new BasicNameValuePair("userid", ((MainActivity) getActivity()).getUID()));
                                params1.add(new BasicNameValuePair("postid", String.valueOf(mRecipes.get(pos).getPostId())));
                                JSONObject json = jParser.makeHttpRequest(url_unfavorites, "POST", params1);
                            }
                        }.start();
                        mAdapter.notifyDataSetChanged();
                }
            });


        }

        public void bindCard(String username, String recipename, String url, String cardtime, String feedstext, String tagText1, String tagText2, String tagText3, String likes, String likestext, String favorites) {
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
            mLikeButton.setLiked(true);
        }
            else{
            mLikeButton.setLiked(false);
        }

            if(favorites.equals("1")){
                mFavoriteButton.setLiked(true);
            }
            else{
                mFavoriteButton.setLiked(false);
            }

        }
    }


    public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
        private List<Recipe> recipez;

        public CardAdapter(List<Recipe> s){
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
            String username = recipez.get(position).getUsername();
            String recipename = recipez.get(position).getRecipename();
            String url = recipez.get(position).getUrl();
            String cardtime = String.valueOf( Integer.parseInt(recipez.get(position).getCooktime()) + Integer.parseInt(recipez.get(position).getPreptime()));
            String feedstext = String.valueOf(recipez.get(position).getServes());
            String tag1 = recipez.get(position).getTag1();
            String tag2 = recipez.get(position).getTag2();
            String tag3 = recipez.get(position).getTag3();
            String likes = recipez.get(position).getLikes();
            String likestext = recipez.get(position).getLikestotal();
            String favorites = recipez.get(position).getFavorites();
            holder.bindCard(username, recipename, url, cardtime, feedstext, tag1, tag2, tag3, likes, likestext, favorites);
        }

        @Override
        public int getItemCount(){
            if(recipez != null)
            return recipez.size();
            return 0;
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public List<Recipe> getData(){
            return recipez;
        }
        public void setData(List<Recipe> data){
            recipez = data;
        }


    }



    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity)getActivity()).showOverflowMenu(true);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        mSwipeRefreshLayout.setOnRefreshListener(null);
        //mRecyclerView.addOnScrollListener(null);
        fab.setOnClickListener(null);
        mRecyclerView = null;
        //mAdapter = null;
        mRecipes = null;
        fab = null;
        searchBar.setText("");
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
        ((MainActivity)getActivity()).showOverflowMenu(true);
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
            //////editor.putString("search", "");
            //////editor.commit();
            //search.toUpperCase();
            String[] searchArray = search.split(" ");
            JSONObject json1;
            JSONObject json;
            if(!(search.equals(""))) {
                if(mSharedPreferences.getString("query", "").equals("0")){
                    for (int i = 0; i < searchArray.length; i++) {
                        Log.d("For loop to make array", searchArray[i]);
                        params.add(new BasicNameValuePair("search[]", searchArray[i]));
                        params.add(new BasicNameValuePair("userid", ((MainActivity)getActivity()).getUID()));
                    }
                    String[] searchUnbroken = {search};
                    for(int i = 0; i < searchUnbroken.length; i++) {
                        params.add(new BasicNameValuePair("searchUnbroken[]", searchUnbroken[i]));
                    }
                json1 = jParser.makeHttpRequest(url_search_recipes, "POST", params);
               }
                else{
                    for (int i = 0; i < searchArray.length; i++) {
                        Log.d("For loop to make array", searchArray[i]);
                        params.add(new BasicNameValuePair("search[]", searchArray[i]));
                        params.add(new BasicNameValuePair("userid", ((MainActivity)getActivity()).getUID()));
                    }
                    String[] searchUnbroken = {search};
                    for(int i = 0; i < searchUnbroken.length; i++) {
                        params.add(new BasicNameValuePair("searchUnbroken[]", searchUnbroken[i]));
                    }
                    json1 = jParser.makeHttpRequest(url_search_recipes_likes, "POST", params);
                }


                try {
                    if (json1 != null) {
                        // products found
                        // Getting Array of Products
                        recipes = json1.getJSONArray("Recipes");
                        int count = recipes.length();
                        Log.d("Count: ", String.valueOf(count));
                        mRecipes = new ArrayList<>();
                        // looping through All Products
                        for (int i = 0; i < recipes.length(); i++) {
                            JSONObject c = recipes.getJSONObject(i);
                            //Log.d(TAG, c.toString());
                            Recipe recipe = gson.fromJson(c.toString(), Recipe.class);
                            mRecipes.add(new Recipe(recipe.getPostId(),
                                    recipe.getUid().toString(),
                                    recipe.getUsername().toString(),
                                    recipe.getRecipename().toString(),
                                    recipe.getUrl().toString(),
                                    recipe.getDatetime().toString()
                                    , recipe.getPreptime().toString(),
                                    recipe.getCooktime().toString(),
                                    recipe.getServes().toString(),
                                    recipe.getTag1().toString(),
                                    recipe.getTag2().toString(),
                                    recipe.getTag3().toString(),
                                    recipe.getLikes().toString(),
                                    recipe.getLikestotal().toString()
                            , recipe.getFavorites().toString()));
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

                if(mSharedPreferences.getString("query", "").equals("0")){
                    List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                    params1.add(new BasicNameValuePair("userid", ((MainActivity)getActivity()).getUID()));
                    json = jParser.makeHttpRequest(url_all_recipes, "POST", params1);
                }
                else{
                    List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                    params1.add(new BasicNameValuePair("userid", ((MainActivity)getActivity()).getUID()));
                    json = jParser.makeHttpRequest(url_all_recipes_likes, "POST", params1);
                }


                try {



                    // Checking for SUCCESS TAG
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // products found
                        // Getting Array of Products
                        recipes = json.getJSONArray("Recipes");
                        int count = recipes.length();
                        mRecipes = new ArrayList<>();
                        // looping through All Products
                        for (int i = 0; i < recipes.length(); i++) {
                            JSONObject c = recipes.getJSONObject(i);
                            //Log.d(TAG, c.toString());
                            Recipe recipe = gson.fromJson(c.toString(), Recipe.class);
                            mRecipes.add(new Recipe(recipe.getPostId(), recipe.getUid().toString(), recipe.getUsername().toString(), recipe.getRecipename().toString(), recipe.getUrl().toString(), recipe.getDatetime().toString()
                                    , recipe.getPreptime().toString(), recipe.getCooktime().toString(), recipe.getServes().toString(), recipe.getTag1().toString(), recipe.getTag2().toString(), recipe.getTag3().toString(), recipe.getLikes().toString(), recipe.getLikestotal().toString()
                            , recipe.getFavorites().toString()));
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

    public void toast(String toast){
        SuperActivityToast.create(getActivity(), new Style(), Style.TYPE_STANDARD)
                .setText(toast)
                .setDuration(Style.DURATION_VERY_SHORT)
                .setFrame(Style.FRAME_LOLLIPOP)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_ORANGE))
                .setAnimations(Style.ANIMATIONS_FLY).show();
    }

    public void launchTest(String postid, String url, String recipename, String servings, String preptime, String cooktime, String likes, String favorites, String userid, String adapterpos, String likestotal){
        Intent i = new Intent(getActivity(), DetailedRecipeActivity.class);
        i.putExtra("postid", postid);
        i.putExtra("url", url);
        i.putExtra("recipename", recipename);
        i.putExtra("servings", servings);
        i.putExtra("preptime", preptime);
        i.putExtra("cooktime", cooktime);
        i.putExtra("likes", likes);
        i.putExtra("favorites", favorites);
        i.putExtra("userid", userid);
        i.putExtra("adapterpos", adapterpos);
        i.putExtra("likestotal", likestotal);
        startActivityForResult(i, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if(requestCode == 0) {
            int adapterpos = Integer.parseInt(data.getStringExtra("adapterpos"));
            String likestotal = data.getStringExtra("likestotal");
            Log.d(TAG, String.valueOf(adapterpos));
            String likes = data.getStringExtra("likes");
            String favorites = data.getStringExtra("favorites");
            mRecipes.get(adapterpos).setLikes(likes);
            mRecipes.get(adapterpos).setFavorites(favorites);
            mRecipes.get(adapterpos).setLikestotal(likestotal);
            //mAdapter.swapItems(mRecipes);
            //mAdapter.notifyItemChanged(adapterpos);
            mAdapter.notifyItemChanged(adapterpos);
        }

    }

    }


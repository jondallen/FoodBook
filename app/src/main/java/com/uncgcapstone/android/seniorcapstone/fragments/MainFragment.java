package com.uncgcapstone.android.seniorcapstone.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.uncgcapstone.android.seniorcapstone.activities.DetailedRecipeActivity;
import com.uncgcapstone.android.seniorcapstone.data.Recipes;
import com.uncgcapstone.android.seniorcapstone.io.ApiClient;
import com.uncgcapstone.android.seniorcapstone.io.ApiInterface;
import com.uncgcapstone.android.seniorcapstone.activities.MainActivity;
import com.uncgcapstone.android.seniorcapstone.R;
import com.uncgcapstone.android.seniorcapstone.data.Recipe;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.RESULT;



public class MainFragment extends Fragment {


    FloatingActionButton fab;
    private final String TAG = "MainFragment";
    JSONArray recipes = null;
    List<Recipe> mRecipes;
    RecyclerView mRecyclerView;
    CardAdapter mAdapter;
    LinearLayoutManager mLinearLayoutManager;
    GridLayoutManager mGridLayoutManager;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RelativeLayout searchBarLayout;
    EditText searchBar;
    ImageView searchIcon;
    AlertDialog mAlertDialog;
    private View v, cardViewHolderView;
    SharedPreferences mSharedPreferences;
    HashMap likePost = new HashMap();
    HashMap favoritePost = new HashMap();
    HashMap likesTotalPost = new HashMap();
    TextView noResults;
    

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
        showProgressDialog();
        ((MainActivity)getActivity()).showOverflowMenu(true);

        mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
        final SharedPreferences.Editor editor = mSharedPreferences.edit();

        noResults = (TextView) v.findViewById(R.id.noResults);
        noResults.setVisibility(GONE);

        searchBarLayout = (RelativeLayout) v.findViewById(R.id.toolbar2);
        searchBar = (EditText) v.findViewById(R.id.searchbar);
        if(!(mSharedPreferences.contains("search"))) {
            editor.putString("search", "");
            editor.commit();
        }
        String str = mSharedPreferences.getString("search", "");
        searchBar.setText(str);
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

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRecipes();
            }
        });

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 18)
                    fab.hide();
                 else if (dy < 0 || mAdapter.getItemCount() == 0)
                    fab.show();
            }
        });

        if(!(mSharedPreferences.getString("search", "").equals(""))) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Search: " + mSharedPreferences.getString("search", ""));
        }
        else if(mSharedPreferences.getString("query", "").equals("0"))
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Latest");
        else if(mSharedPreferences.getString("query", "").equals("1"))
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Most Liked");
        else if(mSharedPreferences.getString("query", "").equals("2"))
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Favorites");
        else if(mSharedPreferences.getString("query", "").equals("3"))
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Highest Rated");
        else if(mSharedPreferences.getString("query", "").equals("4"))
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Most Rated");

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

        mRecipes = new ArrayList<Recipe>();
        getRecipes();

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity)getActivity()).showOverflowMenu(true);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        SuperActivityToast.cancelAllSuperToasts();
        mSwipeRefreshLayout.setOnRefreshListener(null);
        fab.setOnClickListener(null);
        mRecyclerView = null;
        mRecipes = null;
        fab = null;
        searchBar.setText("");
        searchBar = null;
        searchIcon = null;
        recipes = null;
        mStaggeredGridLayoutManager = null;
        mLinearLayoutManager = null;
        mGridLayoutManager = null;
        mSwipeRefreshLayout = null;
        mAlertDialog = null;
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

    private void getRecipes() {
        mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
        String search = mSharedPreferences.getString("search", "");
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String[] searchArray = search.split(" ");

        /*
        Create our retrofit client ApiClient
         */
        Retrofit retrofit = ApiClient.getClient();
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<Recipes> call;
        if (!(search.equals(""))) {
            String[] searchUnbroken = {search};
            if(mSharedPreferences.getString("query", "").equals("0")) {
                call = apiService.searchAllRecipes(((MainActivity) getActivity()).getUID(), searchArray, searchUnbroken);
            }
            else if(mSharedPreferences.getString("query", "").equals("1")) {
                call = apiService.searchRecipesLikes(((MainActivity) getActivity()).getUID(), searchArray, searchUnbroken);
            }
            else if(mSharedPreferences.getString("query", "").equals("2")) {
                call = apiService.searchRecipesFavorites(((MainActivity) getActivity()).getUID(), searchArray, searchUnbroken);
            }
            else if(mSharedPreferences.getString("query", "").equals("3")) {
                call = apiService.searchRecipesRating(((MainActivity) getActivity()).getUID(), searchArray, searchUnbroken);
            }
            else  if(mSharedPreferences.getString("query", "").equals("4")) {
                call = apiService.searchRecipesNumRating(((MainActivity) getActivity()).getUID(), searchArray, searchUnbroken);
            }
            else  if(mSharedPreferences.getString("query", "").equals("5")) {
                 call = apiService.searchRecipesFollows(((MainActivity) getActivity()).getUID(), searchArray, searchUnbroken);
            }
            else {
                call = apiService.searchRecipesSuggested(((MainActivity) getActivity()).getUID(), searchArray, searchUnbroken);
            }
                call.enqueue(new Callback<Recipes>() {
                    @Override
                    public void onResponse(Call<Recipes> call, Response<Recipes> response) {
                        if (response.body() != null) {
                            System.out.println(response.body().getRecipes());
                            mRecipes = response.body().getRecipes();
                            refreshUI();
                        }
                        else{
                            refreshUI();
                        }
                    }

                    @Override
                    public void onFailure(Call<Recipes> call, Throwable t) {
                        Log.d("Error", t.toString());
                        refreshUI();
                    }
                });
            }
        else {
            if(mSharedPreferences.getString("query", "").equals("0")) {
                call = apiService.getAllRecipes(((MainActivity) getActivity()).getUID());
            }
            else  if(mSharedPreferences.getString("query", "").equals("1")) {
                call = apiService.getAllRecipesLikes(((MainActivity) getActivity()).getUID());
            }
            else if(mSharedPreferences.getString("query", "").equals("2")) {
                call = apiService.getAllRecipesFavorites(((MainActivity) getActivity()).getUID());
            }
            else if(mSharedPreferences.getString("query", "").equals("3")) {
                call = apiService.getAllRecipesRating(((MainActivity) getActivity()).getUID());
            }
            else if(mSharedPreferences.getString("query", "").equals("4")) {
                call = apiService.getAllRecipesNumRating(((MainActivity) getActivity()).getUID());
            }
            else  if(mSharedPreferences.getString("query", "").equals("5")) {
                call = apiService.getAllRecipesFollows(((MainActivity) getActivity()).getUID());
            }
            else {
                call = apiService.getAllRecipesSuggested(((MainActivity) getActivity()).getUID());
            }
                call.enqueue(new Callback<Recipes>() {
                    @Override
                    public void onResponse(Call<Recipes> call, Response<Recipes> response) {
                        if (response.body() != null) {
                            System.out.println(response.body());
                            mRecipes = response.body().getRecipes();
                            refreshUI();
                        }
                        else{
                            refreshUI();
                        }
                    }

                    @Override
                    public void onFailure(Call<Recipes> call, Throwable t) {
                        Log.d("Error", t.toString());
                        refreshUI();
                    }
                });
            }
        }


    void refreshItems(){
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
    }


    private class CardViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView cardTitle, cardUsername, cardTime, feedsText, tag1, tag2, tag3, likesText, countText;
        private ImageView cardImage;
        ImageView mLikeButtonThumb, mLikeButtonStar;
        LikeButton mLikeButton, mFavoriteButton;
        SimpleRatingBar starRating;
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
            starRating = (SimpleRatingBar) itemView.findViewById(R.id.starRating);
            countText = (TextView) itemView.findViewById(R.id.countText);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String postid = String.valueOf(mRecipes.get(getAdapterPosition()).getPostid());
                    String url = mRecipes.get(getAdapterPosition()).getUrl();
                    String recipename = mRecipes.get(getAdapterPosition()).getRecipename();
                    String servings = mRecipes.get(getAdapterPosition()).getServes();
                    String preptime = mRecipes.get(getAdapterPosition()).getPreptime();
                    String cooktime = mRecipes.get(getAdapterPosition()).getCooktime();
                    String likes = mRecipes.get(getAdapterPosition()).getLikes();
                    String favorites = mRecipes.get(getAdapterPosition()).getFavorites();
                    String userid = ((MainActivity) getActivity()).getUID().toString();
                    String adapterpos = String.valueOf(getAdapterPosition());
                    String likestotal = String.valueOf(mRecipes.get(getAdapterPosition()).getLikestotal());
                    String username = mRecipes.get(getAdapterPosition()).getUsername();
                    String postuserid = mRecipes.get(getAdapterPosition()).getUid();
                    launchTest(postid, url, recipename, servings, preptime, cooktime, likes, favorites, userid, adapterpos, likestotal, username, postuserid);



                }
            });

            mLikeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    String str = mRecipes.get(getAdapterPosition()).getLikes().toString();
                    int inte = Integer.parseInt(str);
                    if (inte == 0) {
                        mRecipes.get(getAdapterPosition()).setLikes("1");
                        final int pos = getAdapterPosition();
                        Retrofit retrofit = ApiClient.getClient();
                        ApiInterface apiService = retrofit.create(ApiInterface.class);

                        //Do before call to make app feel less laggy
                        toast("Liked " + mRecipes.get(getAdapterPosition()).getRecipename().toString());
                        int total = Integer.parseInt(mRecipes.get(getAdapterPosition()).getLikestotal().toString());
                        total++;
                        mRecipes.get(getAdapterPosition()).setLikestotal(String.valueOf(total));
                        mAdapter.notifyDataSetChanged();

                        Call<Void> call = apiService.likes(((MainActivity) getActivity()).getUID(), String.valueOf(mRecipes.get(pos).getPostid()));
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.d("Error", t.toString());
                            }
                        });
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    mRecipes.get(getAdapterPosition()).setLikes("0");
                    final int pos = getAdapterPosition();
                    Retrofit retrofit = ApiClient.getClient();
                    ApiInterface apiService = retrofit.create(ApiInterface.class);

                    toast("Unliked " + mRecipes.get(getAdapterPosition()).getRecipename().toString());
                    int total = Integer.parseInt(mRecipes.get(getAdapterPosition()).getLikestotal().toString());
                    total--;
                    mRecipes.get(getAdapterPosition()).setLikestotal(String.valueOf(total));
                    mAdapter.notifyDataSetChanged();

                    Call<Void> call = apiService.unlikes(((MainActivity) getActivity()).getUID(), String.valueOf(mRecipes.get(pos).getPostid()));
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.d("Error", t.toString());
                        }
                    });
                }
            });

            mFavoriteButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    String str = mRecipes.get(getAdapterPosition()).getFavorites().toString();
                    int inte = Integer.parseInt(str);
                    if (inte == 0) {
                        mRecipes.get(getAdapterPosition()).setFavorites("1");

                        final int pos = getAdapterPosition();
                        Retrofit retrofit = ApiClient.getClient();
                        ApiInterface apiService = retrofit.create(ApiInterface.class);

                        toast("Favorited " + mRecipes.get(getAdapterPosition()).getRecipename().toString());
                        mAdapter.notifyDataSetChanged();

                        Call<Void> call = apiService.favorites(((MainActivity) getActivity()).getUID(), String.valueOf(mRecipes.get(pos).getPostid()));
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.d("Error", t.toString());
                            }
                        });
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    mRecipes.get(getAdapterPosition()).setFavorites("0");

                    final int pos = getAdapterPosition();

                    Retrofit retrofit = ApiClient.getClient();
                    ApiInterface apiService = retrofit.create(ApiInterface.class);

                    toast("Unfavorited " + mRecipes.get(getAdapterPosition()).getRecipename().toString());
                    mAdapter.notifyDataSetChanged();

                    Call<Void> call = apiService.unfavorites(((MainActivity) getActivity()).getUID(), String.valueOf(mRecipes.get(pos).getPostid()));
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.d("Error", t.toString());
                        }
                    });
                }
            });


        }

        public void bindCard(String username, String recipename, String url, String cardtime, String feedstext, String tagText1, String tagText2, String tagText3, String likes, String likestext, String favorites, String rating, String count) {
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

            if(((MainActivity)getActivity()).getUID().equals(mRecipes.get(getAdapterPosition()).getUid())){
                mLikeButton.setEnabled(false);
                mFavoriteButton.setEnabled(false);
            }

            starRating.setStepSize(0.05F);
            Float f = Float.parseFloat(rating);
            starRating.setRating(f);


            countText.setText("(" + count + ")");



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
            String likestext = String.valueOf(recipez.get(position).getLikestotal());
            String favorites = recipez.get(position).getFavorites();
            String rating = String.valueOf(recipez.get(position).getRating());
            String count = recipez.get(position).getCount().toString();
            holder.bindCard(username, recipename, url, cardtime, feedstext, tag1, tag2, tag3, likes, likestext, favorites, rating, count);
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

    public void showProgressDialog() {
        if (mAlertDialog == null) {
            mAlertDialog = new SpotsDialog(getContext());
            mAlertDialog.setCancelable(false);
        }

        mAlertDialog.show();
    }

    public void hideProgressDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    public void refreshUI(){
            mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mStaggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            mAdapter = new CardAdapter(mRecipes);
            mRecyclerView.setAdapter(mAdapter);
            mSwipeRefreshLayout.setRefreshing(false);

        if(mRecipes.size() > 0){
            noResults.setVisibility(GONE);
        }
        else{
            noResults.setVisibility(VISIBLE);
        }

        hideProgressDialog();
    }

    public void toast(String toast){
        SuperActivityToast.create(getActivity(), new Style(), Style.TYPE_STANDARD)
                .setText(toast)
                .setDuration(Style.DURATION_VERY_SHORT)
                .setFrame(Style.FRAME_STANDARD)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_ORANGE))
                .setAnimations(Style.ANIMATIONS_FLY).show();
    }

    public void launchTest(String postid, String url, String recipename, String servings, String preptime, String cooktime, String likes, String favorites, String userid, String adapterpos, String likestotal, String username, String postuserid){
        Intent i = new Intent(getActivity(), DetailedRecipeActivity.class);
        Bundle extras = new Bundle();
        extras.putString("postid", postid);
        extras.putString("url", url);
        extras.putString("recipename", recipename);
        extras.putString("servings", servings);
        extras.putString("preptime", preptime);
        extras.putString("cooktime", cooktime);
        extras.putString("likes", likes);
        extras.putString("favorites", favorites);
        extras.putString("userid", userid);
        extras.putString("adapterpos", adapterpos);
        extras.putString("likestotal", likestotal);
        extras.putString("username", username);
        mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
        String loggedinuser = mSharedPreferences.getString("email", "");
        extras.putString("loggedinuser", loggedinuser);
        extras.putString("postuserid", postuserid);

        likePost = new HashMap();
        favoritePost = new HashMap();
        likesTotalPost = new HashMap();

        likePost.put(postid, likes);
        favoritePost.put(postid, favorites);
        likesTotalPost.put(postid, likestotal);

        extras.putSerializable("likePost", likePost);
        extras.putSerializable("favoritePost", favoritePost);
        extras.putSerializable("likesTotalPost", likesTotalPost);
        i.putExtras(extras);

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
            likePost = (HashMap) data.getSerializableExtra("likePost");
            favoritePost = (HashMap) data.getSerializableExtra("favoritePost");
            likesTotalPost = (HashMap) data.getSerializableExtra("likesTotalPost");


            for(int i = 0; i < mRecipes.size(); i++){
                if(likePost.containsKey(mRecipes.get(i).getPostid())){
                    mRecipes.get(i).setLikes((String) likePost.get(mRecipes.get(i).getPostid()));
                    mRecipes.get(i).setLikestotal((String) likesTotalPost.get(mRecipes.get(i).getPostid()));
                }
            }

            for(int i = 0; i < mRecipes.size(); i++){
                if(favoritePost.containsKey(mRecipes.get(i).getPostid())){
                    mRecipes.get(i).setFavorites((String) favoritePost.get(mRecipes.get(i).getPostid()));
                }
            }
            //mRecipes.get(adapterpos).setLikes(likes);
            //mRecipes.get(adapterpos).setFavorites(favorites);
            //mRecipes.get(adapterpos).setLikestotal(likestotal);
            likePost.clear();
            favoritePost.clear();
            likesTotalPost.clear();
            mAdapter.notifyDataSetChanged();

        }


    }

    }


package com.uncgcapstone.android.seniorcapstone.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.uncgcapstone.android.seniorcapstone.R;
import com.uncgcapstone.android.seniorcapstone.data.Recipe;
import com.uncgcapstone.android.seniorcapstone.data.Recipes;
import com.uncgcapstone.android.seniorcapstone.fragments.MainFragment;
import com.uncgcapstone.android.seniorcapstone.io.ApiClient;
import com.uncgcapstone.android.seniorcapstone.io.ApiInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.RESULT;

public class SelfProfileActivity extends AppCompatActivity {

    CircleImageView profileImage;
    TextView userTextView;
    String url, username, postuserid, userid = "";
    Toolbar toolbar2;
    TextView profileNumRecipes;

    List<Recipe> mRecipes;
    RecyclerView mRecyclerView;
    CardAdapter mAdapter;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    HashMap likePost;
    HashMap favoritePost;
    HashMap likesTotalPost;
    HashMap followsUser;
    LikeButton profileFollowButton;

    android.app.AlertDialog mAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_profile);

        profileNumRecipes = (TextView) findViewById(R.id.profileNumRecipes);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerviewProfile);
        mRecyclerView.setNestedScrollingEnabled(false);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            if(bundle.getString("url") != null)
            url = bundle.getString("url");
            else
            url = "";
            username = bundle.getString("username");
            postuserid = bundle.getString("postuserid");
            userid = bundle.getString("userid");
            likePost = (HashMap) bundle.getSerializable("likePost");
            favoritePost = (HashMap) bundle.getSerializable("favoritePost");
            likesTotalPost = (HashMap) bundle.getSerializable("likesTotalPost");
            followsUser = (HashMap) bundle.getSerializable("followsUser");
        }

        profileFollowButton = (LikeButton) findViewById(R.id.profileFollowButton);

        profileFollowButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                if(!(userid.equals(postuserid))) {
                    if(followsUser.containsKey(postuserid)){
                        followsUser.remove(postuserid);
                        followsUser.put(postuserid, "1");
                    }
                    else{
                        followsUser.put(postuserid, "1");
                    }

                    Retrofit retrofit = ApiClient.getClient();
                    ApiInterface apiService = retrofit.create(ApiInterface.class);

                    toast("Followed " + username);

                    Call<Void> call = apiService.follows(userid, postuserid);
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
                else{
                    toast("You can't follow yourself!");
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if(!(userid.equals(postuserid))) {
                    if(followsUser.containsKey(postuserid)){
                        followsUser.remove(postuserid);
                        followsUser.put(postuserid, "0");
                    }
                    else{
                        followsUser.put(postuserid, "0");
                    }

                    Retrofit retrofit = ApiClient.getClient();
                    ApiInterface apiService = retrofit.create(ApiInterface.class);

                    toast("Unfollowed " + username);

                    Call<Void> call = apiService.unfollows(userid, postuserid);
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
                else{
                    toast("You can't unfollow yourself!");
                }
            }
        });




        Log.d("Test: ", "URL: " + url + " username: " + username + " postuserid: " + postuserid + " userid:" + userid);

        profileImage = (CircleImageView) findViewById(R.id.profileImage);
        if(url.length() > 0){
            Glide.with(this).load(url).diskCacheStrategy(RESULT).dontAnimate().into(profileImage);
        }
        userTextView = (TextView) findViewById(R.id.profileUsername);
        userTextView.setText(username);

        toolbar2 = (Toolbar) findViewById(R.id.toolbar2);


        setSupportActionBar(toolbar2);
        getSupportActionBar().setTitle(username + "'s profile");

        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mRecipes = new ArrayList<Recipe>();

        fetchRecipes();

    }

    @Override
    public void onStop(){
        super.onStop();
        SuperActivityToast.cancelAllSuperToasts();
    }


public void fetchRecipes(){
    Retrofit retrofit = ApiClient.getClient();
    ApiInterface apiService = retrofit.create(ApiInterface.class);
    showProgressDialog();

    Call<Recipes> call = apiService.getAllRecipesUser(postuserid, userid);
    call.enqueue(new Callback<Recipes>() {
        @Override
        public void onResponse(Call<Recipes> call, Response<Recipes> response) {
            if (response.body() != null) {
                mRecipes = response.body().getRecipes();
                isFollowing();
            }
            else{
                isFollowing();
                hideProgressDialog();
            }
        }
        @Override
        public void onFailure(Call<Recipes> call, Throwable t) {
            Log.d("Error", t.toString());
            isFollowing();
        }
    });
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
                    final int pos = getAdapterPosition();
                    String str = mRecipes.get(getAdapterPosition()).getLikes().toString();
                    int inte = Integer.parseInt(str);

                    if(likePost.containsKey(mRecipes.get(pos).getPostid())){
                        likePost.remove(mRecipes.get(pos).getPostid());
                        likePost.put(mRecipes.get(pos).getPostid(), "1");
                    }
                    else{
                        likePost.put(mRecipes.get(pos).getPostid(), "1");
                    }

                        mRecipes.get(getAdapterPosition()).setLikes("1");

                        Retrofit retrofit = ApiClient.getClient();
                        ApiInterface apiService = retrofit.create(ApiInterface.class);

                        //Do before call to make app feel less laggy
                        toast("Liked " + mRecipes.get(getAdapterPosition()).getRecipename().toString());
                        int total = Integer.parseInt(mRecipes.get(getAdapterPosition()).getLikestotal().toString());
                        total++;

                    likesTotalPost.put(mRecipes.get(getAdapterPosition()).getPostid(), String.valueOf(total));

                        mRecipes.get(getAdapterPosition()).setLikestotal(String.valueOf(total));
                        mAdapter.notifyDataSetChanged();

                        Call<Void> call = apiService.likes(userid, String.valueOf(mRecipes.get(pos).getPostid()));
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

                @Override
                public void unLiked(LikeButton likeButton) {
                    mRecipes.get(getAdapterPosition()).setLikes("0");
                    final int pos = getAdapterPosition();

                    if(likePost.containsKey(mRecipes.get(pos).getPostid())){
                        likePost.remove(mRecipes.get(pos).getPostid());
                        likePost.put(mRecipes.get(pos).getPostid(), "0");
                    }
                    else{
                        likePost.put(mRecipes.get(pos).getPostid(), "0");
                    }

                    Retrofit retrofit = ApiClient.getClient();
                    ApiInterface apiService = retrofit.create(ApiInterface.class);

                    toast("Unliked " + mRecipes.get(getAdapterPosition()).getRecipename().toString());
                    int total = Integer.parseInt(mRecipes.get(getAdapterPosition()).getLikestotal().toString());
                    total--;

                    likesTotalPost.put(mRecipes.get(getAdapterPosition()).getPostid(), String.valueOf(total));

                    mRecipes.get(getAdapterPosition()).setLikestotal(String.valueOf(total));
                    mAdapter.notifyDataSetChanged();

                    Call<Void> call = apiService.unlikes(userid, String.valueOf(mRecipes.get(pos).getPostid()));
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

                    final int pos = getAdapterPosition();

                    if(favoritePost.containsKey(mRecipes.get(pos).getPostid())){
                        favoritePost.remove(mRecipes.get(pos).getPostid());
                        favoritePost.put(mRecipes.get(pos).getPostid(), "1");
                    }
                    else{
                        favoritePost.put(mRecipes.get(pos).getPostid(), "1");
                    }

                        mRecipes.get(getAdapterPosition()).setFavorites("1");


                        Retrofit retrofit = ApiClient.getClient();
                        ApiInterface apiService = retrofit.create(ApiInterface.class);

                        toast("Favorited " + mRecipes.get(getAdapterPosition()).getRecipename().toString());
                        mAdapter.notifyDataSetChanged();

                        Call<Void> call = apiService.favorites(userid, String.valueOf(mRecipes.get(pos).getPostid()));
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

                @Override
                public void unLiked(LikeButton likeButton) {
                    mRecipes.get(getAdapterPosition()).setFavorites("0");

                    final int pos = getAdapterPosition();

                    if(favoritePost.containsKey(mRecipes.get(pos).getPostid())){
                        favoritePost.remove(mRecipes.get(pos).getPostid());
                        favoritePost.put(mRecipes.get(pos).getPostid(), "0");
                    }
                    else{
                        favoritePost.put(mRecipes.get(pos).getPostid(), "0");
                    }

                    Retrofit retrofit = ApiClient.getClient();
                    ApiInterface apiService = retrofit.create(ApiInterface.class);

                    toast("Unfavorited " + mRecipes.get(getAdapterPosition()).getRecipename().toString());
                    mAdapter.notifyDataSetChanged();

                    Call<Void> call = apiService.unfavorites(userid, String.valueOf(mRecipes.get(pos).getPostid()));
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

            Glide.with(SelfProfileActivity.this).load(url).centerCrop().diskCacheStrategy(RESULT).into(cardImage);

            likesText.setText(likestext);

            Log.d("likes", likes);
            Log.d("Favorites", favorites);

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
            LayoutInflater inflater = LayoutInflater.from(SelfProfileActivity.this);
            View cardViewHolderView = inflater.inflate(R.layout.card_view_recycler_view, parent, false);
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
            mAlertDialog = new SpotsDialog(SelfProfileActivity.this);
            mAlertDialog.setCancelable(false);
        }

        mAlertDialog.show();
    }

    public void hideProgressDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    private void isFollowing(){
        Retrofit retrofit = ApiClient.getClient();
        ApiInterface apiService = retrofit.create(ApiInterface.class);

        Call<Object> call = apiService.isFollowing(userid, postuserid);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if(response.body() != null) {
                    if (response.body().toString().equals("1")) {
                        profileFollowButton.setLiked(true);
                        followsUser.put(postuserid, "1");
                    }
                    else {
                        profileFollowButton.setLiked(false);
                        followsUser.put(postuserid, "0");
                    }
                }
                else{
                    profileFollowButton.setLiked(false);
                    followsUser.put(postuserid, "0");
                }
                refreshUI();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d("Error", t.toString());
                refreshUI();
            }
        });
    }

    public void refreshUI(){
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mStaggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new CardAdapter(mRecipes);
        mRecyclerView.setAdapter(mAdapter);

        if(mRecipes.size() == 1)
            profileNumRecipes.setText(mRecipes.size() + " recipe");
        else
        profileNumRecipes.setText(mRecipes.size() + " recipes");

        hideProgressDialog();

    }

    public void toast(String toast){
        SuperActivityToast.create(SelfProfileActivity.this, new Style(), Style.TYPE_STANDARD)
                .setText(toast)
                .setDuration(Style.DURATION_VERY_SHORT)
                .setFrame(Style.FRAME_STANDARD)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_ORANGE))
                .setAnimations(Style.ANIMATIONS_FLY).show();
    }

    public void launchTest(String postid, String url, String recipename, String servings, String preptime, String cooktime, String likes, String favorites, String userid, String adapterpos, String likestotal, String username, String postuserid){
        Intent i = new Intent(SelfProfileActivity.this, DetailedRecipeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("postid", postid);
        bundle.putString("url", url);
        bundle.putString("recipename", recipename);
        bundle.putString("servings", servings);
        bundle.putString("preptime", preptime);
        bundle.putString("cooktime", cooktime);
        bundle.putString("likes", likes);
        bundle.putString("favorites", favorites);
        bundle.putString("userid", userid);
        bundle.putString("adapterpos", adapterpos);
        bundle.putString("likestotal", likestotal);
        bundle.putString("username", username);
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
        String loggedinuser = mSharedPreferences.getString("email", "");
        bundle.putString("loggedinuser", loggedinuser);
        bundle.putString("postuserid", postuserid);
        bundle.putSerializable("likePost", likePost);
        bundle.putSerializable("favoritePost", favoritePost);
        bundle.putSerializable("likesTotalPost", likesTotalPost);
        bundle.putSerializable("followsUser", followsUser);
        i.putExtras(bundle);
        startActivityForResult(i, 4);

    }




    @Override
    public void onBackPressed(){
        Intent data = new Intent();
        Bundle bundle = new Bundle();

        bundle.putSerializable("likePost", likePost);
        bundle.putSerializable("favoritePost", favoritePost);
        bundle.putSerializable("likesTotalPost", likesTotalPost);
        bundle.putSerializable("followsUser", followsUser);
        data.putExtras(bundle);
        setResult(RESULT_OK, data);
        super.onBackPressed();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if(requestCode == 4) {
            likePost = (HashMap) data.getSerializableExtra("likePost");
            favoritePost = (HashMap) data.getSerializableExtra("favoritePost");
            likesTotalPost = (HashMap) data.getSerializableExtra("likesTotalPost");
            followsUser = (HashMap) data.getSerializableExtra("followsUser");


            if(followsUser.containsKey(postuserid)) {
                if (followsUser.get(postuserid).equals("0")) {
                    profileFollowButton.setLiked(false);
                } else {
                    profileFollowButton.setLiked(true);
                }
            }

            for(int i = 0; i < mRecipes.size(); i++){
                if(likePost.containsKey(mRecipes.get(i).getPostid())){
                    mRecipes.get(i).setLikes((String) likePost.get(mRecipes.get(i).getPostid()));
                }
                if(favoritePost.containsKey(mRecipes.get(i).getPostid())){
                    mRecipes.get(i).setFavorites((String) favoritePost.get(mRecipes.get(i).getPostid()));
                }
                if(likesTotalPost.containsKey(mRecipes.get(i).getPostid())){
                    mRecipes.get(i).setLikestotal((String) likesTotalPost.get(mRecipes.get(i).getPostid()));
                }
            }
            mAdapter.notifyDataSetChanged();

        }

    }
}

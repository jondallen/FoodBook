package com.uncgcapstone.android.seniorcapstone.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.gson.Gson;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.uncgcapstone.android.seniorcapstone.io.ApiClient;
import com.uncgcapstone.android.seniorcapstone.io.ApiInterface;
import com.uncgcapstone.android.seniorcapstone.R;
import com.uncgcapstone.android.seniorcapstone.adapters.PagerAdapter;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetailedRecipeActivity extends AppCompatActivity{

    private String url_get_ingredients_and_steps = "http://63d42096.ngrok.io/android_connect/get_ingredients_and_steps.php";
    private String url_likes = "http://63d42096.ngrok.io/android_connect/likes.php";
    private String url_unlikes = "http://63d42096.ngrok.io/android_connect/unlikes.php";
    private String url_favorites = "http://63d42096.ngrok.io/android_connect/favorites.php";
    private String url_unfavorites = "http://63d42096.ngrok.io/android_connect/unfavorites.php";
    JSONArray ingredients = null;
    JSONArray steps = null;
    Gson gson = new Gson();
    public String url, recipename, servings, preptime, cooktime, likes, favorites, userid, adapterpos, username, loggedinuser, postuserid = "";
    int likestotal;
    TextView detailRecipeNameText, detailUsername;
    String postid;
    NestedScrollView testScrollView;
    ImageView backarrow; // detailStar, detailThumb;
    LikeButton detailStar, detailThumb;
    //Toolbar toolbar1;
    TextView detailBackButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_recipe);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //toolbar1 = (Toolbar) findViewById(R.id.toolbar1);
        /*toolbar1.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

        if(bundle != null){
            url = bundle.getString("url");
            recipename = bundle.getString("recipename");
            servings = bundle.getString("servings");
            preptime = bundle.getString("preptime");
            cooktime = bundle.getString("cooktime");
            postid = bundle.getString("postid");
            likes = bundle.getString("likes");
            favorites = bundle.getString("favorites");
            userid = bundle.getString("userid");
            adapterpos = bundle.getString("adapterpos");
            likestotal = Integer.parseInt(bundle.getString("likestotal"));
            username = bundle.getString("username");
            loggedinuser = bundle.getString("loggedinuser");
            postuserid = bundle.getString("postuserid");
        }


        detailBackButton = (TextView) findViewById(R.id.detailBackButton);
        detailBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        detailStar = (LikeButton) findViewById(R.id.starDetail);
        detailThumb = (LikeButton) findViewById(R.id.thumbDetail);

        if(likes.equals("1")){
            detailThumb.setLiked(true);
        }
        else{
            detailThumb.setLiked(false);
        }

        if(favorites.equals("1")){
            detailStar.setLiked(true);
        }
        else{
            detailStar.setLiked(false);
        }

        detailStar.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                int inte = Integer.parseInt(favorites);
                if (inte == 0) {
                    favorites = "1";

                    Retrofit retrofit = ApiClient.getClient();
                    ApiInterface apiService = retrofit.create(ApiInterface.class);

                    toast("Favorited " + recipename);

                    Call<Void> call = apiService.favorites(userid, postid);
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

                    favorites = "0";

                Retrofit retrofit = ApiClient.getClient();
                ApiInterface apiService = retrofit.create(ApiInterface.class);

                toast("Unfavorited " +recipename);

                Call<Void> call = apiService.unfavorites(userid, postid);
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

        detailThumb.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                int inte = Integer.parseInt(likes);
                if (inte == 0) {
                    likes = "1";
                    likestotal++;

                    Retrofit retrofit = ApiClient.getClient();
                    ApiInterface apiService = retrofit.create(ApiInterface.class);

                    toast("Liked " + recipename);

                    Call<Void> call = apiService.likes(userid, postid);
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
                int inte = Integer.parseInt(likes);
                if (inte == 1) {
                    likes = "0";
                    likestotal--;

                    Retrofit retrofit = ApiClient.getClient();
                    ApiInterface apiService = retrofit.create(ApiInterface.class);

                    toast("Unliked " + recipename);

                    Call<Void> call = apiService.unlikes(userid, postid);
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
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Recipe"));
        tabLayout.addTab(tabLayout.newTab().setText("Reviews"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        /**
         * Nutrition info stuff
         */

    }


    @Override
    public void onStop(){
        super.onStop();
        /**
         * The below code is used to allow memory to be GC'd correctly upon leaving the fragment
         * It may or may not be actually necessary
         */
        new Thread(new Runnable() {
            public void run() {
                Glide.get(DetailedRecipeActivity.this).clearDiskCache();
            }
        }).start();
        Glide.get(DetailedRecipeActivity.this).clearMemory();
        /**
         *
         */
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        SuperActivityToast.cancelAllSuperToasts();
    }


    @Override
    public void onBackPressed(){
        Intent data = new Intent();
        Bundle bundle = new Bundle();

        bundle.putString("adapterpos", adapterpos);
        bundle.putString("favorites", favorites);
        bundle.putString("likes", likes);
        bundle.putString("likestotal", String.valueOf(likestotal));
        data.putExtras(bundle);
        setResult(RESULT_OK, data);

        super.onBackPressed();
    }



    public void toast(String toast){
        SuperActivityToast.create(DetailedRecipeActivity.this, new Style(), Style.TYPE_STANDARD)
                .setText(toast)
                .setDuration(Style.DURATION_VERY_SHORT)
                .setFrame(Style.FRAME_STANDARD)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_ORANGE))
                .setAnimations(Style.ANIMATIONS_FLY).show();
    }

public String getServings(){
    return servings;
}
    public String getPreptime(){
        return preptime;
    }
    public String getCooktime(){
        return cooktime;
    }
    public String getPostid(){
        return postid;
    }
    public String getUserid(){
        return userid;
    }
    public String getRecipename(){
        return recipename;
    }
    public String getUrl(){
        return url;
    }
    public String getLoggedinuser(){
        return loggedinuser;
    }
    public String getPostuserid(){
        return postuserid;
    }
    public String getUsername(){
        return username;
    }


}

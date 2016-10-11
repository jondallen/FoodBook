package com.uncgcapstone.android.seniorcapstone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

public class DetailedRecipeActivity extends AppCompatActivity{

    private SwipeBackActivityHelper mHelper;
    String url, recipename, servings, preptime, cooktime, likes, favorites, userid, adapterpos = "";
    int likestotal;
    ImageView detailImage;
    TextView detailRecipeNameText, servesTextDetail, prepTextDetail, cookTextDetail;
    String postid;
    private String url_get_ingredients_and_steps = "http://3661590e.ngrok.io/android_connect/get_ingredients_and_steps.php";
    private String url_likes = "http://3661590e.ngrok.io/android_connect/likes.php";
    private String url_unlikes = "http://3661590e.ngrok.io/android_connect/unlikes.php";
    private String url_favorites = "http://3661590e.ngrok.io/android_connect/favorites.php";
    private String url_unfavorites = "http://3661590e.ngrok.io/android_connect/unfavorites.php";
    JSONParser jParser = new JSONParser();
    JSONArray ingredients = null;
    JSONArray steps = null;
    Gson gson = new Gson();
    Ingredients[] mIngredients;
    Steps[] mSteps;
    ProgressDialog mProgressDialog;
    RecyclerView ingredsRecyclerViewDetail, stepsRecyclerViewDetail;
    RecyclerView.Adapter mAdapter, mAdapter1;
    LinearLayoutManager mLinearLayoutManager, mLinearLayoutManager1;
    Toolbar mToolbar;
    NestedScrollView testScrollView;
    ImageView backarrow; // detailStar, detailThumb;
    LikeButton detailStar, detailThumb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();


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
        }
        ingredsRecyclerViewDetail = (RecyclerView) findViewById(R.id.ingredsRecyclerViewDetail);
        stepsRecyclerViewDetail = (RecyclerView) findViewById(R.id.stepsRecyclerViewDetail);

        detailImage = (ImageView) findViewById(R.id.detailImage);
        detailRecipeNameText = (TextView) findViewById(R.id.detailRecipeNameText);
        servesTextDetail = (TextView) findViewById(R.id.servesTextDetail);
        prepTextDetail = (TextView) findViewById(R.id.prepTextDetail);
        cookTextDetail = (TextView) findViewById(R.id.cookTextDetail);
        backarrow = (ImageView) findViewById(R.id.backarrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
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
                    toast("Favorited " + recipename);
                    favorites = "1";
                    //ImageView img = (ImageView) v;
                    //img.setImageResource(R.drawable.star_big_on);
                    new Thread() {
                        @Override
                        public void run() {
                            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                            params1.add(new BasicNameValuePair("userid", userid));
                            params1.add(new BasicNameValuePair("postid", postid));
                            JSONObject json = jParser.makeHttpRequest(url_favorites, "POST", params1);
                        }
                    }.start();
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                    toast("Unfavorited " + recipename);
                    favorites = "0";
                    //ImageView img = (ImageView) v;
                    //img.setImageResource(R.drawable.star_big);
                    new Thread() {
                        @Override
                        public void run() {
                            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                            params1.add(new BasicNameValuePair("userid", userid));
                            params1.add(new BasicNameValuePair("postid", postid));
                            JSONObject json = jParser.makeHttpRequest(url_unfavorites, "POST", params1);
                        }
                    }.start();
            }
        });

        detailThumb.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                int inte = Integer.parseInt(likes);
                if (inte == 0) {
                    toast("Liked " + recipename);
                    likes = "1";
                    likestotal++;
                    //ImageView img = (ImageView) v;
                    //img.setImageResource(R.drawable.thumb_up_big);
                    new Thread() {
                        @Override
                        public void run() {
                            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                            params1.add(new BasicNameValuePair("userid", userid));
                            params1.add(new BasicNameValuePair("postid", postid));
                            JSONObject json = jParser.makeHttpRequest(url_likes, "POST", params1);
                        }
                    }.start();
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                int inte = Integer.parseInt(likes);
                if (inte == 1) {
                    toast("Unliked " + recipename);
                    likes = "0";
                    likestotal--;
                    //ImageView img = (ImageView) v;
                    //img.setImageResource(R.drawable.thumb_big);
                    new Thread() {
                        @Override
                        public void run() {
                            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                            params1.add(new BasicNameValuePair("userid", userid));
                            params1.add(new BasicNameValuePair("postid", postid));
                            JSONObject json = jParser.makeHttpRequest(url_unlikes, "POST", params1);
                        }
                    }.start();
                }
            }
        });


        new LoadAllProducts().execute();
        showProgressDialog();


        //mHelper = new SwipeBackActivityHelper(this);
        //mHelper.onActivityCreate();

    }

    /*@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity(){
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }*/

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
        Log.d("1", "1");
        Type listType = new TypeToken<List<Recipe>>(){}.getType();
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();


        params.add(new BasicNameValuePair("postid", postid));

            JSONObject json1 = jParser.makeHttpRequest(url_get_ingredients_and_steps, "POST", params);

            try {

                if (json1.getJSONArray("Ingredients") != null) {

                    // products found
                    // Getting Array of Products
                    ingredients = json1.getJSONArray("Ingredients");
                    int count = ingredients.length();
                    mIngredients = new Ingredients[count];
                    // looping through All Products
                    for (int i = 0; i < ingredients.length(); i++) {
                        JSONObject c = ingredients.getJSONObject(i);
                        //Log.d(TAG, c.toString());
                        Ingredients ingredient = gson.fromJson(c.toString(), Ingredients.class);
                        mIngredients[i] = new Ingredients(ingredient.getQuantity(), ingredient.getUnit(), ingredient.getIngredient());
                    }
                }
                if (json1.getJSONArray("Steps") != null) {

                    // products found
                    // Getting Array of Products
                    steps = json1.getJSONArray("Steps");
                    int count = steps.length();
                    mSteps = new Steps[count];
                    // looping through All Products
                    for (int i = 0; i < steps.length(); i++) {
                        JSONObject c = steps.getJSONObject(i);
                        //Log.d(TAG, c.toString());
                        Steps step = gson.fromJson(c.toString(), Steps.class);
                        mSteps[i] = new Steps(step.getStep());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listType = null;
            params = null;
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

        mLinearLayoutManager = new LinearLayoutManager(DetailedRecipeActivity.this, LinearLayoutManager.VERTICAL, false);
        mLinearLayoutManager1 = new LinearLayoutManager(DetailedRecipeActivity.this, LinearLayoutManager.VERTICAL, false);

        ingredsRecyclerViewDetail.setLayoutManager(mLinearLayoutManager);
        ingredsRecyclerViewDetail.setHasFixedSize(true);
        mAdapter = new IngredAdapter(mIngredients);
        ingredsRecyclerViewDetail.setAdapter(mAdapter);
        ingredsRecyclerViewDetail.setNestedScrollingEnabled(false);
        ingredsRecyclerViewDetail.addItemDecoration(new HorizontalDividerItemDecoration.Builder(DetailedRecipeActivity.this).size(5).build());

        stepsRecyclerViewDetail.setLayoutManager(mLinearLayoutManager1);
        stepsRecyclerViewDetail.setHasFixedSize(true);
        mAdapter1 = new StepAdapter(mSteps);
        stepsRecyclerViewDetail.setAdapter(mAdapter1);
        stepsRecyclerViewDetail.setNestedScrollingEnabled(false);
        stepsRecyclerViewDetail.addItemDecoration(new HorizontalDividerItemDecoration.Builder(DetailedRecipeActivity.this).size(5).build());


        Glide.with(getApplicationContext()).load(url).centerCrop().into(detailImage);
        detailRecipeNameText.setText(recipename);
        servesTextDetail.setText("Serves " + servings);
        prepTextDetail.setText("Prep: " + preptime + "m");
        cookTextDetail.setText("Cook: " + cooktime + "m");
        hideProgressDialog();


        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if(mSharedPreferences.getString("firsttime", "0").equals("0")) {
            editor.putString("firsttime", "1");
            editor.commit();
            SuperActivityToast.create(DetailedRecipeActivity.this, new Style(), Style.TYPE_STANDARD)
                    //.setButtonText("Got it")
                    //.setButtonIconResource(R.drawable.check)
                    .setIndeterminate(true)
                    .setTouchToDismiss(true)
                    .setText("Hint: Swipe from the left to go back! (click to dismiss)")
                    //.setDuration(Style.DURATION_VERY_LONG)
                    .setFrame(Style.FRAME_STANDARD)
                    .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_ORANGE))
                    .setAnimations(Style.ANIMATIONS_FLY).
                    show();
        }

    }
}

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(DetailedRecipeActivity.this);
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

    private class IngredHolder extends RecyclerView.ViewHolder {
        private TextView recyclertext;
        private TextView recyclertext1;
        private TextView recyclertext2;
        public IngredHolder(View itemView){
            super(itemView);

            recyclertext = (TextView) itemView.findViewById(R.id.recyclertext);
            recyclertext1 = (TextView) itemView.findViewById(R.id.recyclertext1);
            recyclertext2 = (TextView) itemView.findViewById(R.id.recyclertext2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        public void bindCard(String quantity, String unit, String ingredient) {
            if(ingredient != null)
                if(quantity.equals("") || quantity.equals(" "))
                    recyclertext.setPadding(0,6,0,6);
            if(unit.equals("") || unit.equals(" "))
                recyclertext1.setPadding(0,6,0,6);
            recyclertext.setText(quantity.trim());
            recyclertext1.setText(unit.trim());
            recyclertext2.setText(ingredient.trim());

        }
    }


    private class IngredAdapter extends RecyclerView.Adapter<IngredHolder> {
        private Ingredients[] ingredz;

        public IngredAdapter(Ingredients[] s){
            ingredz = s;
        }

        @Override
        public IngredHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(DetailedRecipeActivity.this);
            View ingredHolderView = inflater.inflate(R.layout.ingredients_recyclerview, parent, false);
            return new IngredHolder(ingredHolderView);
        }

        @Override
        public void onBindViewHolder(IngredHolder holder, int position){
            String quantity = ingredz[position].getQuantity().toString();
            String unit = ingredz[position].getUnit().toString();
            String ingredient = ingredz[position].getIngredient().toString();
            holder.bindCard(quantity, unit, ingredient);
        }

        @Override
        public int getItemCount(){
            if(ingredz != null)
                return ingredz.length;
            return 0;
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private class StepHolder extends RecyclerView.ViewHolder {
        private TextView recyclertext3;
        private ImageView numbering;

        public StepHolder(View itemView){
            super(itemView);

            recyclertext3 = (TextView) itemView.findViewById(R.id.recyclertext3);
            numbering = (ImageView) itemView.findViewById(R.id.numbering);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        public void bindCard(String step) {
            if(step != null)
                recyclertext3.setText(step);

            switch(getAdapterPosition()){
                case 0: numbering.setImageResource(R.drawable.numeric_1_box_outline);
                    break;
                case 1: numbering.setImageResource(R.drawable.numeric_2_box_outline);
                    break;
                case 2: numbering.setImageResource(R.drawable.numeric_3_box_outline);
                    break;
                case 3: numbering.setImageResource(R.drawable.numeric_4_box_outline);
                    break;
                case 4: numbering.setImageResource(R.drawable.numeric_5_box_outline);
                    break;
                case 5: numbering.setImageResource(R.drawable.numeric_6_box_outline);
                    break;
                case 6: numbering.setImageResource(R.drawable.numeric_7_box_outline);
                    break;
                case 7: numbering.setImageResource(R.drawable.numeric_8_box_outline);
                    break;
                case 8: numbering.setImageResource(R.drawable.numeric_9_box_outline);
                    break;
                default:
                    break;
            }
        }
    }


    private class StepAdapter extends RecyclerView.Adapter<StepHolder> {
        private Steps[] stepz;

        public StepAdapter(Steps[] s){
            stepz = s;
        }

        @Override
        public StepHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(DetailedRecipeActivity.this);
            View stepHolderView = inflater.inflate(R.layout.steps_recyclerview_numbers, parent, false);
            return new StepHolder(stepHolderView);
        }

        @Override
        public void onBindViewHolder(StepHolder holder, int position){
            String step = stepz[position].getStep();
            holder.bindCard(step);
        }

        @Override
        public int getItemCount(){
            if(stepz != null)
                return stepz.length;
            return 0;
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }
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



}

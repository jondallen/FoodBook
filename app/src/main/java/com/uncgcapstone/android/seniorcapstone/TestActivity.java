package com.uncgcapstone.android.seniorcapstone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import junit.framework.Test;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

import static android.view.View.GONE;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.RESULT;

public class TestActivity extends AppCompatActivity implements SwipeBackActivityBase {

    private SwipeBackActivityHelper mHelper;
    String url, recipename, servings, preptime, cooktime = "";
    ImageView detailImage;
    TextView detailRecipeNameText, servesTextDetail, prepTextDetail, cookTextDetail;
    String postid;
    private String url_get_ingredients_and_steps = "http://3661590e.ngrok.io/android_connect/get_ingredients_and_steps.php";
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
        }
        ingredsRecyclerViewDetail = (RecyclerView) findViewById(R.id.ingredsRecyclerViewDetail);
        stepsRecyclerViewDetail = (RecyclerView) findViewById(R.id.stepsRecyclerViewDetail);

        detailImage = (ImageView) findViewById(R.id.detailImage);
        detailRecipeNameText = (TextView) findViewById(R.id.detailRecipeNameText);
        servesTextDetail = (TextView) findViewById(R.id.servesTextDetail);
        prepTextDetail = (TextView) findViewById(R.id.prepTextDetail);
        cookTextDetail = (TextView) findViewById(R.id.cookTextDetail);


        new LoadAllProducts().execute();
        showProgressDialog();


        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();




    }

    @Override
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
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d("OnStopCalled", "TestActivity");
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
        String[] ingreds = new String[mIngredients.length];
        for(int i = 0; i < mIngredients.length; i++){
            ingreds[i] = mIngredients[i].getQuantity().toString() + " " + mIngredients[i].getUnit().toString() + " " + mIngredients[i].getIngredient().toString();
        }

        mLinearLayoutManager = new LinearLayoutManager(TestActivity.this, LinearLayoutManager.VERTICAL, false);
        mLinearLayoutManager1 = new LinearLayoutManager(TestActivity.this, LinearLayoutManager.VERTICAL, false);

        ingredsRecyclerViewDetail.setLayoutManager(mLinearLayoutManager);
        ingredsRecyclerViewDetail.setHasFixedSize(true);
        mAdapter = new IngredAdapter(ingreds);
        ingredsRecyclerViewDetail.setAdapter(mAdapter);
        ingredsRecyclerViewDetail.setNestedScrollingEnabled(false);
        ingredsRecyclerViewDetail.addItemDecoration(new HorizontalDividerItemDecoration.Builder(TestActivity.this).size(5).build());

        stepsRecyclerViewDetail.setLayoutManager(mLinearLayoutManager1);
        stepsRecyclerViewDetail.setHasFixedSize(true);
        mAdapter1 = new StepAdapter(mSteps);
        stepsRecyclerViewDetail.setAdapter(mAdapter1);
        stepsRecyclerViewDetail.setNestedScrollingEnabled(false);
        stepsRecyclerViewDetail.addItemDecoration(new HorizontalDividerItemDecoration.Builder(TestActivity.this).size(5).build());


        Glide.with(getApplicationContext()).load(url).centerCrop().into(detailImage);
        detailRecipeNameText.setText(recipename);
        servesTextDetail.setText("Serves " + servings);
        prepTextDetail.setText("Prep: " + preptime + "m");
        cookTextDetail.setText("Cook: " + cooktime + "m");
        hideProgressDialog();

    }
    //});

    // }

}



    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(TestActivity.this);
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
        public IngredHolder(View itemView){
            super(itemView);

            recyclertext = (TextView) itemView.findViewById(R.id.recyclertext);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        public void bindCard(String ingredient) {
            if(ingredient != null)
            recyclertext.setText(ingredient);

        }
    }


    private class IngredAdapter extends RecyclerView.Adapter<IngredHolder> {
        private String[] ingredz;

        public IngredAdapter(String[] s){
            ingredz = s;
        }

        @Override
        public IngredHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(TestActivity.this);
            View ingredHolderView = inflater.inflate(R.layout.ingredients_recyclerview, parent, false);
            return new IngredHolder(ingredHolderView);
        }

        @Override
        public void onBindViewHolder(IngredHolder holder, int position){
            String ingredient = ingredz[position];
            holder.bindCard(ingredient);
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
        private TextView recyclertext1;
        public StepHolder(View itemView){
            super(itemView);

            recyclertext1 = (TextView) itemView.findViewById(R.id.recyclertext1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        public void bindCard(String step) {
            if(step != null)
                recyclertext1.setText(step);

        }
    }


    private class StepAdapter extends RecyclerView.Adapter<StepHolder> {
        private Steps[] stepz;

        public StepAdapter(Steps[] s){
            stepz = s;
        }

        @Override
        public StepHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(TestActivity.this);
            View stepHolderView = inflater.inflate(R.layout.steps_recyclerview, parent, false);
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



}

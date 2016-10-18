package com.uncgcapstone.android.seniorcapstone.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uncgcapstone.android.seniorcapstone.activities.DetailedRecipeActivity;
import com.uncgcapstone.android.seniorcapstone.data.Ingredients;
import com.uncgcapstone.android.seniorcapstone.io.JSONParser;
import com.uncgcapstone.android.seniorcapstone.R;
import com.uncgcapstone.android.seniorcapstone.data.Recipe;
import com.uncgcapstone.android.seniorcapstone.data.Steps;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailRecipeFragment extends Fragment {

    String  servings, preptime, cooktime, postid, userid, recipename, url, username;

    RecyclerView ingredsRecyclerViewDetail, stepsRecyclerViewDetail;
    TextView servesTextDetail, prepTextDetail, cookTextDetail;
    private String url_get_ingredients_and_steps = "http://63d42096.ngrok.io/android_connect/get_ingredients_and_steps.php";
    private String url_likes = "http://63d42096.ngrok.io/android_connect/likes.php";
    private String url_unlikes = "http://63d42096.ngrok.io/android_connect/unlikes.php";
    private String url_favorites = "http://63d42096.ngrok.io/android_connect/favorites.php";
    private String url_unfavorites = "http://63d42096.ngrok.io/android_connect/unfavorites.php";
    JSONParser jParser = new JSONParser();
    JSONArray ingredients = null;
    JSONArray steps = null;
    Gson gson = new Gson();
    Ingredients[] mIngredients;
    Steps[] mSteps;
    AlertDialog mAlertDialog;
    RecyclerView.Adapter mAdapter, mAdapter1;
    LinearLayoutManager mLinearLayoutManager, mLinearLayoutManager1;
    ImageView detailImage;
    TextView detailRecipeNameText, detailUsername;


    public DetailRecipeFragment() {
        // Required empty public constructor
    }

    public static DetailRecipeFragment newInstance() {
        DetailRecipeFragment fragment = new DetailRecipeFragment();
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
        View v =  inflater.inflate(R.layout.fragment_detail_recipe, container, false);

        ingredsRecyclerViewDetail = (RecyclerView) v.findViewById(R.id.ingredsRecyclerViewDetail);
        stepsRecyclerViewDetail = (RecyclerView) v.findViewById(R.id.stepsRecyclerViewDetail);

        servesTextDetail = (TextView) v.findViewById(R.id.servesTextDetail);
        prepTextDetail = (TextView) v.findViewById(R.id.prepTextDetail);
        cookTextDetail = (TextView) v.findViewById(R.id.cookTextDetail);

        servings = ((DetailedRecipeActivity)getActivity()).getServings();
        preptime = ((DetailedRecipeActivity)getActivity()).getPreptime();
        cooktime = ((DetailedRecipeActivity)getActivity()).getCooktime();
        postid = ((DetailedRecipeActivity)getActivity()).getPostid();
        userid = ((DetailedRecipeActivity)getActivity()).getUserid();
        recipename = ((DetailedRecipeActivity)getActivity()).getRecipename();
        url = ((DetailedRecipeActivity)getActivity()).getUrl();
        username = ((DetailedRecipeActivity)getActivity()).getUsername();


        detailImage = (ImageView) v.findViewById(R.id.detailImage);
        Glide.with(getContext()).load(url).centerCrop().into(detailImage);

        detailRecipeNameText = (TextView) v.findViewById(R.id.detailRecipeNameText);
        detailRecipeNameText.setText(recipename);

        detailUsername = (TextView) v.findViewById(R.id.detailUsername);
        detailUsername.setText("Added by " + username);

        new LoadAllProducts().execute();
        showProgressDialog();
        return v;
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

            mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            mLinearLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

            ingredsRecyclerViewDetail.setLayoutManager(mLinearLayoutManager);
            ingredsRecyclerViewDetail.setHasFixedSize(true);
            mAdapter = new IngredAdapter(mIngredients);
            ingredsRecyclerViewDetail.setAdapter(mAdapter);
            ingredsRecyclerViewDetail.setNestedScrollingEnabled(false);
            ingredsRecyclerViewDetail.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).size(5).build());

            stepsRecyclerViewDetail.setLayoutManager(mLinearLayoutManager1);
            stepsRecyclerViewDetail.setHasFixedSize(true);
            mAdapter1 = new StepAdapter(mSteps);
            stepsRecyclerViewDetail.setAdapter(mAdapter1);
            stepsRecyclerViewDetail.setNestedScrollingEnabled(false);
            stepsRecyclerViewDetail.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).size(5).build());



            servesTextDetail.setText("Serves " + servings);
            prepTextDetail.setText("Prep: " + preptime + "m");
            cookTextDetail.setText("Cook: " + cooktime + "m");
            hideProgressDialog();

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
            LayoutInflater inflater = LayoutInflater.from(getContext());
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
            LayoutInflater inflater = LayoutInflater.from(getContext());
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

}

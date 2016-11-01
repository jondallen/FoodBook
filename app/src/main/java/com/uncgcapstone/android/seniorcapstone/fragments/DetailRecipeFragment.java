package com.uncgcapstone.android.seniorcapstone.fragments;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.gson.Gson;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.uncgcapstone.android.seniorcapstone.activities.DetailedRecipeActivity;
import com.uncgcapstone.android.seniorcapstone.activities.MainActivity;
import com.uncgcapstone.android.seniorcapstone.activities.SelfProfileActivity;
import com.uncgcapstone.android.seniorcapstone.data.Ingredient;
import com.uncgcapstone.android.seniorcapstone.data.Ingredients;
import com.uncgcapstone.android.seniorcapstone.data.Step;
import com.uncgcapstone.android.seniorcapstone.data.Steps;
import com.uncgcapstone.android.seniorcapstone.data.Url;
import com.uncgcapstone.android.seniorcapstone.data.User;
import com.uncgcapstone.android.seniorcapstone.io.ApiClient;
import com.uncgcapstone.android.seniorcapstone.io.ApiInterface;
import com.uncgcapstone.android.seniorcapstone.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.view.View.GONE;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.RESULT;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailRecipeFragment extends Fragment {

    String  servings, preptime, cooktime, postid, userid, recipename, url, username, postuserid = "";

    RecyclerView ingredsRecyclerViewDetail, stepsRecyclerViewDetail;
    TextView servesTextDetail, prepTextDetail, cookTextDetail;
    private String url_get_ingredients_and_steps = "http://63d42096.ngrok.io/android_connect/get_ingredients_and_steps.php";
    private String url_get_ingredients = "http://63d42096.ngrok.io/android_connect_retro/get_ingredients.php";
    private String url_get_steps = "http://63d42096.ngrok.io/android_connect_retro/get_steps.php";
    private String url_likes = "http://63d42096.ngrok.io/android_connect/likes.php";
    private String url_unlikes = "http://63d42096.ngrok.io/android_connect/unlikes.php";
    private String url_favorites = "http://63d42096.ngrok.io/android_connect/favorites.php";
    private String url_unfavorites = "http://63d42096.ngrok.io/android_connect/unfavorites.php";
    JSONArray ingredients = null;
    JSONArray steps = null;
    Gson gson = new Gson();
    List<Ingredient> mIngredients;
    List<Step> mSteps;
    AlertDialog mAlertDialog;
    RecyclerView.Adapter mAdapter, mAdapter1;
    LinearLayoutManager mLinearLayoutManager, mLinearLayoutManager1;
    ImageView detailImage;
    TextView detailRecipeNameText, detailUsername;
    CircleImageView uploaderIcon;
    String imageUrl = "";
    List<User> mUser;
    RelativeLayout relLayoutProfile;




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
        postuserid = ((DetailedRecipeActivity)getActivity()).getPostuserid();



        detailImage = (ImageView) v.findViewById(R.id.detailImage);
        Glide.with(getContext()).load(url).centerCrop().into(detailImage);

        detailRecipeNameText = (TextView) v.findViewById(R.id.detailRecipeNameText);
        detailRecipeNameText.setText(recipename);

        detailUsername = (TextView) v.findViewById(R.id.detailUsername);
        detailUsername.setText(username);

        uploaderIcon = (CircleImageView) v.findViewById(R.id.uploaderIcon);

        relLayoutProfile = (RelativeLayout) v.findViewById(R.id.relLayoutProfile);
        relLayoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                if(imageUrl.length() > 0)
                extras.putString("url", imageUrl);
                else
                    extras.putString("url", "");
                extras.putString("username", username);
                extras.putString("postuserid", ((DetailedRecipeActivity)getActivity()).getPostuserid());
                extras.putString("userid", userid);
                ((DetailedRecipeActivity)getActivity()).showUserProfile(extras);
            }
        });


        showProgressDialog();
        getIngredients();
        return v;
    }

    @Override
    public void onStop(){
        super.onStop();

    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        SuperActivityToast.cancelAllSuperToasts();
    }

    /*public void getInfo(){
        Log.d("get info", "called");
        Retrofit retrofit = ApiClient.getClient();
        ApiInterface apiService = retrofit.create(ApiInterface.class);

        Call<Void> call = apiService.getInfo("2 eggs");
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("We in ", "there");
                Log.d("Response:", String.valueOf(response.body()));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Error", t.toString());
            }
        });
    }*/

    private void getIngredients(){
        Retrofit retrofit = ApiClient.getClient();
        ApiInterface apiService = retrofit.create(ApiInterface.class);

        Call<Ingredients> call = apiService.getIngredients(((DetailedRecipeActivity)getActivity()).getPostid());
        call.enqueue(new Callback<Ingredients>() {
            @Override
            public void onResponse(Call<Ingredients> call, Response<Ingredients> response) {
                if (response.body() != null) {
                    mIngredients = response.body().getIngredients();
                    getSteps();
                }
            }

            @Override
            public void onFailure(Call<Ingredients> call, Throwable t) {
                Log.d("DetailRecipeFrag Ingr", t.toString());
            }
        });

    }

    private void getSteps(){
        Retrofit retrofit = ApiClient.getClient();
        ApiInterface apiService = retrofit.create(ApiInterface.class);

        Call<Steps> call = apiService.getSteps(((DetailedRecipeActivity)getActivity()).getPostid());
        call.enqueue(new Callback<Steps>() {
            @Override
            public void onResponse(Call<Steps> call, Response<Steps> response) {
                if (response.body() != null) {
                    mSteps = response.body().getSteps();
                    getUploader();
                }
            }

            @Override
            public void onFailure(Call<Steps> call, Throwable t) {
                Log.d("DetailRecipeFrag Steps", t.toString());
            }
        });

    }

    private void getUploader(){
            Retrofit retrofit = ApiClient.getClient();
            ApiInterface apiService = retrofit.create(ApiInterface.class);

            Call<Url> call = apiService.getProfileImage(((DetailedRecipeActivity)getActivity()).getPostuserid());
            call.enqueue(new Callback<Url>() {
                @Override
                public void onResponse(Call<Url> call, Response<Url> response) {
                    if (response.body().getUser() != null) {
                        mUser = response.body().getUser();
                        if(mUser.size() > 0) {
                            imageUrl = mUser.get(0).getUrl().toString();
                        }
                        else{
                            imageUrl = "";
                        }
                    }
                    refreshUI();
                }

                @Override
                public void onFailure(Call<Url> call, Throwable t) {
                    Log.d("Error", t.toString());
                    imageUrl = "";
                    refreshUI();
                }
            });

    }


    public void refreshUI() {
        Glide.with(getContext()).load(imageUrl).diskCacheStrategy(RESULT).placeholder(R.drawable.person).dontAnimate().into(uploaderIcon);

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
        private List<Ingredient> ingredz;

        public IngredAdapter(List<Ingredient> s){
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
            String quantity = ingredz.get(position).getQuantity().toString();
            String unit = ingredz.get(position).getUnit().toString();
            String ingredient = ingredz.get(position).getIngredient().toString();
            holder.bindCard(quantity, unit, ingredient);
        }

        @Override
        public int getItemCount(){
            if(ingredz != null)
                return ingredz.size();
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
        private List<Step> stepz;

        public StepAdapter(List<Step> s){
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
            String step = stepz.get(position).getStep();
            holder.bindCard(step);
        }

        @Override
        public int getItemCount(){
            if(stepz != null)
                return stepz.size();
            return 0;
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    public void toast(String toast){
        SuperActivityToast.create(getActivity(), new Style(), Style.TYPE_STANDARD)
                .setText(toast)
                .setDuration(Style.DURATION_VERY_SHORT)
                .setFrame(Style.FRAME_STANDARD)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_ORANGE))
                .setAnimations(Style.ANIMATIONS_FLY).show();
    }

}

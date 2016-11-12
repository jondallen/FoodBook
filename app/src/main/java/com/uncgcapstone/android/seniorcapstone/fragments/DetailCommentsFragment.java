package com.uncgcapstone.android.seniorcapstone.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.gson.Gson;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.uncgcapstone.android.seniorcapstone.R;
import com.uncgcapstone.android.seniorcapstone.activities.DetailedRecipeActivity;
import com.uncgcapstone.android.seniorcapstone.activities.SelfProfileActivity;
import com.uncgcapstone.android.seniorcapstone.data.Review;
import com.uncgcapstone.android.seniorcapstone.data.Reviews;
import com.uncgcapstone.android.seniorcapstone.io.ApiClient;
import com.uncgcapstone.android.seniorcapstone.io.ApiInterface;

import org.json.JSONArray;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.RESULT;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailCommentsFragment extends Fragment {
    List<Review> mReviews;
    RecyclerView commentsRecyclerViewDetail;
    RecyclerView.Adapter mAdapter;
    LinearLayoutManager mLinearLayoutManager;
    TextView emptyText, commentsTitle, avgRatingText;
    android.app.AlertDialog mAlertDialog;
    FloatingActionButton commentsFAB;
    SimpleRatingBar avgRatingBar;
    TextView numRatingText;
    String mAvg, mCount = "";
    LinearLayout commentsRelLayout;


    public DetailCommentsFragment() {
        // Required empty public constructor
    }

    public static DetailCommentsFragment newInstance() {
        DetailCommentsFragment fragment = new DetailCommentsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detail_comments, container, false);

        mReviews = new ArrayList<>();
        commentsRecyclerViewDetail = (RecyclerView) v.findViewById(R.id.commentsRecyclerViewDetail);

        avgRatingBar = (SimpleRatingBar) v.findViewById(R.id.avgRatingBar);
        numRatingText = (TextView) v.findViewById(R.id.numRatingText);
        avgRatingText = (TextView) v.findViewById(R.id.avgRatingText);

        commentsRelLayout = (LinearLayout) v.findViewById(R.id.commentsRelLayout);
        commentsRelLayout.setVisibility(GONE);

        emptyText = (TextView) v.findViewById(R.id.emptyText);
        emptyText.setVisibility(GONE);

        commentsTitle = (TextView) v.findViewById(R.id.commentsTitle);

      commentsFAB = (FloatingActionButton) v.findViewById(R.id.commentsFAB);
        commentsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((DetailedRecipeActivity)getActivity()).getUserid().equals(((DetailedRecipeActivity)getActivity()).getPostuserid())){
                    toast("You can't review your own recipe!");
                }
                else {
                    LayoutInflater inflates = getActivity().getLayoutInflater();
                    final View dialogview = inflates.inflate(R.layout.review_dialog, null);
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Review " + ((DetailedRecipeActivity) getActivity()).getRecipename())
                            .setView(dialogview)
                            .setPositiveButton("Submit",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            EditText text = (EditText) dialogview.findViewById(R.id.dialogText);
                                            SimpleRatingBar bar = (SimpleRatingBar) dialogview.findViewById(R.id.dialogRating);

                                            String review = text.getText().toString();
                                            String rating = String.valueOf(bar.getRating());

                                            if (review.equals("")) {
                                                toast("You must leave an actual review!");
                                            } else {
                                                showProgressDialog();
                                                createReview(review, rating);
                                                dialog.dismiss();
                                            }


                                        }
                                    }
                            )
                            .setNegativeButton(android.R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }
                            )
                            .create().show();
                }
            }
        });
        //showProgressDialog();


        getAllReviews();
        return v;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        SuperActivityToast.cancelAllSuperToasts();
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


    private class CommentsHolder extends RecyclerView.ViewHolder {
        TextView commentsUsername, commentsComment, commentsDateTime;
        SimpleRatingBar commentsRating;
        CircleImageView commentsIcon;
        RelativeLayout relLayoutCommentsProfile;


        public CommentsHolder(View itemView) {
            super(itemView);

            commentsUsername = (TextView) itemView.findViewById(R.id.commentsUsername);
            commentsComment = (TextView) itemView.findViewById(R.id.commentsComment);
            commentsRating = (SimpleRatingBar) itemView.findViewById(R.id.commentsRating);
            commentsDateTime = (TextView) itemView.findViewById(R.id.commentsDateTime);
            commentsIcon = (CircleImageView) itemView.findViewById(R.id.commentsIcon);
            relLayoutCommentsProfile = (RelativeLayout) itemView.findViewById(R.id.relLayoutCommentsProfile);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            relLayoutCommentsProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //((DetailedRecipeActivity)getActivity()).showUserProfile();

                    Bundle extras = new Bundle();
                    if(mReviews.get(getAdapterPosition()).getUrl().length() > 0){
                        extras.putString("url", mReviews.get(getAdapterPosition()).getUrl());
                    }

                    else
                        extras.putString("url", "");
                    extras.putString("username", mReviews.get(getAdapterPosition()).getUsername());
                    extras.putString("postuserid", mReviews.get(getAdapterPosition()).getUserid());
                    extras.putString("userid", ((DetailedRecipeActivity)getActivity()).getUserid());
                    ((DetailedRecipeActivity) getActivity()).showUserProfile(extras);
                }
            });

        }

        public void bindCard(String username, String comment, String datetime, String rating, String url) {
            commentsUsername.setText(username);
            commentsComment.setText(comment);
            commentsDateTime.setText(datetime);
            commentsRating.setRating(Float.parseFloat(rating));
            if(url.length() > 0){
                Glide.with(getContext()).load(url).centerCrop().placeholder(R.drawable.person).dontAnimate().diskCacheStrategy(RESULT).into(commentsIcon);
            }


        }
    }


    private class CommentsAdapter extends RecyclerView.Adapter<CommentsHolder> {
        private List<Review> commentz;

        public CommentsAdapter(List<Review> s) {
            commentz = s;
        }

        @Override
        public CommentsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View commentsHolderView = inflater.inflate(R.layout.comments_recyclerview, parent, false);
            return new CommentsHolder(commentsHolderView);
        }

        @Override
        public void onBindViewHolder(CommentsHolder holder, int position) {
            String username = commentz.get(position).getUsername();
            String comment = commentz.get(position).getReview();
            String datetime = commentz.get(position).getDatetime();
            String rating = commentz.get(position).getRating();
            String url = commentz.get(position).getUrl();
            holder.bindCard(username, comment, datetime, rating, url);
        }

        @Override
        public int getItemCount() {
            if (commentz != null)
                return commentz.size();
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    public void createReview(String review, String rating){
        Retrofit retrofit = ApiClient.getClient();
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Calendar calendar = Calendar.getInstance();
        String datetime = new SimpleDateFormat("MM/dd/yyyy , hh:mm a").format(calendar.getTime());

        Call<Void> call = apiService.createReview(((DetailedRecipeActivity)getActivity()).getUserid(), ((DetailedRecipeActivity)getActivity()).getPostid(), review, rating, ((DetailedRecipeActivity)getActivity()).getLoggedinuser(), datetime);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                getAllReviews();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Error", t.toString());
                getAllReviews();
            }
        });
    }

    public void getAllReviews(){
        Retrofit retrofit = ApiClient.getClient();
        ApiInterface apiService = retrofit.create(ApiInterface.class);


        Call<Reviews> call = apiService.getAllReviews(((DetailedRecipeActivity)getActivity()).getPostid());
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                if (response.body().getReviews().size() > 0) {
                    mReviews = response.body().getReviews();
                    mAvg = String.valueOf(response.body().getAverage());
                    mCount = String.valueOf(response.body().getCount());
                    refreshUI();
                }
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                Log.d("DetailComm getallRev", t.toString());
                refreshUI();
            }
        });
    }

    public void refreshUI() {
        hideProgressDialog();
        if(mReviews.size() == 0){
            commentsRecyclerViewDetail.setVisibility(GONE);
            emptyText.setVisibility(VISIBLE);
            hideProgressDialog();
        }
        else {
            emptyText.setVisibility(GONE);
            commentsRelLayout.setVisibility(VISIBLE);

            avgRatingBar.setRating(Float.parseFloat(mAvg));
            numRatingText.setText("Number of reviews: " + mCount);

            avgRatingText.setText("Average Review: ");

            commentsTitle.setText(((DetailedRecipeActivity)getActivity()).getRecipename());

            commentsRecyclerViewDetail.setVisibility(VISIBLE);

            mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            commentsRecyclerViewDetail.setLayoutManager(mLinearLayoutManager);
            commentsRecyclerViewDetail.setHasFixedSize(true);
            mAdapter = new CommentsAdapter(mReviews);
            commentsRecyclerViewDetail.setAdapter(mAdapter);
            //hideProgressDialog();
        }
    }


    public void toast(String toast) {
        SuperActivityToast.create(getContext(), new Style(), Style.TYPE_STANDARD)
                .setText(toast)
                .setDuration(Style.DURATION_VERY_SHORT)
                .setFrame(Style.FRAME_STANDARD)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_ORANGE))
                .setAnimations(Style.ANIMATIONS_FLY).show();
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
}

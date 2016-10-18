package com.uncgcapstone.android.seniorcapstone.fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.uncgcapstone.android.seniorcapstone.R;
import com.uncgcapstone.android.seniorcapstone.activities.DetailedRecipeActivity;
import com.uncgcapstone.android.seniorcapstone.data.Comments;
import com.uncgcapstone.android.seniorcapstone.data.Ingredients;
import com.uncgcapstone.android.seniorcapstone.data.Recipe;
import com.uncgcapstone.android.seniorcapstone.data.Steps;
import com.uncgcapstone.android.seniorcapstone.io.JSONParser;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.RESULT;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailCommentsFragment extends Fragment {


    JSONParser jParser = new JSONParser();
    JSONArray ingredients = null;
    JSONArray steps = null;
    Gson gson = new Gson();
    private String url_create_review = "http://63d42096.ngrok.io/android_connect/create_review.php";
    private String url_get_all_reviews = "http://63d42096.ngrok.io/android_connect/get_all_reviews.php";


    List<Comments> mComments;
    RecyclerView commentsRecyclerViewDetail;
    RecyclerView.Adapter mAdapter;
    LinearLayoutManager mLinearLayoutManager;
    TextView emptyText, commentsTitle, avgRatingText;
    android.app.AlertDialog mAlertDialog;
    JSONArray comments = null;
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

        mComments = new ArrayList<>();
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
                Log.d("Click", "Recognized");
                LayoutInflater inflates = getActivity().getLayoutInflater();
                final View dialogview = inflates.inflate(R.layout.review_dialog, null);
               new AlertDialog.Builder(getActivity())
                        .setTitle("Review " + ((DetailedRecipeActivity)getActivity()).getRecipename())
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
                                        }
                                        else{
                                            showProgressDialog();
                                           new CreateReview().execute(review, rating);
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
        });
        showProgressDialog();
        new GetAllReviews().execute();
        return v;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
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

        public CommentsHolder(View itemView) {
            super(itemView);

            commentsUsername = (TextView) itemView.findViewById(R.id.commentsUsername);
            commentsComment = (TextView) itemView.findViewById(R.id.commentsComment);
            commentsRating = (SimpleRatingBar) itemView.findViewById(R.id.commentsRating);
            commentsDateTime = (TextView) itemView.findViewById(R.id.commentsDateTime);
            commentsIcon = (CircleImageView) itemView.findViewById(R.id.commentsIcon);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

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
        private List<Comments> commentz;

        public CommentsAdapter(List<Comments> s) {
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

        /**
         * Background Async Task to Load all product by making HTTP Request
         * */
       public class CreateReview extends AsyncTask<String, String, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            protected String doInBackground(String... args) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                Calendar calendar = Calendar.getInstance();
                String datetime = new SimpleDateFormat("MM/dd/yyyy , hh:mm a").format(calendar.getTime());

                params.add(new BasicNameValuePair("userid", ((DetailedRecipeActivity)getActivity()).getUserid()));
                params.add(new BasicNameValuePair("postid", ((DetailedRecipeActivity)getActivity()).getPostid()));
                params.add(new BasicNameValuePair("review", args[0]));
                params.add(new BasicNameValuePair("rating", args[1]));
                params.add(new BasicNameValuePair("username", ((DetailedRecipeActivity)getActivity()).getUsername()));
                params.add(new BasicNameValuePair("datetime", datetime));


                JSONObject json = jParser.makeHttpRequest(url_create_review, "POST", params);

                try {
                    int success = json.getInt("success");

                    if (success == 1) {

                    } else {
                        toast("Review failed!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } ;
                params = null;
                return null;
            }

            protected void onPostExecute(String file_url) {
            new GetAllReviews().execute();
            }

        }


    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class GetAllReviews extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            JSONObject json;

                    List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                    params1.add(new BasicNameValuePair("postid", ((DetailedRecipeActivity)getActivity()).getPostid()));
                    json = jParser.makeHttpRequest(url_get_all_reviews, "POST", params1);


                try {

                    // Checking for SUCCESS TAG
                    int success = json.getInt("success");

                    if (success == 1) {
                        comments = json.getJSONArray("Reviews");
                        int count = comments.length();
                        mComments = new ArrayList<>();
                        // looping through All Products
                        for (int i = 0; i < comments.length(); i++) {
                            JSONObject c = comments.getJSONObject(i);
                            //Log.d(TAG, c.toString());
                            Comments comment = gson.fromJson(c.toString(), Comments.class);
                            mComments.add(new Comments(comment.getUserid(), comment.getPostid(), comment.getReview(), comment.getRating(), comment.getUsername(), comment.getDatetime(), comment.getUrl()));
                        }

                        mAvg = json.getString("average");
                        mCount = json.getString("count");

                    } else {
                        // no products found

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params = null;

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            if(mComments.size() == 0){
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

                commentsTitle.setText("Reviews for  " + ((DetailedRecipeActivity)getActivity()).getRecipename());

                commentsRecyclerViewDetail.setVisibility(VISIBLE);

                mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                commentsRecyclerViewDetail.setLayoutManager(mLinearLayoutManager);
                commentsRecyclerViewDetail.setHasFixedSize(true);
                mAdapter = new CommentsAdapter(mComments);
                commentsRecyclerViewDetail.setAdapter(mAdapter);
                hideProgressDialog();
            }
        }
        //});

        // }

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

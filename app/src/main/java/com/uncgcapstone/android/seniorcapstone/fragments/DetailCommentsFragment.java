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
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import static android.view.View.GONE;


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
    TextView emptyText;
    ProgressDialog mProgressDialog;
    JSONArray comments = null;
    FloatingActionButton commentsFAB;


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

        emptyText = (TextView) v.findViewById(R.id.emptyText);
        emptyText.setVisibility(GONE);

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

        new GetAllReviews().execute();
        return v;
    }


    private class CommentsHolder extends RecyclerView.ViewHolder {
        TextView commentsUsername, commentsComment, commentsDateTime;
        SimpleRatingBar commentsRating;

        public CommentsHolder(View itemView) {
            super(itemView);

            commentsUsername = (TextView) itemView.findViewById(R.id.commentsUsername);
            commentsComment = (TextView) itemView.findViewById(R.id.commentsComment);
            commentsRating = (SimpleRatingBar) itemView.findViewById(R.id.commentsRating);
            commentsDateTime = (TextView) itemView.findViewById(R.id.commentsDateTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        public void bindCard(String username, String comment, String datetime, String rating) {
            commentsUsername.setText(username);
            commentsComment.setText(comment);
            commentsDateTime.setText("Submitted " + datetime);
            commentsRating.setRating(Float.parseFloat(rating));

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
            holder.bindCard(username, comment, datetime, rating);
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
                            mComments.add(new Comments(comment.getUserid(), comment.getPostid(), comment.getReview(), comment.getRating(), comment.getUsername(), comment.getDatetime()));
                        }

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
                emptyText.setVisibility(View.VISIBLE);
                hideProgressDialog();
            }
            else {
                emptyText.setVisibility(GONE);
                commentsRecyclerViewDetail.setVisibility(View.VISIBLE);

                mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                commentsRecyclerViewDetail.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).size(5).build());
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
}

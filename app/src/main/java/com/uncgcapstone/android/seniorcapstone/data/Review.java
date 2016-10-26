package com.uncgcapstone.android.seniorcapstone.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Review {

    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("postid")
    @Expose
    private String postid;
    @SerializedName("review")
    @Expose
    private String review;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("datetime")
    @Expose
    private String datetime;
    @SerializedName("url")
    @Expose
    private String url;

    /**
     * No args constructor for use in serialization
     *
     */
    public Review() {
    }

    /**
     *
     * @param postid
     * @param username
     * @param userid
     * @param rating
     * @param datetime
     * @param url
     * @param review
     */
    public Review(String userid, String postid, String review, String rating, String username, String datetime, String url) {
        this.userid = userid;
        this.postid = postid;
        this.review = review;
        this.rating = rating;
        this.username = username;
        this.datetime = datetime;
        this.url = url;
    }

    /**
     *
     * @return
     * The userid
     */
    public String getUserid() {
        return userid;
    }

    /**
     *
     * @param userid
     * The userid
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     *
     * @return
     * The postid
     */
    public String getPostid() {
        return postid;
    }

    /**
     *
     * @param postid
     * The postid
     */
    public void setPostid(String postid) {
        this.postid = postid;
    }

    /**
     *
     * @return
     * The review
     */
    public String getReview() {
        return review;
    }

    /**
     *
     * @param review
     * The review
     */
    public void setReview(String review) {
        this.review = review;
    }

    /**
     *
     * @return
     * The rating
     */
    public String getRating() {
        return rating;
    }

    /**
     *
     * @param rating
     * The rating
     */
    public void setRating(String rating) {
        this.rating = rating;
    }

    /**
     *
     * @return
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     * The datetime
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     *
     * @param datetime
     * The datetime
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
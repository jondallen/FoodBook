package com.uncgcapstone.android.seniorcapstone.data;

/**
 * Created by jon on 10/15/2016.
 */

public class Comments {
    String userid, postid, review, rating, username, datetime, url;

    public Comments(){

    }
    public Comments(String userid, String postid, String review, String rating, String username, String datetime, String url){
        this.userid = userid;
        this.postid = postid;
        this.review = review;
        this.rating = rating;
        this.username = username;
        this.datetime = datetime;
        this.url = url;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}

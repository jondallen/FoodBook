package com.uncgcapstone.android.seniorcapstone;

/**
 * Created by jon on 9/17/2016.
 */
public class Recipe {
    String uid;
    String username;
    String recipename;
    String url;
    int postid;

    public Recipe(){

    }

    public Recipe(int postid, String uid, String username, String recipename, String url){
        this.postid = postid;
        this.uid = uid;
        this.username = username;
        this.recipename = recipename;
        this.url = url;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRecipename() {
        return recipename;
    }

    public void setRecipename(String recipename) {
        this.recipename = recipename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPostId() {
        return postid;
    }

    public void setPostId(int postid) {
        this.postid = postid;
    }
}

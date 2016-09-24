package com.uncgcapstone.android.seniorcapstone;

/**
 * Created by jon on 9/17/2016.
 */
public class Recipe {
    int postid;
    String uid;
    String username;
    String recipename;
    String url;
    String datetime;
    String preptime;
    String cooktime;
    String serves;

    public Recipe(){

    }

    public Recipe(int postid, String uid, String username, String recipename, String url, String datetime, String preptime, String cooktime, String serves){
        this.postid = postid;
        this.uid = uid;
        this.username = username;
        this.recipename = recipename;
        this.url = url;
        this.datetime = datetime;
        this.preptime = preptime;
        this.cooktime = cooktime;
        this.serves = serves;
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

    public int getPostid() {
        return postid;
    }

    public void setPostid(int postid) {
        this.postid = postid;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getPreptime() {
        return preptime;
    }

    public void setPreptime(String preptime) {
        this.preptime = preptime;
    }

    public String getCooktime() {
        return cooktime;
    }

    public void setCooktime(String cooktime) {
        this.cooktime = cooktime;
    }

    public String getServes() {
        return serves;
    }

    public void setServes(String serves) {
        this.serves = serves;
    }
}


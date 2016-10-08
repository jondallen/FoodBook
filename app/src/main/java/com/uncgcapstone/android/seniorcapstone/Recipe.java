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
    String tag1;
    String tag2;
    String tag3;
    String likes;
    String likestotal;
    String favorites;

    public Recipe(){

    }

    public Recipe(int postid, String uid, String username, String recipename, String url, String datetime, String preptime, String cooktime, String serves, String tag1, String tag2, String tag3, String likes, String likestotal, String favorites){
        this.postid = postid;
        this.uid = uid;
        this.username = username;
        this.recipename = recipename;
        this.url = url;
        this.datetime = datetime;
        this.preptime = preptime;
        this.cooktime = cooktime;
        this.serves = serves;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
        this.likes = likes;
        this.likestotal = likestotal;
        this.favorites = favorites;
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

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public String getTag3() {
        return tag3;
    }

    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getLikestotal() {
        return likestotal;
    }

    public void setLikestotal(String likestotal) {
        this.likestotal = likestotal;
    }

    public String getFavorites() {
        return favorites;
    }

    public void setFavorites(String favorites) {
        this.favorites = favorites;
    }
}


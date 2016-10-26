package com.uncgcapstone.android.seniorcapstone.data;

/**
 * Created by jon on 10/20/2016.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Recipe {

    @SerializedName("postid")
    @Expose
    private String postid;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("recipename")
    @Expose
    private String recipename;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("datetime")
    @Expose
    private String datetime;
    @SerializedName("preptime")
    @Expose
    private String preptime;
    @SerializedName("cooktime")
    @Expose
    private String cooktime;
    @SerializedName("serves")
    @Expose
    private String serves;
    @SerializedName("tag1")
    @Expose
    private String tag1;
    @SerializedName("tag2")
    @Expose
    private String tag2;
    @SerializedName("tag3")
    @Expose
    private String tag3;
    @SerializedName("likes")
    @Expose
    private String likes;
    @SerializedName("likestotal")
    @Expose
    private String likestotal;
    @SerializedName("favorites")
    @Expose
    private String favorites;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("count")
    @Expose
    private String count;

    /**
     * No args constructor for use in serialization
     *
     */
    public Recipe() {
    }

    /**
     *
     * @param postid
     * @param uid
     * @param recipename
     * @param count
     * @param favorites
     * @param url
     * @param username
     * @param tag2
     * @param tag3
     * @param likestotal
     * @param preptime
     * @param likes
     * @param cooktime
     * @param rating
     * @param datetime
     * @param serves
     * @param tag1
     */
    public Recipe(String postid, String uid, String username, String recipename, String url, String datetime, String preptime, String cooktime, String serves, String tag1, String tag2, String tag3, String likes, String likestotal, String favorites, String rating, String count) {
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
        this.rating = rating;
        this.count = count;
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
     * The uid
     */
    public String getUid() {
        return uid;
    }

    /**
     *
     * @param uid
     * The uid
     */
    public void setUid(String uid) {
        this.uid = uid;
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
     * The recipename
     */
    public String getRecipename() {
        return recipename;
    }

    /**
     *
     * @param recipename
     * The recipename
     */
    public void setRecipename(String recipename) {
        this.recipename = recipename;
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
     * The preptime
     */
    public String getPreptime() {
        return preptime;
    }

    /**
     *
     * @param preptime
     * The preptime
     */
    public void setPreptime(String preptime) {
        this.preptime = preptime;
    }

    /**
     *
     * @return
     * The cooktime
     */
    public String getCooktime() {
        return cooktime;
    }

    /**
     *
     * @param cooktime
     * The cooktime
     */
    public void setCooktime(String cooktime) {
        this.cooktime = cooktime;
    }

    /**
     *
     * @return
     * The serves
     */
    public String getServes() {
        return serves;
    }

    /**
     *
     * @param serves
     * The serves
     */
    public void setServes(String serves) {
        this.serves = serves;
    }

    /**
     *
     * @return
     * The tag1
     */
    public String getTag1() {
        return tag1;
    }

    /**
     *
     * @param tag1
     * The tag1
     */
    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    /**
     *
     * @return
     * The tag2
     */
    public String getTag2() {
        return tag2;
    }

    /**
     *
     * @param tag2
     * The tag2
     */
    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    /**
     *
     * @return
     * The tag3
     */
    public String getTag3() {
        return tag3;
    }

    /**
     *
     * @param tag3
     * The tag3
     */
    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }

    /**
     *
     * @return
     * The likes
     */
    public String getLikes() {
        return likes;
    }

    /**
     *
     * @param likes
     * The likes
     */
    public void setLikes(String likes) {
        this.likes = likes;
    }

    /**
     *
     * @return
     * The likestotal
     */
    public String getLikestotal() {
        return likestotal;
    }

    /**
     *
     * @param likestotal
     * The likestotal
     */
    public void setLikestotal(String likestotal) {
        this.likestotal = likestotal;
    }

    /**
     *
     * @return
     * The favorites
     */
    public String getFavorites() {
        return favorites;
    }

    /**
     *
     * @param favorites
     * The favorites
     */
    public void setFavorites(String favorites) {
        this.favorites = favorites;
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
     * The count
     */
    public String getCount() {
        return count;
    }

    /**
     *
     * @param count
     * The count
     */
    public void setCount(String count) {
        this.count = count;
    }

}
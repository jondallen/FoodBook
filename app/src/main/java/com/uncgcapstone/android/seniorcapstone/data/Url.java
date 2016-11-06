package com.uncgcapstone.android.seniorcapstone.data;

/**
 * Created by jon on 10/21/2016.
 */

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Url {

    @SerializedName("user")
    @Expose
    private List<User> user = new ArrayList<User>();
    @SerializedName("uid")
    @Expose
    private String uid;

    /**
     * No args constructor for use in serialization
     *
     */
    public Url() {
    }

    /**
     *
     * @param user
     */
    public Url(List<User> user, String uid) {
        this.user = user;
        this.uid = uid;
    }

    /**
     *
     * @return
     * The user
     */
    public List<User> getUser() {
        return user;
    }

    /**
     *
     * @param user
     * The user
     */
    public void setUser(List<User> user) {
        this.user = user;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
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
    public Url(List<User> user) {
        this.user = user;
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

}
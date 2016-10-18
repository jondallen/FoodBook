package com.uncgcapstone.android.seniorcapstone.data;

/**
 * Created by jon on 10/17/2016.
 */

public class User {
    public String url;

    public User(){
    }

    public User(String url){
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

package com.uncgcapstone.android.seniorcapstone.io;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jon on 10/20/2016.
 */

public class ApiClient {
    //private static final String url_all_recipes = "http://63d42096.ngrok.io/android_connect_retro/";
    /*
    New Database
     */
    private static final String url_all_recipes = "http://54.201.252.62/test/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url_all_recipes)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

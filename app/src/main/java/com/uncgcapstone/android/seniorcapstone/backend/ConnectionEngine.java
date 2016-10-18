package com.uncgcapstone.android.seniorcapstone;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dfmut on 10/15/2016.
 */

public class ConnectionEngine extends Activity {
    private JSONParser jsonParser;
    private Gson gson = new Gson();
    private List<NameValuePair> params;
    private Recipe recipe;
    private LinkedList<Recipe> results;
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private InputStream inputStream;
    private HttpURLConnection httpURLConnection;
    private int response;
    private StringBuilder stringBuilder;
    private String line;
    private String data;
    private String username;
    private String password;

    private final URL signupURL = new URL("Insert");
    private final URL loginURL = new URL("Insert");
    private final URL recipeStream = new URL("Insert");

    public ConnectionEngine() throws MalformedURLException {
    }

    public class Signup extends AsyncTask<String, Void, LinkedList<Recipe>> {


        protected LinkedList<Recipe> doInBackground(String... params) {

            //signup
            username = params[0];
            password = params[1];

            jsonObject = new JSONObject();

            //cryptoengine
            try {
                jsonObject.put("Username", username);
                jsonObject.put("Password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                return downloadStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class Login extends AsyncTask<String, Void, LinkedList<Recipe>> {

        protected LinkedList<Recipe> doInBackground(String... params) {

            //login
            try {
                return downloadStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private LinkedList<Recipe> downloadStream() throws IOException {

        inputStream = null;

        try {

            httpURLConnection = (HttpURLConnection) recipeStream.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            response = httpURLConnection.getResponseCode();
            Log.d("Sign-up", "The response is: " + response);

            inputStream = httpURLConnection.getInputStream();

            try {

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                stringBuilder = new StringBuilder();
                line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(data + "\n");
                }
                inputStream.close();
                data = stringBuilder.toString();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }

            // try parse the string to a JSON object
            try {
                jsonObject = new JSONObject(data);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return ConvertJSONObjectToArray(jsonObject);
    }

    private LinkedList<Recipe> ConvertJSONObjectToArray(JSONObject object) {
        try {
            if (object != null) {
                jsonArray = object.getJSONArray("Recipes");
                Log.d("Count: ", String.valueOf(jsonArray.length()));
                results = new LinkedList<Recipe>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = new JSONObject();
                    jsonObject = jsonArray.getJSONObject(i);
                    recipe = gson.fromJson(jsonObject.toString(), Recipe.class);
                    results.add(recipe);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return results;
    }
}
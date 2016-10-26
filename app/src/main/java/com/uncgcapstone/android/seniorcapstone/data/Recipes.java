package com.uncgcapstone.android.seniorcapstone.data;

/**
 * Created by jon on 10/20/2016.
 */

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Recipes {

    @SerializedName("Recipes")
    @Expose
    private List<Recipe> recipes = new ArrayList<Recipe>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Recipes() {
    }

    /**
     *
     * @param recipes
     */
    public Recipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    /**
     *
     * @return
     * The recipes
     */
    public List<Recipe> getRecipes() {
        return recipes;
    }

    /**
     *
     * @param recipes
     * The Recipes
     */
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

}
package com.uncgcapstone.android.seniorcapstone;

/**
 * Created by jon on 10/4/2016
 * Used to temporarily store a list of ingredients to be displayed in DetailedRecipeActivity
 */

public class Ingredients {
    String quantity;
    String unit;
    String ingredient;

    public Ingredients(){

    }

    public Ingredients(String quantity, String unit, String ingredient){
        this.quantity = quantity;
        this.unit = unit;
        this.ingredient = ingredient;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}

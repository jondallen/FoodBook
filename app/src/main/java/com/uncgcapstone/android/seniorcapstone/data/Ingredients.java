package com.uncgcapstone.android.seniorcapstone.data;

        import java.util.ArrayList;
        import java.util.List;

        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class Ingredients {

    @SerializedName("Ingredients")
    @Expose
    private List<Ingredient> ingredients = new ArrayList<Ingredient>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Ingredients() {
    }

    /**
     *
     * @param ingredients
     */
    public Ingredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    /**
     *
     * @return
     * The ingredients
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     *
     * @param ingredients
     * The Ingredients
     */
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

}

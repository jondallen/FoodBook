package com.uncgcapstone.android.seniorcapstone.data;

/**
 * Created by jon on 10/4/2016.
 * Used to temporarily store a list of steps to be displayed in DetailedRecipeActivity
 */

public class Steps {
    String step;

    public Steps(){

    }

    public Steps(String step) {
        this.step = step;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }
}

package com.uncgcapstone.android.seniorcapstone.data;

/**
 * Created by jon on 10/21/2016.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Step {

    @SerializedName("step")
    @Expose
    private String step;

    /**
     * No args constructor for use in serialization
     *
     */
    public Step() {
    }

    /**
     *
     * @param step
     */
    public Step(String step) {
        this.step = step;
    }

    /**
     *
     * @return
     * The step
     */
    public String getStep() {
        return step;
    }

    /**
     *
     * @param step
     * The step
     */
    public void setStep(String step) {
        this.step = step;
    }

}
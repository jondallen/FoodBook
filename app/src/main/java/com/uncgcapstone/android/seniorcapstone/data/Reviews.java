package com.uncgcapstone.android.seniorcapstone.data;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Reviews {

    @SerializedName("Reviews")
    @Expose
    private List<Review> reviews = new ArrayList<Review>();
    @SerializedName("average")
    @Expose
    private Double average;
    @SerializedName("count")
    @Expose
    private Integer count;

    /**
     * No args constructor for use in serialization
     *
     */
    public Reviews() {
    }

    /**
     *
     * @param reviews
     * @param count
     * @param average
     */
    public Reviews(List<Review> reviews, Double average, Integer count) {
        this.reviews = reviews;
        this.average = average;
        this.count = count;
    }

    /**
     *
     * @return
     * The reviews
     */
    public List<Review> getReviews() {
        return reviews;
    }

    /**
     *
     * @param reviews
     * The Reviews
     */
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    /**
     *
     * @return
     * The average
     */
    public Double getAverage() {
        return average;
    }

    /**
     *
     * @param average
     * The average
     */
    public void setAverage(Double average) {
        this.average = average;
    }

    /**
     *
     * @return
     * The count
     */
    public Integer getCount() {
        return count;
    }

    /**
     *
     * @param count
     * The count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

}
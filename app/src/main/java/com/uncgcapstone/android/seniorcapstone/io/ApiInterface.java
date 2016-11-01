package com.uncgcapstone.android.seniorcapstone.io;

import com.uncgcapstone.android.seniorcapstone.data.Ingredients;
import com.uncgcapstone.android.seniorcapstone.data.Recipes;
import com.uncgcapstone.android.seniorcapstone.data.Reviews;
import com.uncgcapstone.android.seniorcapstone.data.Steps;
import com.uncgcapstone.android.seniorcapstone.data.Url;
import com.uncgcapstone.android.seniorcapstone.data.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by jon on 10/20/2016.
 */

    public interface ApiInterface {
        //@GET("get_all_recipes.php")
        //Call<RecipeModel> getRecipes(@Query("api_key") String apiKey);


    /*@Headers({"Content-Type:application/json", "x-app-id:a73b1a04", "x-app-key:997a5b530342e1b5132b1159d8668040", "x-remote-user-id:0"})
    @POST("https://trackapi.nutritionix.com/v2/natural/nutrients")
            Call<Void> getInfo(
        @Query("query") String query
    );*/


        @FormUrlEncoded
        @POST("get_all_recipes.php")
                Call<Recipes> getAllRecipes(
                @Field("userid") String userid
                );

    @FormUrlEncoded
    @POST("get_all_recipes_favorites.php")
    Call<Recipes> getAllRecipesFavorites(
            @Field("userid") String userid
    );

    @FormUrlEncoded
    @POST("get_all_recipes_suggested.php")
    Call<Recipes> getAllRecipesSuggested(
            @Field("userid") String userid
    );

    @FormUrlEncoded
    @POST("get_all_recipes_follows.php")
    Call<Recipes> getAllRecipesFollows(
            @Field("userid") String userid
    );

    @FormUrlEncoded
    @POST("get_all_recipes_likes.php")
    Call<Recipes> getAllRecipesLikes(
            @Field("userid") String userid
    );

    @FormUrlEncoded
    @POST("get_all_recipes_user.php")
    Call<Recipes> getAllRecipesUser(
            @Field("userid") String userid,
            @Field("loggedinuser") String loggedinuser
    );

    @FormUrlEncoded
    @POST("get_all_recipes_rating.php")
    Call<Recipes> getAllRecipesRating(
            @Field("userid") String userid
    );

    @FormUrlEncoded
    @POST("get_all_recipes_num_rating.php")
    Call<Recipes> getAllRecipesNumRating(
            @Field("userid") String userid
    );

    @FormUrlEncoded
    @POST("search_recipes_favorites.php")
    Call<Recipes> searchRecipesFavorites(
            @Field("userid") String userid,
            @Field("search") String[] search,
            @Field("searchUnbroken") String[] searchUnbroken
    );

    @FormUrlEncoded
    @POST("search_recipes_likes.php")
    Call<Recipes> searchRecipesLikes(
            @Field("userid") String userid,
            @Field("search") String[] search,
            @Field("searchUnbroken") String[] searchUnbroken
    );

    @FormUrlEncoded
    @POST("search_recipes_follows.php")
    Call<Recipes> searchRecipesFollows(
            @Field("userid") String userid,
            @Field("search") String[] search,
            @Field("searchUnbroken") String[] searchUnbroken
    );

    @FormUrlEncoded
    @POST("search_recipes_rating.php")
    Call<Recipes> searchRecipesRating(
            @Field("userid") String userid,
            @Field("search") String[] search,
            @Field("searchUnbroken") String[] searchUnbroken
    );

    @FormUrlEncoded
    @POST("search_recipes_num_rating.php")
    Call<Recipes> searchRecipesNumRating(
            @Field("userid") String userid,
            @Field("search") String[] search,
            @Field("searchUnbroken") String[] searchUnbroken
    );

    @FormUrlEncoded
    @POST("search_recipes.php")
    Call<Recipes> searchAllRecipes(
            @Field("userid") String userid,
            @Field("search") String[] search,
            @Field("searchUnbroken") String[] searchUnbroken
    );

    @FormUrlEncoded
    @POST("create_recipe.php")
    Call<Void> createRecipe(
            @Field("uid") String uid,
            @Field("username") String username,
            @Field("recipename") String recipename,
            @Field("url") String url,
            @Field("datetime") String datetime,
            @Field("preptime") String preptime,
            @Field("cooktime") String cooktime,
            @Field("serves") String serves,
            @Field("tags[]") ArrayList<String> tags,
            @Field("ingredients[]") ArrayList<String> ingredients,
            @Field("ingredients2[]") ArrayList<String> ingredients2,
            @Field("ingredients3[]") ArrayList<String> ingredients3,
            @Field("steps[]") ArrayList<String> steps,
            @Field("ingredtags[]") ArrayList<String> ingredtags
    );

    @FormUrlEncoded
    @POST("get_ingredients.php")
    Call<Ingredients> getIngredients(
            @Field("postid") String postid
    );

    @FormUrlEncoded
    @POST("get_steps.php")
    Call<Steps> getSteps(
            @Field("postid") String postid
    );

    @FormUrlEncoded
    @POST("set_profile_image.php")
    Call<Void> setProfileImage(
            @Field("userid") String userid,
            @Field("url") String url
    );

    @FormUrlEncoded
    @POST("get_profile_image.php")
    Call<Url> getProfileImage(
            @Field("userid") String userid
    );

    @FormUrlEncoded
    @POST("get_all_reviews.php")
    Call<Reviews> getAllReviews(
            @Field("postid") String postid
    );

    @FormUrlEncoded
    @POST("create_review.php")
    Call<Void> createReview(
            @Field("userid") String userid,
            @Field("postid") String postid,
            @Field("review") String review,
            @Field("rating") String rating,
            @Field("username") String username,
            @Field("datetime") String datetime

    );

    @FormUrlEncoded
    @POST("likes.php")
    Call<Void> likes(
            @Field("userid") String userid,
            @Field("postid") String postid
    );

    @FormUrlEncoded
    @POST("unlikes.php")
    Call<Void> unlikes(
            @Field("userid") String userid,
            @Field("postid") String postid
    );

    @FormUrlEncoded
    @POST("favorites.php")
    Call<Void> favorites(
            @Field("userid") String userid,
            @Field("postid") String postid
    );

    @FormUrlEncoded
    @POST("unfavorites.php")
    Call<Void> unfavorites(
            @Field("userid") String userid,
            @Field("postid") String postid
    );

    @FormUrlEncoded
    @POST("follows.php")
    Call<Void> follows(
            @Field("userid1") String userid1,
            @Field("userid2") String userid2
    );

    @FormUrlEncoded
    @POST("unfollows.php")
    Call<Void> unfollows(
            @Field("userid1") String userid1,
            @Field("userid2") String userid2
    );

    @FormUrlEncoded
    @POST("is_following.php")
    Call<Object> isFollowing(
            @Field("userid1") String userid1,
            @Field("userid2") String userid2
    );

}

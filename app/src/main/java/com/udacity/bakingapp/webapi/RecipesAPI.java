package com.udacity.bakingapp.webapi;

import com.udacity.bakingapp.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipesAPI {

    String BASE_URL = "http://go.udacity.com/";

    //load the list of all recipes
    @GET("android-baking-app-json")
    Call<List<Recipe>> getRecipes();
}

package com.udacity.bakingapp.webapi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipesAPIClient {

    private static RecipesAPI api;

    private RecipesAPIClient(){}

    public static RecipesAPI getInstance(){
        //create a Retrofit singleton
        if (api == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RecipesAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            api = retrofit.create(RecipesAPI.class);
        }

        return api;
    }
}



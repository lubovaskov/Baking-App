package com.udacity.bakingapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.udacity.bakingapp.tests.IdlingResourceManager;

public class RecipesActivity extends AppCompatActivity
    implements
        //interface for communication with the recipes fragment
        RecipesFragment.RecipesFragmentListener,
        //interface for managing idling resources needed for tests
        IdlingResourceManager {

    private static final String TAG_RECIPES_FRAGMENT = "recipes_fragment";
    private static final String RECIPES_IDLING_RESOURCE_NAME = "recipes_idling_resource";

    //an idling resource to be used by tests
    @Nullable
    private CountingIdlingResource idlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        //search if the recipes fragment exists
        FragmentManager fManager = getSupportFragmentManager();
        RecipesFragment recipesFragment =
                (RecipesFragment) fManager.findFragmentByTag(TAG_RECIPES_FRAGMENT);

        //create the recipes fragment if it does not exist
        if (recipesFragment == null) {
            recipesFragment = RecipesFragment.newInstance(this);
            fManager.beginTransaction()
                    .add(R.id.fragment_recipes_container, recipesFragment, TAG_RECIPES_FRAGMENT)
                    .commit();
        }
    }

    public void onError(String errorText) {
        Toast.makeText(this, errorText, Toast.LENGTH_LONG).show();
    }

    @VisibleForTesting
    @NonNull
    public CountingIdlingResource getIdlingResource() {
        //create idling resource to be used by tests
        if (idlingResource == null) {
            idlingResource = new CountingIdlingResource(RECIPES_IDLING_RESOURCE_NAME);
        }
        return idlingResource;
    }

    @Override
    public void setBusyIdlingResource() {
        //mark the idling resource as busy
        getIdlingResource().increment();
    }

    @Override
    public void setIdleIdlingResource() {
        //mark the idling resource as idle
        getIdlingResource().decrement();
    }
}

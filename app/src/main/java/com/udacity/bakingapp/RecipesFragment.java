package com.udacity.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.bakingapp.adapters.RecipesAdapter;
import com.udacity.bakingapp.model.Recipe;
import com.udacity.bakingapp.tests.IdlingResourceManager;
import com.udacity.bakingapp.utils.NetworkUtils;
import com.udacity.bakingapp.webapi.RecipesAPIClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipesFragment extends Fragment
    implements
        //Retrofit result interface
        Callback<List<Recipe>>,
        //recipes recycler view adapter interface
        RecipesAdapter.RecipesAdapterOnClickHandler {

    private static final String RECIPES_PARCELABLE_NAME = "recipes";

    //Butterknife unbinder
    private Unbinder unbinder;
    //instance of the interface for communication between the fragment and
    //the activity containing this fragment
    private RecipesFragmentListener recipesListener;
    //instance of the interface for managing idling resources needed for tests
    private IdlingResourceManager idlingResourceManager;

    //list of recipes displayed in the fragment
    private List<Recipe> recipes;

    //UI components
    private RecipesAdapter rvRecipesAdapter;
    @BindView(R.id.recyclerview_recipes)
    RecyclerView rvRecipes;

    //interface for communication between the fragment and the activity containing this fragment
    public interface RecipesFragmentListener {
        void onError(String errorText);
    }

    @NonNull
    public static RecipesFragment newInstance(IdlingResourceManager idlingResourceManager) {
        RecipesFragment fragment = new RecipesFragment();
        fragment.idlingResourceManager = idlingResourceManager;
        return fragment;
    }

    public RecipesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        View fragmentView = inflater.inflate(R.layout.fragment_recipes, container,
                false);
        //initialize butterknife bindings
        unbinder = ButterKnife.bind(this, fragmentView);

        //create the adapter for the recipes recycler view
        rvRecipesAdapter = new RecipesAdapter(getActivity(), this);
        rvRecipes.setAdapter(rvRecipesAdapter);

        if (savedInstanceState == null) {
            //load recipes list if there is no saved state
            getRecipes();
        } else {
            //restore recipes list if there is a saved state
            restoreInstanceState(savedInstanceState);
        }

        return fragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //release butterknife bindings
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //save a reference to the current activity
        recipesListener = (RecipesFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //clear the reference to the current activity
        recipesListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save recipes list
        if (recipes != null) {
            outState.putParcelableArrayList(RECIPES_PARCELABLE_NAME, new ArrayList<>(recipes));
        }
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        //restore recipes list
        recipes = savedInstanceState.getParcelableArrayList(RECIPES_PARCELABLE_NAME);
        rvRecipesAdapter.reloadData(recipes);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //create options menu
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            //refresh recipes list from options menu
            case R.id.action_refresh_recipes:
                getRecipes();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getRecipes() {
        //send request to receive recipes list if there is network connection
        if (checkOnline()) {
            //pause testing until recipe data is loaded
            if (idlingResourceManager != null) {
                idlingResourceManager.setBusyIdlingResource();
            }
            //enqueue recipe data loading
            RecipesAPIClient.getInstance().getRecipes().enqueue(this);
        }
    }

    @Override
    public void onResponse(@NonNull Call<List<Recipe>> call,
                           @NonNull Response<List<Recipe>> response) {
        //load recipes list from the JSON response
        if (response.isSuccessful()) {
            recipes = response.body();
            if (recipes != null) {
                rvRecipesAdapter.reloadData(recipes);
            }
        } else {
            if (recipesListener != null) {
                recipesListener.onError(getResources().getString(R.string.network_error_text));
            }
        }
        //allow testing to continue
        if (idlingResourceManager != null) {
            idlingResourceManager.setIdleIdlingResource();
        }
    }

    @Override
    public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
        if (recipesListener != null) {
            recipesListener.onError(getResources().getString(R.string.network_error_text));
        }
        //allow testing to continue
        if (idlingResourceManager != null) {
            idlingResourceManager.setIdleIdlingResource();
        }
    }

    @NonNull
    private Boolean checkOnline() {
        //check if network connection is available
        if (!NetworkUtils.isOnline(getActivity())) {
            if (recipesListener != null) {
                recipesListener.onError(getResources().getString(R.string.not_online_error_text));
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRecipeClick(int position) {
        //start the recipe steps activity, passing to it the Recipe object at the current position
        Intent intent = new Intent(getActivity(), RecipeStepListActivity.class);
        if (recipes != null) {
            intent.putExtra(Recipe.RECIPE_PARCELABLE_NAME, recipes.get(position));
        }
        startActivity(intent);
    }
}

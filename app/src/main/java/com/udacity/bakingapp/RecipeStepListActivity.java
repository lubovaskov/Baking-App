package com.udacity.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.udacity.bakingapp.adapters.RecipeStepsAdapter;
import com.udacity.bakingapp.model.Recipe;
import com.udacity.bakingapp.model.RecipeIngredient;
import com.udacity.bakingapp.model.RecipeStep;
import com.udacity.bakingapp.utils.WidgetUtils;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeStepListActivity extends AppCompatActivity
    implements RecipeStepsAdapter.RecipeStepViewer {

    private static final String RECIPE_PARCELABLE_NAME = "recipe";

    private Unbinder unbinder;

    Recipe recipe;

    private RecipeStepsAdapter rvRecipeStepsAdapter;

    @BindView(R.id.recyclerview_recipe_steps)
    RecyclerView rvRecipeSteps;
    @BindView(R.id.textview_recipe_ingredients)
    TextView tvRecipeIngredients;
    @BindBool(R.bool.use_two_pane)
    boolean useTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipestep_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //initialize butterknife bindings
        unbinder = ButterKnife.bind(this);

        //restore saved recipe steps, if there are any
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null) {
                recipe = intent.getParcelableExtra(Recipe.RECIPE_PARCELABLE_NAME);
                if (recipe != null) {
                    updateUI();
                    displayRecipeStep(0);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //release butterknife bindings
        unbinder.unbind();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECIPE_PARCELABLE_NAME, recipe);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recipe = savedInstanceState.getParcelable(RECIPE_PARCELABLE_NAME);
        if (recipe != null) {
            updateUI();
        }
    }

    public void displayRecipeStep(int position) {
        if (useTwoPane) {
            RecipeStep recipeStep = recipe.steps.get(position);
            Bundle arguments = new Bundle();
            arguments.putParcelable(RecipeStepDetailFragment.RECIPE_STEP_ARGUMENT_NAME, recipeStep);
            RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipestep_detail_container, fragment)
                    .commit();
        }
    }

    private void updateUI() {
        //set activity title
        this.setTitle(recipe.name);

        //reload ingredients text
        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ingredient : recipe.ingredients) {
            sb.append(RecipeIngredient.formatIngdedient(
                    this, ingredient.ingredient, ingredient.quantity, ingredient.measure));
            sb.append("\n");
        }
        tvRecipeIngredients.setText(sb.toString().trim());

        //reload recipe steps into the recyclerview
        rvRecipeStepsAdapter = new RecipeStepsAdapter(this, this,
                recipe.steps, useTwoPane);
        rvRecipeSteps.setAdapter(rvRecipeStepsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_set_widget_recipe:
                //choose the current recipe to be shown in the widget
                WidgetUtils.setWidgetRecipe(this, recipe);
                return true;
            case R.id.action_clear_widget_recipe:
                //clear the widget recipe
                WidgetUtils.setWidgetRecipe(this, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

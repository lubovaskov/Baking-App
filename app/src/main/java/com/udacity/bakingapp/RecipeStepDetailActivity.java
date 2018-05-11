package com.udacity.bakingapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.udacity.bakingapp.model.RecipeStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RecipeStepDetailActivity extends AppCompatActivity {

    public static String RECIPE_STEPS_EXTRA_NAME = "recipe_steps";
    public static String RECIPE_STEPS_POSITION_EXTRA_NAME = "recipe_steps_position";
    private static final String RECIPE_STEPS_PARCELABLE_NAME = "recipe_steps";
    private static final String RECIPE_STEPS_POSITION_PARCELABLE_NAME = "recipe_steps_position";

    private Unbinder unbinder;

    private List<RecipeStep> recipeSteps;
    private int currentPosition;

    @BindView(R.id.button_previous_recipe_step)
    Button btnPrevious;
    @BindView(R.id.button_next_recipe_step)
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipestep_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //initialize butterknife bindings
        unbinder = ButterKnife.bind(this);

        if (savedInstanceState == null) {
            recipeSteps = getIntent().getParcelableArrayListExtra(RECIPE_STEPS_EXTRA_NAME);
            currentPosition = getIntent().getIntExtra(RECIPE_STEPS_POSITION_EXTRA_NAME, 0);
            reloadFragment();
            setButtonsVisibility();
        }

        updateTitle();
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
        outState.putParcelableArrayList(RECIPE_STEPS_PARCELABLE_NAME,
                new ArrayList<Parcelable>(recipeSteps));
        outState.putInt(RECIPE_STEPS_POSITION_PARCELABLE_NAME, currentPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recipeSteps = savedInstanceState.getParcelableArrayList(RECIPE_STEPS_PARCELABLE_NAME);
        currentPosition = savedInstanceState.getInt(RECIPE_STEPS_POSITION_PARCELABLE_NAME);
        setButtonsVisibility();
        updateTitle();
    }

    private void updateTitle() {
        //display recipe step id in the title
        if ((recipeSteps != null) && (recipeSteps.size() > currentPosition) &&
                (recipeSteps.get(currentPosition) != null)) {
            setTitle(String.format(Locale.getDefault(),
                    getResources().getString(R.string.recipe_step_detail_activity_title),
                    recipeSteps.get(currentPosition).id));
        }
    }

    private void setButtonsVisibility() {
        //hide previous and next step buttons if current configuration is landscape
        int orientation = getResources().getConfiguration().orientation;
        if ((orientation == Configuration.ORIENTATION_LANDSCAPE) && (recipeSteps != null) &&
            (recipeSteps.get(currentPosition).videoURL != null) &&
            (!recipeSteps.get(currentPosition).videoURL.isEmpty())) {
                btnPrevious.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
        } else {
            btnPrevious.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.button_previous_recipe_step)
    void onButtonPreviousClick() {
        //load previous recipe step
        if (currentPosition > 0) {
            currentPosition -= 1;
            reloadFragment();
            setButtonsVisibility();
        }
    }

    @OnClick(R.id.button_next_recipe_step)
    void onButtonNextClick() {
        //load next recipe step
        if (currentPosition < recipeSteps.size() - 1) {
            currentPosition += 1;
            reloadFragment();
            setButtonsVisibility();
        }
    }

    private void reloadFragment() {
        //load the current recipe into a new fragment and display the fragment
        Bundle arguments = new Bundle();
        arguments.putParcelable(RecipeStepDetailFragment.RECIPE_STEP_ARGUMENT_NAME,
                recipeSteps.get(currentPosition));
        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipestep_detail_container, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

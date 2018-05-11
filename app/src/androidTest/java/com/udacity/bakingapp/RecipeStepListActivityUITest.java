package com.udacity.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.udacity.bakingapp.model.Recipe;
import com.udacity.bakingapp.model.RecipeIngredient;
import com.udacity.bakingapp.model.RecipeStep;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class RecipeStepListActivityUITest {

    @Rule
    public ActivityTestRule<RecipeStepListActivity> activityTestRule =
            new ActivityTestRule<RecipeStepListActivity>(RecipeStepListActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    //create and pass a test recipe to the activity
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent intent = new Intent(targetContext, RecipeStepListActivity.class);
                    intent.putExtra(Recipe.RECIPE_PARCELABLE_NAME, getTestRecipe());
                    return intent;
                }
            };

    //test if the details of a recipe step are displayed when a recipe step is clicked
    @Test
    public void onRecyclerViewRecipeStepsClick_opensRecipeStepDetailActivity() {
        //click on the first item in the recipe steps recycler view
        onView(withId(R.id.recyclerview_recipe_steps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        if (activityTestRule.getActivity().useTwoPane) {
            //check if the recipe step description text view is displayed
            onView(withId(R.id.textview_recipestep_description))
                    .check(matches(isDisplayed()));
        } else {
            //check if the previous recipe step button is displayed
            onView(withId(R.id.button_previous_recipe_step))
                    .check(matches(isDisplayed()));
        }
    }

    private Recipe getTestRecipe() {
        ArrayList<RecipeIngredient> recipeIngredients = new ArrayList<>();
        recipeIngredients.add(new RecipeIngredient("test_ingredient",
                Float.parseFloat("1"), "test_measure"));
        ArrayList<RecipeStep> recipeSteps = new ArrayList<>();
        recipeSteps.add(new RecipeStep(1, "shortDescription",
                "description", "",""));
        return new Recipe(1, "test_recipe", recipeIngredients, recipeSteps,
                1, "");
    }
}

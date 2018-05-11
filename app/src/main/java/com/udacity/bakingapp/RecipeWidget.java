package com.udacity.bakingapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.udacity.bakingapp.model.Recipe;
import com.udacity.bakingapp.model.RecipeIngredient;
import com.udacity.bakingapp.utils.WidgetUtils;

public class RecipeWidget extends AppWidgetProvider {

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe recipe) {
        //construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_recipe);
        views.removeAllViews(R.id.widget_ingredients_container);

        if (recipe != null) {
            //load recipe name into widget
            views.setTextViewText(R.id.widget_recipe, recipe.name);
            //load recipe ingredients into widget
            for (RecipeIngredient ingredient : recipe.ingredients) {
                RemoteViews ingredientView = new RemoteViews(context.getPackageName(),
                        R.layout.widget_recipe_item);

                String line = RecipeIngredient.formatIngdedient(
                        context, ingredient.ingredient, ingredient.quantity, ingredient.measure);

                ingredientView.setTextViewText(R.id.widget_ingredient, line);
                views.addView(R.id.widget_ingredients_container, ingredientView);
            }
        } else {
            //if there is no selected recipe for displaying into widget - show a reminder
            views.setTextViewText(R.id.widget_recipe, context.getString(R.string.no_widget_recipe));
        }

        //instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //there may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId,
                    WidgetUtils.getWidgetRecipe(context));
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}


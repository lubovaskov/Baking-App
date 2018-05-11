package com.udacity.bakingapp.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import com.udacity.bakingapp.RecipeWidget;
import com.udacity.bakingapp.model.Recipe;

public class WidgetUtils {

    private static final String WIDGET_RECIPE_SHARED_PREFERENCE = "widget_recipe";

    //save a recipe into shared preferences to be displayed into a widget
    public static void setWidgetRecipe(Context context, Recipe recipe) {
        SharedPreferences prefs = context.getSharedPreferences(WIDGET_RECIPE_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        if (recipe != null) {
            Gson gson = new Gson();
            String jsonRecipe = gson.toJson(recipe);
            prefs.edit().putString(WIDGET_RECIPE_SHARED_PREFERENCE, jsonRecipe).apply();
        } else {
            prefs.edit().remove(WIDGET_RECIPE_SHARED_PREFERENCE).apply();
        }
        updateRecipeWidget(context);
    }

    //load a recipe from shared preferences to be displayed into a widget
    public static Recipe getWidgetRecipe(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(WIDGET_RECIPE_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        if (prefs.contains(WIDGET_RECIPE_SHARED_PREFERENCE)) {
            Gson gson = new Gson();
            String jsonRecipe = prefs.getString(WIDGET_RECIPE_SHARED_PREFERENCE, null);
            return gson.fromJson(jsonRecipe, Recipe.class);
        } else {
            return null;
        }
    }

    //update the content of all widgets when a recipe is chosen
    private static void updateRecipeWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetCompName = new ComponentName(context, RecipeWidget.class);
        for (int widgetId : appWidgetManager.getAppWidgetIds(widgetCompName)) {
            RecipeWidget.updateAppWidget(context, appWidgetManager, widgetId, getWidgetRecipe(context));
        }
    }
}

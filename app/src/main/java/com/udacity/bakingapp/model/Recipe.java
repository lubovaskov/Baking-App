package com.udacity.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Recipe implements Parcelable{

    public static String RECIPE_PARCELABLE_NAME = "recipe";

    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("ingredients")
    public List<RecipeIngredient> ingredients;
    @SerializedName("steps")
    public List<RecipeStep> steps;
    @SerializedName("servings")
    public int servings;
    @SerializedName("image")
    public String image;

    public Recipe(int id, String name, List<RecipeIngredient> ingredients, List<RecipeStep> steps, int servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    private Recipe(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        ingredients = parcel.createTypedArrayList(RecipeIngredient.CREATOR);
        steps = parcel.createTypedArrayList(RecipeStep.CREATOR);
        servings = parcel.readInt();
        image = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeTypedList(ingredients);
        parcel.writeTypedList(steps);
        parcel.writeInt(servings);
        parcel.writeString(image);
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel parcel) {
            return new Recipe(parcel);
        }

        @Override
        public Recipe[] newArray(int i) {
            return new Recipe[i];
        }
    };
}

package com.udacity.bakingapp.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.udacity.bakingapp.R;

import java.util.Locale;

public class RecipeIngredient implements Parcelable {

    @SerializedName("ingredient")
    public String ingredient;
    @SerializedName("quantity")
    public Float quantity;
    @SerializedName("measure")
    public String measure;

    public RecipeIngredient(String ingredient, Float quantity, String measure) {
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.measure = measure;
    }

    private RecipeIngredient(Parcel parcel) {
        ingredient = parcel.readString();
        quantity = parcel.readFloat();
        measure = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ingredient);
        parcel.writeFloat(quantity);
        parcel.writeString(measure);
    }

    public static final Parcelable.Creator<RecipeIngredient> CREATOR =
            new Parcelable.Creator<RecipeIngredient>() {
        @Override
        public RecipeIngredient createFromParcel(Parcel parcel) {
            return new RecipeIngredient(parcel);
        }

        @Override
        public RecipeIngredient[] newArray(int i) {
            return new RecipeIngredient[i];
        }
    };

    public static String formatIngdedient(Context context, String name, float quantity,
                                          String measure) {

        String line = context.getResources().getString(R.string.recipe_ingredients_line);

        String quantityStr = String.format(Locale.getDefault(), "%s", quantity);
        if (quantity == (long) quantity) {
            quantityStr = String.format(Locale.getDefault(), "%d", (long) quantity);
        }

        return String.format(Locale.getDefault(), line, name, quantityStr, measure);
    }
}

package com.udacity.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.bakingapp.R;
import com.udacity.bakingapp.model.Recipe;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipesAdapterViewHolder> {

    private final Context mContext;
    private List<Recipe> recipes;

    final private RecipesAdapterOnClickHandler mClickHandler;

    public interface RecipesAdapterOnClickHandler {
        void onRecipeClick(int position);
    }

    public RecipesAdapter(@NonNull Context context, RecipesAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public RecipesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_recipe, viewGroup,
                false);
        view.setFocusable(true);

        return new RecipesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipesAdapterViewHolder recipesAdapterViewHolder, int position) {
        Recipe recipe = recipes.get(position);
        recipesAdapterViewHolder.tvRecipeName.setText(recipe.name);
        recipesAdapterViewHolder.tvRecipeServings.setText(String.format(Locale.getDefault(),
                mContext.getResources().getString(R.string.recipe_servings), recipe.servings));
        String imagePath = recipe.image;
        if ((recipe.image != null) && (recipe.image.equals(""))) {
            imagePath = null;
        }

        Picasso.with(mContext)
                .load(imagePath)
                .error(R.drawable.recipe_placeholder)
                .placeholder(R.drawable.recipe_placeholder)
                .into(recipesAdapterViewHolder.ivRecipeImage);
    }

    @Override
    public int getItemCount() {
        if (recipes == null) return 0;
        return recipes.size();
    }

    /*@Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_REVIEWS;
    }*/

    public void reloadData(List<Recipe> newData) {
        recipes = newData;
        notifyDataSetChanged();
    }

    class RecipesAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.textview_recipe_name)
        TextView tvRecipeName;
        @BindView(R.id.textview_recipe_servings)
        TextView tvRecipeServings;
        @BindView(R.id.imageview_recipe_image)
        ImageView ivRecipeImage;

        RecipesAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onRecipeClick(adapterPosition);
        }
    }
}

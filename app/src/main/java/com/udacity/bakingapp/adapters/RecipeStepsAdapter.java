package com.udacity.bakingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.bakingapp.R;
import com.udacity.bakingapp.RecipeStepDetailActivity;
import com.udacity.bakingapp.model.RecipeStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepsAdapter
        extends RecyclerView.Adapter<RecipeStepsAdapter.RecipeStepsAdapterViewHolder> {

    private List<RecipeStep> recipeSteps;

    private Context mContext;
    private RecipeStepViewer mRecipeStepViewer;
    private final boolean mTwoPane;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mTwoPane) {
                mRecipeStepViewer.displayRecipeStep((Integer) view.getTag());
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, RecipeStepDetailActivity.class);
                intent.putParcelableArrayListExtra(RecipeStepDetailActivity.RECIPE_STEPS_EXTRA_NAME,
                        new ArrayList<>(recipeSteps));
                intent.putExtra(RecipeStepDetailActivity.RECIPE_STEPS_POSITION_EXTRA_NAME,
                        (Integer) view.getTag());
                context.startActivity(intent);
            }
        }
    };

    public RecipeStepsAdapter(Context context, RecipeStepViewer recipeStepViewer,
                              List<RecipeStep> items, boolean twoPane) {
        mContext = context;
        mRecipeStepViewer = recipeStepViewer;
        recipeSteps = items;
        mTwoPane = twoPane;
    }

    @Override
    public RecipeStepsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipestep_list_content, parent, false);
        return new RecipeStepsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeStepsAdapterViewHolder holder, int position) {
        RecipeStep recipeStep = recipeSteps.get(position);
        holder.tvRecipeStep.setText(String.format(Locale.getDefault(),
                mContext.getResources().getString(R.string.recipe_step_text),
                recipeStep.id, recipeStep.shortDescription));
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        if (recipeSteps != null) {
            return recipeSteps.size();
        } else {
            return 0;
        }
    }

    class RecipeStepsAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_recipe_step)
        TextView tvRecipeStep;

        RecipeStepsAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    //interface for communication with RecipeStepListActivity
    public interface RecipeStepViewer {
        void displayRecipeStep(int position);
    }
}

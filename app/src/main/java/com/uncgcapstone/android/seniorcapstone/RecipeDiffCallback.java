package com.uncgcapstone.android.seniorcapstone;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

public class RecipeDiffCallback extends DiffUtil.Callback {
    private List<Recipe> mOldRecipe;
    private List<Recipe> mNewRecipe;

    public RecipeDiffCallback(List<Recipe> oldList, List<Recipe> newList) {
        this.mOldRecipe = oldList;
        this.mNewRecipe = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldRecipe != null ? mOldRecipe.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewRecipe != null ? mNewRecipe.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mNewRecipe.get(newItemPosition).getLikes() == mOldRecipe.get(oldItemPosition).getLikes();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mNewRecipe.get(newItemPosition).equals(mOldRecipe.get(oldItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Recipe newRecipe = mNewRecipe.get(newItemPosition);
        Recipe oldRecipe = mOldRecipe.get(oldItemPosition);
        Bundle diffBundle = new Bundle();
        if (newRecipe.getLikes() != oldRecipe.getLikes()) {
            diffBundle.putString("likes", newRecipe.getLikes());
        }
        if (diffBundle.size() == 0) return null;
        return diffBundle;
    }
}
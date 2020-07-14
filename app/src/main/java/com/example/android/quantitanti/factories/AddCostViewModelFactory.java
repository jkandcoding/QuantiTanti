package com.example.android.quantitanti.factories;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.viewmodels.AddCostViewModel;

public class AddCostViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final CostDatabase mDb;
    private final int mCostId;

    public AddCostViewModelFactory(CostDatabase database, int costId) {
        mDb = database;
        mCostId = costId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AddCostViewModel(mDb, mCostId);
    }
}

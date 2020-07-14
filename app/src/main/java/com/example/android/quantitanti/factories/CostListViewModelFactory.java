package com.example.android.quantitanti.factories;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.viewmodels.CostListViewModel;

public class CostListViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final CostDatabase mDb;

    public CostListViewModelFactory(CostDatabase database) {
        mDb = database;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new CostListViewModel(mDb);
    }

}

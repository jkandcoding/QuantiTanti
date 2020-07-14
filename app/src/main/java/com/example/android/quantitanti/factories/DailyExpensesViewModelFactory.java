package com.example.android.quantitanti.factories;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.viewmodels.DailyExpensesViewModel;

public class DailyExpensesViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final CostDatabase dailyExDb;
    private final String dailyExDate;


    public DailyExpensesViewModelFactory(CostDatabase database, String date) {
        dailyExDb = database;
        dailyExDate = date;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DailyExpensesViewModel(dailyExDb, dailyExDate);
    }
}

package com.example.android.quantitanti.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.database.CostEntry;

import java.util.List;

public class DailyExpensesViewModel extends ViewModel {

    // Constant for logging
    private static final String TAG = DailyExpensesViewModel.class.getSimpleName();

    private LiveData<List<CostEntry>> costs;

    public DailyExpensesViewModel(CostDatabase database, String date) {
        costs = database.costDao().loadCostsByDate(date);
    }

    public LiveData<List<CostEntry>> getCosts() {
        return costs;
    }
}

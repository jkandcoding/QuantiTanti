package com.example.android.quantitanti.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.database.CostEntry;

public class AddCostViewModel extends ViewModel {

    private LiveData<CostEntry> cost;

    public AddCostViewModel(CostDatabase database, int costId) {
        cost = database.costDao().loadCostById(costId);
    }

    public LiveData<CostEntry> getCost() {
        return cost;
    }
}

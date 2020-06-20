package com.example.android.quantitanti.models;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.database.CostEntry;

import java.util.List;

public class CostListViewModel extends ViewModel {

    // Constant for logging
    private static final String TAG = CostListViewModel.class.getSimpleName();

    private LiveData<List<CostEntry>> expenses;

    public CostListViewModel(CostDatabase database) {
//        super(application);
//        CostDatabase database = CostDatabase.getInstance(this.getApplication());
//        Log.d(TAG, "Actively retrieving the expenses from the DataBase");
        expenses = database.costDao().loadTotalCosts();
    }

    public LiveData<List<CostEntry>> getDailyExpenses() {
        return expenses;
    }
}

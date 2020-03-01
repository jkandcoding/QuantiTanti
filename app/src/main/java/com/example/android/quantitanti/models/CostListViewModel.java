package com.example.android.quantitanti.models;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.quantitanti.R;
import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.database.CostEntry;
import com.example.android.quantitanti.database.DailyExpensesView;

import java.util.List;

public class CostListViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = CostListViewModel.class.getSimpleName();

    private LiveData<List<CostEntry>> expenses;

    public CostListViewModel(@NonNull Application application) {
        super(application);
        CostDatabase database = CostDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the expenses from the DataBase");
        expenses = database.costDao().loadTotalCosts();
    }



    public LiveData<List<CostEntry>> getDailyExpenses() {
        return expenses;
    }
}

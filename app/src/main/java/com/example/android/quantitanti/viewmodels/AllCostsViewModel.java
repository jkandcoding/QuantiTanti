//package com.example.android.quantitanti.viewmodels;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.ViewModel;
//
//import com.example.android.quantitanti.database.CostDatabase;
//import com.example.android.quantitanti.models.DailyExpenseTagsWithPicsPojo;
//
//import java.util.List;
//
//public class AllCostsViewModel extends ViewModel {
//    private LiveData<List<DailyExpenseTagsWithPicsPojo>> allCosts;
//
//    public AllCostsViewModel(CostDatabase database) {
//        allCosts = database.dailyExpensesDao().loadAllCostsWithTagsAndPicsForFragment();
//    }
//
//    public LiveData<List<DailyExpenseTagsWithPicsPojo>> getAllCosts() {
//        return allCosts;
//    }
//
//
//}

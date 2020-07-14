//package com.example.android.quantitanti.factories;
//
//import androidx.annotation.NonNull;
//import androidx.lifecycle.ViewModel;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.example.android.quantitanti.database.CostDatabase;
//import com.example.android.quantitanti.viewmodels.AllCostsViewModel;
//
//public class AllCostsViewModelFactory extends ViewModelProvider.NewInstanceFactory {
//
//    private final CostDatabase mDb;
//
//    public AllCostsViewModelFactory(CostDatabase database) {
//        mDb = database;
//    }
//
//    @NonNull
//    @Override
//    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//        return (T) new AllCostsViewModel(mDb);
//    }
//}

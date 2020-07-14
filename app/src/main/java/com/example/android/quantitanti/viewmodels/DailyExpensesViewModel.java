package com.example.android.quantitanti.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.models.CostPojo;
import com.example.android.quantitanti.models.DailyExpenseTagsWithPicsPojo;
import com.example.android.quantitanti.models.TotalCostPojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DailyExpensesViewModel extends ViewModel {

    private LiveData<List<DailyExpenseTagsWithPicsPojo>> costs;
    private LiveData<List<CostPojo>> categoryCosts;

    public DailyExpensesViewModel(CostDatabase database, String date) {
        costs = database.dailyExpensesDao().loadCostsWithTagsAndPics(date);
        categoryCosts = database.costDao().loadCategoryCosts(date);
    }

    public LiveData<List<DailyExpenseTagsWithPicsPojo>> getCosts() {
        return costs;
    }

    public LiveData<List<TotalCostPojo>> getTotalCategoryCosts() {
       return Transformations.map(categoryCosts, this::convertToTotalCost);
    }

    private List<TotalCostPojo> convertToTotalCost(List<CostPojo> costPojo) {

        List<TotalCostPojo> totalCostPojos = new ArrayList<>();
        List<String> helpPair = new ArrayList<>();
        List<String> helpCurrency = new ArrayList<>();

        for (CostPojo costPojo1 : costPojo) {
            //new TotalCostPojo, new currency
            if (!helpCurrency.contains(costPojo1.getCurrency())) {
                TotalCostPojo obj = new TotalCostPojo();
                obj.setCurrency(costPojo1.getCurrency());
                Map<String, Integer> catCost = new TreeMap<>();
                catCost.put(costPojo1.getCategory(), costPojo1.getCategoryCosts());
                obj.setCategoryCosts(catCost);
                totalCostPojos.add(obj);
                //fill helper lists:
                helpCurrency.add(costPojo1.getCurrency());
                helpPair.add(costPojo1.getCurrency() + costPojo1.getCategory());
                //---------------------------------------
                // same currency, new category
            } else if (!helpPair.contains(costPojo1.getCurrency() + costPojo1.getCategory())) {
                for (TotalCostPojo tcp : totalCostPojos) {
                    if (tcp.getCurrency().equals(costPojo1.getCurrency())) {
                        Map<String, Integer> catCost = tcp.getCategoryCosts();
                        catCost.put(costPojo1.getCategory(), costPojo1.getCategoryCosts());
                        tcp.setCategoryCosts(catCost);
                        //fill helper lists:
                        helpPair.add(costPojo1.getCurrency() + costPojo1.getCategory());
                        //-------------------
                    }
                }
                //same currency, same category, updating costValue
            } else if (helpPair.contains(costPojo1.getCurrency() + costPojo1.getCategory())) {
                for (TotalCostPojo tcp : totalCostPojos) {
                    if (tcp.getCurrency().equals(costPojo1.getCurrency())) {
                        Map<String, Integer> catCost = tcp.getCategoryCosts();
                       Integer oldValue = catCost.put(costPojo1.getCategory(), costPojo1.getCategoryCosts());
                       if (oldValue != null) {
                           catCost.put(costPojo1.getCategory(), costPojo1.getCategoryCosts() + oldValue);
                           tcp.setCategoryCosts(catCost);
                       }
                    }
                }
            }
        }
        helpPair.clear();
        helpCurrency.clear();
        return totalCostPojos;
    }
}


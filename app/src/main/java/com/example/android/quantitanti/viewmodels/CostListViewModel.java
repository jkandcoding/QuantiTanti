package com.example.android.quantitanti.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.models.DailyExpenseTagsWithPicsPojo;
import com.example.android.quantitanti.models.TotalFrontCostPojo;
import com.example.android.quantitanti.models.TotalFrontHelpPojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CostListViewModel extends ViewModel {

    //TotalCostFragment
    private LiveData<List<TotalFrontHelpPojo>> expenses;

    //AllCostsFragment
    private LiveData<List<DailyExpenseTagsWithPicsPojo>> allCosts;

    public CostListViewModel(CostDatabase database) {
        //TotalCostFragment
        expenses = database.costDao().loadTotalCosts();

        //AllCostsFragment
        allCosts = database.dailyExpensesDao().loadAllCostsWithTagsAndPicsForFragment();
    }

    public LiveData<List<TotalFrontCostPojo>> getDailyExpenses() {
        return Transformations.map(expenses, this::convertToTotalFrontCosts);
    }

    public LiveData<List<DailyExpenseTagsWithPicsPojo>> getAllCosts() {
        return allCosts;
    }

    private List<TotalFrontCostPojo> convertToTotalFrontCosts(List<TotalFrontHelpPojo> totalFrontHelpPojos) {

        List<TotalFrontCostPojo> totalFrontCostPojos = new ArrayList<>();
        List<String> helpPair = new ArrayList<>();  //date + currency
        List<String> helpDate = new ArrayList<>();

        for (TotalFrontHelpPojo totalFrontHelpPojo1 : totalFrontHelpPojos) {
            if (totalFrontHelpPojo1.getCurrency() != null) {        //necessary for costs with currency = null
                //new TotalFrontCostPojo, new date
                if (!helpDate.contains(totalFrontHelpPojo1.getDate())) {
                    TotalFrontCostPojo obj = new TotalFrontCostPojo();
                    obj.setDate(totalFrontHelpPojo1.getDate());
                    Map<String, Integer> curCost = new TreeMap<>();
                    curCost.put(totalFrontHelpPojo1.getCurrency(), totalFrontHelpPojo1.getCost());
                    obj.setFrontCosts(curCost);
                    totalFrontCostPojos.add(obj);
                    //fill helper lists
                    helpDate.add(totalFrontHelpPojo1.getDate());
                    helpPair.add(totalFrontHelpPojo1.getDate() + totalFrontHelpPojo1.getCurrency());
                    //---------------------
                    //same date, new currency
                } else if (!helpPair.contains(totalFrontHelpPojo1.getDate() + totalFrontHelpPojo1.getCurrency())) {
                    for (TotalFrontCostPojo tcp : totalFrontCostPojos) {
                        if (tcp.getDate().equals(totalFrontHelpPojo1.getDate())) {
                            Map<String, Integer> curCost = tcp.getFrontCosts();
                            curCost.put(totalFrontHelpPojo1.getCurrency(), totalFrontHelpPojo1.getCost());
                            tcp.setFrontCosts(curCost);
                            //fill helper list
                            helpPair.add(totalFrontHelpPojo1.getDate() + totalFrontHelpPojo1.getCurrency());
                            //-----------------------
                        }
                    }
                    //same date, same currency, update totalCost
                } else if (helpPair.contains(totalFrontHelpPojo1.getDate() + totalFrontHelpPojo1.getCurrency())) {
                    for (TotalFrontCostPojo tcp : totalFrontCostPojos) {
                        if (tcp.getDate().equals(totalFrontHelpPojo1.getDate())) {
                            Map<String, Integer> curCost = tcp.getFrontCosts();
                            Integer oldValue = curCost.put(totalFrontHelpPojo1.getCurrency(), totalFrontHelpPojo1.getCost());
                            if (oldValue != null) {
                                curCost.put(totalFrontHelpPojo1.getCurrency(), totalFrontHelpPojo1.getCost() + oldValue);
                                tcp.setFrontCosts(curCost);
                            }
                        }
                    }
                }
            }
        }
        helpPair.clear();
        helpDate.clear();
        return totalFrontCostPojos;
    }
}

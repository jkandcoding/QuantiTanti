package com.example.android.quantitanti.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.android.quantitanti.models.CostPojo;
import com.example.android.quantitanti.models.TotalCostPojo;
import com.example.android.quantitanti.models.TotalFrontCostPojo;
import com.example.android.quantitanti.models.TotalFrontHelpPojo;

import java.util.List;

@Dao
public interface CostDao {

    @Insert
    void insertCost(CostEntry costEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCost(CostEntry costEntry);

    @Delete
    void deleteCost(CostEntry costEntry);

    @Query("DELETE FROM expenses WHERE id =:id")
    void deleteCostWithId(int id);


    @Query("SELECT * FROM expenses WHERE id = :id")
    LiveData<CostEntry> loadCostById(int id);

    @Query("SELECT * FROM expenses WHERE date = :date ORDER BY id")
    LiveData<List<CostEntry>> loadCostsByDate(String date);

    @Query("SELECT id FROM expenses ORDER BY id DESC LIMIT 1")
    int loadLastCostId();

    //deleting from CostListActivity
    @Query("DELETE FROM expenses WHERE date = :date")
    void deleteDailyCosts(String date);

    //todo change this to return SUM (cost) in all currencies
    @Query("SELECT date, currency, cost FROM expenses ORDER BY date")
    LiveData<List<TotalFrontHelpPojo>> loadTotalCosts();

    @Query("SELECT DISTINCT currency FROM expenses")
    LiveData<List<String>> loadAllDiffCurrencies();


     //for CostListActivity -> todo OVO CU BRISATI
    @Query("SELECT SUM (cost) AS dailycost FROM expenses WHERE date = :date AND currency = :currency")
    int loadTotalCost(String date, String currency);

    //sum category costs
    @Query("SELECT SUM (cost) AS sumCategory FROM expenses WHERE date = :date AND category = :category AND currency = :currency")
    int loadSumCategoryCost(String date, String category, String currency);

    @Query("SELECT currency, category, cost AS categoryCosts FROM expenses WHERE date = :date")
    LiveData<List<CostPojo>> loadCategoryCosts(String date);

}

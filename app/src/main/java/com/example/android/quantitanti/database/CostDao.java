package com.example.android.quantitanti.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CostDao {

    @Insert
    void insertCost(CostEntry costEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCost(CostEntry costEntry);

    @Delete
    void deleteCost(CostEntry costEntry);

    @Query("SELECT * FROM expenses WHERE id = :id")
    LiveData<CostEntry> loadCostById(int id);

    @Query("SELECT * FROM expenses WHERE date = :date ORDER BY id")
    LiveData<List<CostEntry>> loadCostsByDate(String date);

    //deleting from CostListActivity
    @Query("DELETE FROM expenses WHERE date = :date")
    void deleteDailyCosts(String date);

//    @Query("SELECT * FROM DailyExpensesView ORDER BY oneDate")
//    LiveData<List<DailyExpensesView>> loadTotalCosts();

    @Query("SELECT DISTINCT id, date, SUM (cost) AS cost FROM expenses GROUP BY date ORDER BY date")
    LiveData<List<CostEntry>> loadTotalCosts();

    //for CostListActivity
//    @Query("SELECT dailyCost FROM DailyExpensesView WHERE oneDate = :date")
//    int loadTotalCost(String date);

    //for CostListActivity
    @Query("SELECT SUM (cost) AS dailycost FROM expenses WHERE date = :date")
    int loadTotalCost(String date);

    //sum category costs
    @Query("SELECT SUM (cost) AS sumCategory FROM expenses WHERE date = :date AND category = :category")
    int loadSumCategoryCost(String date, String category);

}

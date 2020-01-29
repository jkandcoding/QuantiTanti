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


    @Query("SELECT * FROM expenses ORDER BY date")
    LiveData<List<CostEntry>> loadAllCosts();

    @Insert
    void insertCost(CostEntry costEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCost(CostEntry costEntry);

    @Delete
    void deleteCost(CostEntry costEntry);

    @Query("SELECT * FROM expenses WHERE id = :id")
    LiveData<CostEntry> loadCostById(int id);

    // upit za dohvat svih troskova unutar jednog datuma
    @Query("SELECT * FROM expenses WHERE date = :date ORDER BY id")
    LiveData<List<CostEntry>> loadCostsByDate(String date);

    // brisanje svih troskova unutar jednog datuma (brisanje s CostListActivitya)
    @Query("DELETE FROM expenses WHERE date = :date")
    void deleteDailyCosts(String date);

    @Query("SELECT * FROM DailyExpensesView ORDER BY oneDate")
    LiveData<List<DailyExpensesView>> loadTotalCosts();

    // upit za dohvat ukupnog troska na datum (za prikaz na CostListActivityu)
    @Query("SELECT dailyCost FROM DailyExpensesView WHERE oneDate = :date")
    int loadTotalCost(String date);

    //sum category costs
    @Query("SELECT SUM (cost) AS sumCategory FROM expenses WHERE date = :date AND category = :category")
    int loadSumCategoryCost(String date, String category);


}

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

    //deleting from CostListActivity
    @Query("DELETE FROM expenses WHERE date = :date")
    void deleteDailyCosts(String date);

    @Query("SELECT id FROM expenses ORDER BY id DESC LIMIT 1")
    int loadLastCostId();

   @Query("SELECT date, currency, cost FROM expenses ORDER BY date DESC")
    LiveData<List<TotalFrontHelpPojo>> loadTotalCosts();

    @Query("SELECT currency, category, cost AS categoryCosts FROM expenses WHERE date = :date")
    LiveData<List<CostPojo>> loadCategoryCosts(String date);

    @Query("SELECT DISTINCT category FROM expenses ORDER BY category")
    LiveData<List<String>> loadCategories();

}

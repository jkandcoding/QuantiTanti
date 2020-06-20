package com.example.android.quantitanti.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface Expenses_tags_join_dao {
    @Insert
    void insert(Expenses_tags_join expenses_tags_join);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Expenses_tags_join expenses_tags_join);

    @Delete
    void delete(Expenses_tags_join expenses_tags_join);

    //prikaz troskova selectano po tagu -> prikaz u novom activity-u
    @Query("SELECT * FROM expenses INNER JOIN expenses_tags_join ON expenses.id = expenses_tags_join.expense_id " +
            "WHERE tag_id = :tag_id")
    List<CostEntry> getExpensesForTag(final int tag_id);

    // svi tagovi nekog troska -> prikaz u AddCostActivityu, kod update-anja troska
    @Query("SELECT tag_id FROM expenses_tags_join WHERE expense_id = :expense_id")
    List<Integer> getTagIdsForCost(int expense_id);



}

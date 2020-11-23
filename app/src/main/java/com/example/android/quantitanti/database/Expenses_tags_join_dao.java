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

    // svi tagovi nekog troska -> prikaz u AddCostActivityu, kod update-anja troska
    @Query("SELECT tag_id FROM expenses_tags_join WHERE expense_id = :expense_id")
    List<Integer> getTagIdsForCost(int expense_id);

    //when deleting tag -> checking if tag is assigned to costs
    @Query("SELECT expense_id FROM expenses_tags_join WHERE tag_id = :tag_id")
    List<Integer> loadExpenseIdsForTag(int tag_id);

    @Query("DELETE FROM expenses_tags_join WHERE tag_id = :tag_id")
    void deleteWithTagId(int tag_id);



}

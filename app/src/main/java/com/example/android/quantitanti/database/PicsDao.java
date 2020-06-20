package com.example.android.quantitanti.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PicsDao {

    @Insert
    void insertPic(PicsEntry picsEntry);

    @Update
    void updatePic(PicsEntry picsEntry);

    @Delete
    void deletePic(PicsEntry picsEntry);

    @Query("SELECT pic_name FROM pics WHERE expense_id = :expense_id")
    List<String > loadPicNamesByCostId(int expense_id);

    @Query("SELECT * FROM pics WHERE expense_id = :expense_id")
    List<PicsEntry> loadPicsByCostId(int expense_id);
}

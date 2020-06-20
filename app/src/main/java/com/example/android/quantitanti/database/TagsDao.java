package com.example.android.quantitanti.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TagsDao {

    @Insert
    void insertTag(TagEntry tagEntry);

    @Update
    void updateTag(TagEntry tagEntry);

    @Delete
    void deleteTag(TagEntry tagEntry);

    @Query("SELECT * FROM tags")
    LiveData<TagEntry> loadAllTags();

    @Query("SELECT tag_name FROM tags")
    LiveData<List<String>> loadNameTags();

    @Query("SELECT tag_id FROM tags WHERE tag_name = :tag_name")
    int loadTagId(String tag_name);

}

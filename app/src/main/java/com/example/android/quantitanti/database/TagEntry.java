package com.example.android.quantitanti.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "tags", indices = {
        @Index(name = "tag_id", value = {"tag_id"}),
        @Index(name = "tag_name", value = {"tag_name"})
})
public class TagEntry {

    @PrimaryKey(autoGenerate = true)
    private int tag_id;
    private String tag_name;

    public TagEntry() {
    }


    @Ignore
    public TagEntry(String tag_name) {
        this.tag_name = tag_name;
    }

    public int getTag_id() {
        return tag_id;
    }

    public void setTag_id(int tag_id) {
        this.tag_id = tag_id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    @Override
    public String toString() {
        return "TagEntry{" +
                "tag_name='" + tag_name + '\'' +
                '}';
    }
}

package com.example.android.quantitanti.database;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "expenses_tags_join",
        primaryKeys = {"expense_id", "tag_id"},
        foreignKeys = {
                @ForeignKey(entity = CostEntry.class,
                        parentColumns = "id",
                        childColumns = "expense_id",
                        onDelete = CASCADE),
                @ForeignKey(entity = TagEntry.class,
                        parentColumns = "tag_id",
                        childColumns = "tag_id")
        },
        indices = {
                @Index(name = "expense_id_join", value = {"expense_id"}),
                @Index(name = "tag_id_join", value = {"tag_id"})
        })

public class Expenses_tags_join {
    public int expense_id;
    public int tag_id;

    public Expenses_tags_join(int expense_id, int tag_id) {
        this.expense_id = expense_id;
        this.tag_id = tag_id;
    }


}

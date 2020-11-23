package com.example.android.quantitanti.models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.android.quantitanti.database.CostEntry;
import com.example.android.quantitanti.database.Expenses_tags_join;
import com.example.android.quantitanti.database.PicsEntry;
import com.example.android.quantitanti.database.TagEntry;

import java.util.List;

//Pojo expenses-tags-pics relation
public class DailyExpenseTagsWithPicsPojo {

    public DailyExpenseTagsWithPicsPojo() {
    }

    @Embedded
    public CostEntry costEntry;

    @Relation(parentColumn = "id",
            entityColumn = "tag_id",
            entity = TagEntry.class,
            projection = {"tag_name"},
            associateBy = @Junction(value = Expenses_tags_join.class,
            parentColumn = "expense_id",
            entityColumn = "tag_id"))
    private List<String> tagNames;

    @Relation(parentColumn = "id",
            entityColumn = "expense_id",
            entity = PicsEntry.class)
    private List<PicsEntry> picsEntries;


    public CostEntry getCostEntry() {
        return costEntry;
    }

    public void setCostEntry(CostEntry costEntry) {
        this.costEntry = costEntry;
    }

    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    public List<PicsEntry> getPicsEntries() {
        return picsEntries;
    }

    public void setPicsEntries(List<PicsEntry> picsEntries) {
        this.picsEntries = picsEntries;
    }
}

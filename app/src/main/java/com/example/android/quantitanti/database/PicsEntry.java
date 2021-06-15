package com.example.android.quantitanti.database;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;


import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "pics",
        foreignKeys = @ForeignKey(entity = CostEntry.class,
                                    parentColumns = "id",
                                    childColumns = "expense_id",
                                    onDelete = CASCADE), indices = {
        @Index(name = "expense_id", value = {"expense_id"})
})
public class PicsEntry {

    @PrimaryKey(autoGenerate = true)
    public int pics_id;
    public String pic_uri;
    public String pic_name;
    public int expense_id;

    public PicsEntry() {
    }

    @Ignore
    public PicsEntry(String pic_uri, String pic_name, int expense_id) {
        this.pic_uri = pic_uri;
        this.pic_name = pic_name;
        this.expense_id = expense_id;
    }

    public int getPics_id() {
        return pics_id;
    }

    public void setPics_id(int pics_id) {
        this.pics_id = pics_id;
    }

    public String getPic_uri() {
        return pic_uri;
    }

    public void setPic_uri(String pic_uri) {
        this.pic_uri = pic_uri;
    }

    public String getPic_name() {
        return pic_name;
    }

    public void setPic_name(String pic_name) {
        this.pic_name = pic_name;
    }

    public int getExpense_id() {
        return expense_id;
    }

    public void setExpense_id(int expense_id) {
        this.expense_id = expense_id;
    }
}

package com.example.android.quantitanti.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses")
public class CostEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String category;
    private String name;
    private int cost;
    private String date;


    //todo upisi kategorije
    /**
     * Possible values for the categories
     */
    @Ignore
    public static final String CATEGORY_1 = "Car";
    @Ignore
    public static final String CATEGORY_2 = "Clothes";
    @Ignore
    public static final String CATEGORY_3 = "Food";
    @Ignore
    public static final String CATEGORY_4 = "Utilities";
    @Ignore
    public static final String CATEGORY_5 ="Groceries";
    @Ignore
    public static final String CATEGORY_6 = "Education";
    @Ignore
    public static final String CATEGORY_7 = "Sport";
    @Ignore
    public static final String CATEGORY_8 = "Cosmetics";
    @Ignore
    public static final String CATEGORY_9 = "Other";

    public CostEntry(int id, String category, String name, int cost, String date) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.cost = cost;
        this.date = date;
    }

    @Ignore
    public CostEntry(String category, String name, int cost, String date) {
        this.category = category;
        this.name = name;
        this.cost = cost;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}

package com.example.android.quantitanti.database;

import androidx.room.DatabaseView;

@DatabaseView("SELECT DISTINCT date AS oneDate, SUM (cost) AS dailyCost FROM expenses" +
        " GROUP BY date ORDER BY date")

public class DailyExpensesView {

    public String oneDate;
    public int dailyCost;


    public String getOneDate() {
        return oneDate;
    }

    public int getDailyCost() {
        return dailyCost;
    }
}


package com.example.android.quantitanti.models;

import java.util.Map;

public class TotalFrontCostPojo {

    private String date;
    private Map<String, Integer> frontCosts;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Integer> getFrontCosts() {
        return frontCosts;
    }

    public void setFrontCosts(Map<String, Integer> frontCosts) {
        this.frontCosts = frontCosts;
    }
}

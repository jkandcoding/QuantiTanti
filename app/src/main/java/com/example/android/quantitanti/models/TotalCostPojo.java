package com.example.android.quantitanti.models;

import java.util.Map;

public class TotalCostPojo {

    public TotalCostPojo() {
    }

    private String currency;
    private Map<String, Integer> categoryCosts;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Map<String, Integer> getCategoryCosts() {
        return categoryCosts;
    }

    public void setCategoryCosts(Map<String, Integer> categoryCosts) {
        this.categoryCosts = categoryCosts;
    }
}

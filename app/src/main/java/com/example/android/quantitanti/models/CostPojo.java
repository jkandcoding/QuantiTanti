package com.example.android.quantitanti.models;

public class CostPojo {

    private String currency;
    private String category;
    private int categoryCosts;

    public CostPojo() {
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCategoryCosts() {
        return categoryCosts;
    }

    public void setCategoryCosts(int categoryCosts) {
        this.categoryCosts = categoryCosts;
    }
}

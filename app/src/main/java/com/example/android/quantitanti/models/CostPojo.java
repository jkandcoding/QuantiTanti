package com.example.android.quantitanti.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

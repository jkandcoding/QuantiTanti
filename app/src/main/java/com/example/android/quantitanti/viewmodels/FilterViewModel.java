package com.example.android.quantitanti.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android.quantitanti.database.CostDatabase;

import java.util.ArrayList;
import java.util.List;

public class FilterViewModel extends AndroidViewModel {

    private LiveData<List<String>> categoryNames;

    private List<String> categoriesForFilter;

    private LiveData<List<String>> tagNames;

    private List<String> tagsForFilter;


    public FilterViewModel(@NonNull Application application) {
        super(application);

        CostDatabase database = CostDatabase.getInstance(this.getApplication());
        categoryNames = database.costDao().loadCategories();
        tagNames = database.tagsDao().loadNameTags();
    }

    public LiveData<List<String>> getCategoryNames() {
        return categoryNames;
    }

    public LiveData<List<String>> getTagNames() {
        return tagNames;
    }

    public List<String> getCategoriesForFilter() {
        return categoriesForFilter;
    }

    public void setCategoriesForFilter(List<String> categoriesForFilter) {
        this.categoriesForFilter = categoriesForFilter;
    }

    public List<String> getTagsForFilter() {
        return tagsForFilter;
    }

    public void setTagsForFilter(List<String> tagsForFilter) {
        this.tagsForFilter = tagsForFilter;
    }
}

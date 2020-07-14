package com.example.android.quantitanti.models;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.quantitanti.database.CostDatabase;

import java.util.List;

public class TagsViewModel extends AndroidViewModel {

    private LiveData<List<String>> tagNames;

    public TagsViewModel(Application application) {
        super(application);

        CostDatabase database = CostDatabase.getInstance(this.getApplication());
        tagNames = database.tagsDao().loadNameTags();
    }

    public LiveData<List<String>> getTagNames() {
        return tagNames;
    }
}

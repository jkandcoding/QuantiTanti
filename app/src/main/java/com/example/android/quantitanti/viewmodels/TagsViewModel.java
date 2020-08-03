package com.example.android.quantitanti.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.models.TagsPojo;

import java.util.ArrayList;
import java.util.List;

public class TagsViewModel extends AndroidViewModel {

    private LiveData<List<String>> tagNames;

    public TagsViewModel(Application application) {
        super(application);

        CostDatabase database = CostDatabase.getInstance(this.getApplication());
        tagNames = database.tagsDao().loadNameTags();
    }

    public LiveData<List<TagsPojo>> getTagsForDialog() {
        return Transformations.map(tagNames, this::convertToTagsPojo);
    }

    private List<TagsPojo> convertToTagsPojo(List<String> tagNames) {
        List<TagsPojo> tags = new ArrayList<>();
        tags.clear();
        for (String s : tagNames) {
            TagsPojo tag = new TagsPojo(s);
            tag.setTagName(s);
            tags.add(tag);
        }
        return tags;
    }

}

package com.example.android.quantitanti.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.database.TagEntry;
import com.example.android.quantitanti.models.TagsPojo;

import java.util.ArrayList;
import java.util.List;

public class TagsViewModel extends AndroidViewModel {

    private LiveData<List<String>> tagNames;
    private LiveData<List<TagEntry>> tagsAllForDialog;

    public TagsViewModel(Application application) {
        super(application);

        CostDatabase database = CostDatabase.getInstance(this.getApplication());
        tagNames = database.tagsDao().loadNameTags();
        tagsAllForDialog = database.tagsDao().loadAllTags();
    }

    public LiveData<List<TagsPojo>> getTagsForDialog() {
        return Transformations.map(tagsAllForDialog, this::convertToTagsPojo);
    }

    private List<TagsPojo> convertToTagsPojo(List<TagEntry> tagsAllForDialog) {
        List<TagsPojo> tags = new ArrayList<>();
        tags.clear();
        for (TagEntry s : tagsAllForDialog) {
            TagsPojo tag = new TagsPojo(s.getTag_id(), s.getTag_name());
            tag.setTagId(s.getTag_id());
            tag.setTagName(s.getTag_name());
            tags.add(tag);
        }
        return tags;
    }
}

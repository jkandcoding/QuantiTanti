package com.example.android.quantitanti.models;

public class TagsPojo {

    private String tagName;
    private boolean selected = false;


    public TagsPojo(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

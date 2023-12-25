package com.templatemela.smartpdfreader.model;

import ja.burhanrashid52.photoeditor.PhotoFilter;

public class FilterItem {
    private PhotoFilter mFilter;
    private int mImageId;
    private String mName;

    public FilterItem(int mImageId, String mName, PhotoFilter mFilter) {
        this.mImageId = mImageId;
        this.mName = mName;
        this.mFilter = mFilter;
    }

    public int getImageId() {
        return this.mImageId;
    }

    public String getName() {
        return this.mName;
    }

    public PhotoFilter getFilter() {
        return this.mFilter;
    }
}

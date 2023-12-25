package com.templatemela.smartpdfreader.model;

public class HomePageItem {
    private final int mDrawableId;
    private final int mIconId;
    private final int mTitleString;

    public HomePageItem(int mIconId, int mDrawableId, int mTitleString) {
        this.mIconId = mIconId;
        this.mDrawableId = mDrawableId;
        this.mTitleString = mTitleString;
    }

    public int getNavigationItemId() {
        return this.mIconId;
    }

    public int getTitleString() {
        return this.mTitleString;
    }

    public int getmDrawableId() {
        return this.mDrawableId;
    }
}

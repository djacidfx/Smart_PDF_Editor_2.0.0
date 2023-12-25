package com.templatemela.smartpdfreader.model;

public class PreviewImageOptionItem {
    private int mOptionImageId;
    private String mOptionName;

    public PreviewImageOptionItem(int optionId, String name) {
        this.mOptionImageId = optionId;
        this.mOptionName = name;
    }

    public int getOptionImageId() {
        return this.mOptionImageId;
    }

    public void setOptionImageId(int optionImageId) {
        this.mOptionImageId = optionImageId;
    }

    public String getOptionName() {
        return this.mOptionName;
    }

    public void setOptionName(String optionName) {
        this.mOptionName = optionName;
    }
}

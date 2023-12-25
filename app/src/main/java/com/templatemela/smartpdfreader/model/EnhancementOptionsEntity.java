package com.templatemela.smartpdfreader.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class EnhancementOptionsEntity {
    private Drawable mImage;
    private String mName;

    public EnhancementOptionsEntity(Drawable drawable, String str) {
        this.mImage = drawable;
        this.mName = str;
    }

    public EnhancementOptionsEntity(Context context, int i, String str) {
        this.mImage = context.getResources().getDrawable(i);
        this.mName = str;
    }

    public EnhancementOptionsEntity(Context context, int i, int i2) {
        this.mImage = context.getResources().getDrawable(i);
        this.mName = context.getString(i2);
    }

    public Drawable getImage() {
        return this.mImage;
    }

    public void setImage(Drawable drawable) {
        this.mImage = drawable;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String str) {
        this.mName = str;
    }
}

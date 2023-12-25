package com.templatemela.smartpdfreader.model;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;

public class Watermark {
    private Font.FontFamily mFontFamily;
    private int mFontStyle;
    private int mRotationAngle;
    private BaseColor mTextColor;
    private int mTextSize;
    private String mWatermarkText;

    public String getWatermarkText() {
        return this.mWatermarkText;
    }

    public void setWatermarkText(String watermarkText) {
        this.mWatermarkText = watermarkText;
    }

    public int getRotationAngle() {
        return this.mRotationAngle;
    }

    public void setRotationAngle(int rotationAngle) {
        this.mRotationAngle = rotationAngle;
    }

    public BaseColor getTextColor() {
        return this.mTextColor;
    }

    public void setTextColor(BaseColor baseColor) {
        this.mTextColor = baseColor;
    }

    public int getTextSize() {
        return this.mTextSize;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    public Font.FontFamily getFontFamily() {
        return this.mFontFamily;
    }

    public void setFontFamily(Font.FontFamily fontFamily) {
        this.mFontFamily = fontFamily;
    }

    public int getFontStyle() {
        return this.mFontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.mFontStyle = fontStyle;
    }
}

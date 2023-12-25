package com.templatemela.smartpdfreader.model;

import java.util.ArrayList;

public class ImageToPDFOptions extends PDFOptions {
    private String mImageScaleType;
    private ArrayList<String> mImagesUri;
    private int mMarginBottom = 0;
    private int mMarginLeft = 0;
    private int mMarginRight = 0;
    private int mMarginTop = 0;
    private String mMasterPwd;
    private String mPageNumStyle;
    private String mQualityString;

    public ImageToPDFOptions() {
        setPasswordProtected(false);
        setWatermarkAdded(false);
        setBorderWidth(0);
    }

    public ImageToPDFOptions(String str, String str2, boolean z, String str3, String str4, int i, String str5, ArrayList<String> arrayList, boolean z2, Watermark watermark, int i2) {
        super(str, str2, z, str3, i, z2, watermark, i2);
        this.mQualityString = str4;
        this.mImagesUri = arrayList;
        this.mMasterPwd = str5;
    }

    public String getQualityString() {
        return this.mQualityString;
    }

    public ArrayList<String> getImagesUri() {
        return this.mImagesUri;
    }

    public void setQualityString(String mQualityString) {
        this.mQualityString = mQualityString;
    }

    public void setImagesUri(ArrayList<String> arrayList) {
        this.mImagesUri = arrayList;
    }

    public void setMargins(int mMarginTop, int mMarginBottom, int mMarginRight, int mMarginLeft) {
        this.mMarginTop = mMarginTop;
        this.mMarginBottom = mMarginBottom;
        this.mMarginRight = mMarginRight;
        this.mMarginLeft = mMarginLeft;
    }

    public void setMasterPwd(String masterPwd) {
        this.mMasterPwd = masterPwd;
    }

    public int getMarginTop() {
        return this.mMarginTop;
    }

    public int getMarginBottom() {
        return this.mMarginBottom;
    }

    public int getMarginRight() {
        return this.mMarginRight;
    }

    public int getMarginLeft() {
        return this.mMarginLeft;
    }

    public String getImageScaleType() {
        return this.mImageScaleType;
    }

    public void setImageScaleType(String imageScaleType) {
        this.mImageScaleType = imageScaleType;
    }

    public String getPageNumStyle() {
        return this.mPageNumStyle;
    }

    public void setPageNumStyle(String pageNumStyle) {
        this.mPageNumStyle = pageNumStyle;
    }

    public String getMasterPwd() {
        return this.mMasterPwd;
    }
}

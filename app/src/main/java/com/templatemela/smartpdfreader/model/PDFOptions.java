package com.templatemela.smartpdfreader.model;

public class PDFOptions {
    private int mBorderWidth;
    private String mOutFileName;
    private int mPageColor;
    private String mPageSize;
    private String mPassword;
    private boolean mPasswordProtected;
    private Watermark mWatermark;
    private boolean mWatermarkAdded;

    PDFOptions() {
    }

    PDFOptions(String mOutFileName, String mPageSize, boolean mPasswordProtected, String mPassword, int mBorderWidth, boolean mWatermarkAdded, Watermark watermark, int mPageColor) {
        this.mOutFileName = mOutFileName;
        this.mPageSize = mPageSize;
        this.mPasswordProtected = mPasswordProtected;
        this.mPassword = mPassword;
        this.mBorderWidth = mBorderWidth;
        this.mWatermarkAdded = mWatermarkAdded;
        this.mWatermark = watermark;
        this.mPageColor = mPageColor;
    }

    public String getOutFileName() {
        return this.mOutFileName;
    }

    public String getPageSize() {
        return this.mPageSize;
    }

    public boolean isPasswordProtected() {
        return this.mPasswordProtected;
    }

    public String getPassword() {
        return this.mPassword;
    }

    public int getBorderWidth() {
        return this.mBorderWidth;
    }

    public void setOutFileName(String outFileName) {
        this.mOutFileName = outFileName;
    }

    public void setPasswordProtected(boolean passwordProtected) {
        this.mPasswordProtected = passwordProtected;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public void setPageSize(String pageSize) {
        this.mPageSize = pageSize;
    }

    public void setBorderWidth(int borderWidth) {
        this.mBorderWidth = borderWidth;
    }

    public boolean isWatermarkAdded() {
        return this.mWatermarkAdded;
    }

    public void setWatermarkAdded(boolean watermarkAdded) {
        this.mWatermarkAdded = watermarkAdded;
    }

    public Watermark getWatermark() {
        return this.mWatermark;
    }

    public void setWatermark(Watermark watermark) {
        this.mWatermark = watermark;
    }

    public int getPageColor() {
        return this.mPageColor;
    }

    public void setPageColor(int pageColor) {
        this.mPageColor = pageColor;
    }
}

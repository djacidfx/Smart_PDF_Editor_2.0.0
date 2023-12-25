package com.templatemela.smartpdfreader.model;

import android.content.Context;
import android.net.Uri;

import com.itextpdf.text.Font;
import com.templatemela.smartpdfreader.preferences.TextToPdfPreferences;

public class TextToPDFOptions extends PDFOptions {
    private final int mFontColor;
    private final Font.FontFamily mFontFamily;
    private final int mFontSize;
    private final Uri mInFileUri;

    public TextToPDFOptions(String name, String size, boolean isPassProtected, String mPassword, Uri mInFileUri, int mFontSize, Font.FontFamily mFontFamily, int mFontColor, int mPageColor) {
        super(name, size, isPassProtected, mPassword, 0, false, null, mPageColor);
        this.mInFileUri = mInFileUri;
        this.mFontSize = mFontSize;
        this.mFontFamily = mFontFamily;
        this.mFontColor = mFontColor;
    }

    public Uri getInFileUri() {
        return this.mInFileUri;
    }

    public int getFontSize() {
        return this.mFontSize;
    }

    public Font.FontFamily getFontFamily() {
        return this.mFontFamily;
    }

    public int getFontColor() {
        return this.mFontColor;
    }

    public static class Builder {
        private String mFileName;
        private int mFontColor;
        private Font.FontFamily mFontFamily;
        private int mFontSize;
        private String mFontSizeTitle;
        private Uri mInFileUri;
        private int mPageColor;
        private String mPageSize;
        private String mPassword;
        private boolean mPasswordProtected = false;

        public Builder(Context context) {
            TextToPdfPreferences textToPdfPreferences = new TextToPdfPreferences(context);
            this.mPageSize = textToPdfPreferences.getPageSize();
            this.mFontColor = textToPdfPreferences.getFontColor();
            this.mFontFamily = Font.FontFamily.valueOf(textToPdfPreferences.getFontFamily());
            this.mFontSize = textToPdfPreferences.getFontSize();
            this.mPageColor = textToPdfPreferences.getPageColor();
        }

        public String getFileName() {
            return this.mFileName;
        }

        public Builder setFileName(String fileName) {
            this.mFileName = fileName;
            return this;
        }

        public String getPageSize() {
            return this.mPageSize;
        }

        public Builder setPageSize(String pageSize) {
            this.mPageSize = pageSize;
            return this;
        }

        public Builder setPasswordProtected(boolean passwordProtected) {
            this.mPasswordProtected = passwordProtected;
            return this;
        }

        public String getPassword() {
            return this.mPassword;
        }

        public Builder setPassword(String password) {
            this.mPassword = password;
            return this;
        }

        public int getPageColor() {
            return this.mPageColor;
        }

        public Builder setPageColor(int pageColor) {
            this.mPageColor = pageColor;
            return this;
        }

        public Builder setInFileUri(Uri inFileUri) {
            this.mInFileUri = inFileUri;
            return this;
        }

        public int getFontSize() {
            return this.mFontSize;
        }

        public Builder setFontSize(int fontSize) {
            this.mFontSize = fontSize;
            return this;
        }

        public Font.FontFamily getFontFamily() {
            return this.mFontFamily;
        }

        public Builder setFontFamily(Font.FontFamily fontFamily) {
            this.mFontFamily = fontFamily;
            return this;
        }

        public int getFontColor() {
            return this.mFontColor;
        }

        public Builder setFontColor(int fontColor) {
            this.mFontColor = fontColor;
            return this;
        }

        public String getFontSizeTitle() {
            return this.mFontSizeTitle;
        }

        public Builder setFontSizeTitle(String fontSizeTitle) {
            this.mFontSizeTitle = fontSizeTitle;
            return this;
        }

        public TextToPDFOptions build() {
            return new TextToPDFOptions(mFileName, mPageSize, mPasswordProtected, mPassword, mInFileUri, mFontSize, mFontFamily, mFontColor, mPageColor);
        }
    }
}

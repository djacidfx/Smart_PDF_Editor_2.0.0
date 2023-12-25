package com.templatemela.smartpdfreader.model;

import java.io.File;

public class PDFFile {
    private boolean mIsEncrypted;
    private File mPdfFile;

    public PDFFile(File file, boolean isEncrypted) {
        this.mPdfFile = file;
        this.mIsEncrypted = isEncrypted;
    }

    public File getPdfFile() {
        return this.mPdfFile;
    }

    public void setPdfFile(File file) {
        this.mPdfFile = file;
    }

    public boolean isEncrypted() {
        return this.mIsEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.mIsEncrypted = encrypted;
    }
}

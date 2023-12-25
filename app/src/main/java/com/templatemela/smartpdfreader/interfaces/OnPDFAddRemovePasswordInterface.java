package com.templatemela.smartpdfreader.interfaces;

public interface OnPDFAddRemovePasswordInterface {
    void pdfEnded(String name, Boolean isSuccess);

    void pdfStarted();
}

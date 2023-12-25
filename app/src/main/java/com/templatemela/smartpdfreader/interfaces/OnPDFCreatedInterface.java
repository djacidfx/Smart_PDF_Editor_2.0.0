package com.templatemela.smartpdfreader.interfaces;

public interface OnPDFCreatedInterface {
    void onPDFCreated(boolean isCreated, String path);

    void onPDFCreationStarted();
}

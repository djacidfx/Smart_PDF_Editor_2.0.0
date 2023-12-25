package com.templatemela.smartpdfreader.interfaces;

public interface MergeFilesListener {
    void mergeStarted();

    void resetValues(boolean isPDFMerged, String path);
}

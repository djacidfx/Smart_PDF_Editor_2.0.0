package com.templatemela.smartpdfreader.interfaces;

import java.util.ArrayList;

public interface ExtractImagesListener {
    void extractionStarted();

    void resetView();

    void updateView(int i, ArrayList<String> arrayList);
}

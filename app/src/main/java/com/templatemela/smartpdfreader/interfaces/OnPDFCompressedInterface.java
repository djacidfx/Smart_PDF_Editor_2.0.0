package com.templatemela.smartpdfreader.interfaces;

public interface OnPDFCompressedInterface {
    void pdfCompressionEnded(String path, Boolean isSuccess);

    void pdfCompressionStarted();
}

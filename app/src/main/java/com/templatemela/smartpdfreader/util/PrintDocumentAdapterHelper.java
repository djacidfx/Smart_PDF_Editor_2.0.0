package com.templatemela.smartpdfreader.util;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class PrintDocumentAdapterHelper extends PrintDocumentAdapter {
    private File mFile;

    PrintDocumentAdapterHelper(File file) {
        this.mFile = file;
    }

    public void onWrite(PageRange[] pageRangeArr, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, PrintDocumentAdapter.WriteResultCallback writeResultCallback) {
        try {
            FileInputStream fileInputStream = new FileInputStream(this.mFile.getName());
            FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
            byte[] bArr = new byte[1024];
            while (true) {
                int read = fileInputStream.read(bArr);
                if (read > 0) {
                    fileOutputStream.write(bArr, 0, read);
                } else {
                    writeResultCallback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                    fileInputStream.close();
                    fileOutputStream.close();
                    return;
                }
            }
        } catch (Exception exception) {
        }
    }

    @Override
    public void onLayout(PrintAttributes attributes, PrintAttributes printAttributes2, CancellationSignal signal, PrintDocumentAdapter.LayoutResultCallback layoutResultCallback, Bundle bundle) {

        if (signal.isCanceled()) {
            layoutResultCallback.onLayoutCancelled();
        } else {
            layoutResultCallback.onLayoutFinished(new PrintDocumentInfo.Builder("myFile").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build(), true);
        }
    }
}

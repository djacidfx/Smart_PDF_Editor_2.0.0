package com.templatemela.smartpdfreader.util;

import android.content.Context;
import android.net.Uri;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import java.io.InputStream;

public abstract class FileReader {
    Context mContext;

    
    public abstract void createDocumentFromStream(Uri uri, Document document, Font font, InputStream inputStream) throws Exception;

    public FileReader(Context context) {
        mContext = context;
    }


    public void read(Uri uri, Document document, Font font) {
        try {
            InputStream openInputStream = mContext.getContentResolver().openInputStream(uri);
            if (openInputStream != null) {
                createDocumentFromStream(uri, document, font, openInputStream);
                openInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

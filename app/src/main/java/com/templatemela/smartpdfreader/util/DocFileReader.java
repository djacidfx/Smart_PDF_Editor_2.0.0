package com.templatemela.smartpdfreader.util;

import android.content.Context;
import android.net.Uri;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import java.io.InputStream;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

public class DocFileReader extends FileReader {
    public DocFileReader(Context context) {
        super(context);
    }

    @Override
    public void createDocumentFromStream(Uri uri, Document document, Font font, InputStream inputStream) throws Exception {
        String text = new WordExtractor(new HWPFDocument(inputStream)).getText();
        Paragraph paragraph = new Paragraph(text + "\n", font);
        paragraph.setAlignment(3);
        document.add(paragraph);
    }
}

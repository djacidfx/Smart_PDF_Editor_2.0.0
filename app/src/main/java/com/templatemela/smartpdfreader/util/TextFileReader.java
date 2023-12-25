package com.templatemela.smartpdfreader.util;

import android.content.Context;
import android.net.Uri;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class TextFileReader extends FileReader {
    public TextFileReader(Context context) {
        super(context);
    }

    
    public void createDocumentFromStream(Uri uri, Document document, Font font, InputStream inputStream) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                PrintStream printStream = System.out;
                printStream.println("line = " + readLine);
                Paragraph paragraph = new Paragraph(readLine + "\n", font);
                paragraph.setAlignment(3);
                document.add(paragraph);
            } else {
                bufferedReader.close();
                return;
            }
        }
    }
}

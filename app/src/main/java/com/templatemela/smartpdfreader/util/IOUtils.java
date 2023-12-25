package com.templatemela.smartpdfreader.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
    private static int BUFFER_SIZE = 231;

    public static int copy2(InputStream inputStream, OutputStream outputStream) throws Exception, IOException {
        byte[] bArr = new byte[BUFFER_SIZE];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, BUFFER_SIZE);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, BUFFER_SIZE);
        int i = 0;
        while (true) {
            try {
                int read = bufferedInputStream.read(bArr, 0, BUFFER_SIZE);
                if (read == -1) {
                    break;
                }
                bufferedOutputStream.write(bArr, 0, read);
                i += read;
            } finally {
                try {
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    Log.e(e.getMessage(), e.toString());
                }
                try {
                    bufferedInputStream.close();
                } catch (IOException e2) {
                    Log.e(e2.getMessage(), e2.toString());
                }
            }
        }
        bufferedOutputStream.flush();
        return i;
    }
}

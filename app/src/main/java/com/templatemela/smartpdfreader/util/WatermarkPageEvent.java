package com.templatemela.smartpdfreader.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.templatemela.smartpdfreader.model.Watermark;

class WatermarkPageEvent extends PdfPageEventHelper {
    private Phrase mPhrase;
    private Watermark mWatermark;

    WatermarkPageEvent() {
    }

    @Override
    public void onEndPage(PdfWriter pdfWriter, Document document) {
        ColumnText.showTextAligned(pdfWriter.getDirectContent(), 1, this.mPhrase, (document.getPageSize().getLeft() + document.getPageSize().getRight()) / 2.0f, (document.getPageSize().getTop() + document.getPageSize().getBottom()) / 2.0f, (float) mWatermark.getRotationAngle());
    }

    public Watermark getWatermark() {
        return this.mWatermark;
    }

    public void setWatermark(Watermark watermark) {
        mWatermark = watermark;
        mPhrase = new Phrase(mWatermark.getWatermarkText(), new Font(mWatermark.getFontFamily(), (float) mWatermark.getTextSize(), this.mWatermark.getFontStyle(), this.mWatermark.getTextColor()));
    }
}

package com.templatemela.smartpdfreader.util;

import android.app.Activity;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.database.DatabaseHelper;
import com.templatemela.smartpdfreader.interfaces.DataSetChanged;
import com.templatemela.smartpdfreader.model.Watermark;

import java.io.FileOutputStream;
import java.io.IOException;

public class WatermarkUtils {

    public final Activity mContext;
    private final FileUtils mFileUtils;

    public Watermark mWatermark;

    public static String getStyleNameFromFont(int i) {
        return i != 1 ? i != 2 ? i != 3 ? i != 4 ? i != 8 ? "NORMAL" : "STRIKETHRU" : Chunk.UNDERLINE : "BOLDITALIC" : "ITALIC" : "BOLD";
    }

    public WatermarkUtils(Activity activity) {
        this.mContext = activity;
        this.mFileUtils = new FileUtils(activity);
    }

    public void setWatermark(String name, DataSetChanged dataSetChanged) {
        MaterialDialog build = new MaterialDialog.Builder(mContext).title(R.string.add_watermark).customView(R.layout.add_watermark_dialog, true).positiveText(android.R.string.ok).negativeText(android.R.string.cancel).build();
        final MDButton actionButton = build.getActionButton(DialogAction.POSITIVE);
        mWatermark = new Watermark();
        EditText edtText = build.getCustomView().findViewById(R.id.watermarkText);
        EditText edtAngel = build.getCustomView().findViewById(R.id.watermarkAngle);
        EditText edtSize = build.getCustomView().findViewById(R.id.watermarkFontSize);
        Spinner spnFamily = build.getCustomView().findViewById(R.id.watermarkFontFamily);
        Spinner spnStyle = build.getCustomView().findViewById(R.id.watermarkStyle);
        edtAngel.setFilters(new InputFilter[]{new InputFilterMinMax("1", "360")});
        edtSize.setFilters(new InputFilter[]{new InputFilterMinMax("1", "36")});
        spnFamily.setAdapter(new ArrayAdapter(mContext, android.R.layout.simple_spinner_dropdown_item, Font.FontFamily.values()));

        spnStyle.setAdapter(new ArrayAdapter(mContext, android.R.layout.simple_spinner_dropdown_item, mContext.getResources().getStringArray(R.array.fontStyles)));
        edtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                actionButton.setEnabled(charSequence.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtils.getInstance().isEmpty(editable)) {
                    StringUtils.getInstance().showSnackbar(mContext, R.string.snackbar_watermark_cannot_be_blank);
                } else {
                    mWatermark.setWatermarkText(editable.toString());
                }
            }
        });
        actionButton.setEnabled(false);
        actionButton.setOnClickListener(view -> addWatermark(edtText, spnFamily, spnStyle, edtAngel, edtSize, build.getCustomView().findViewById(R.id.watermarkColor), name, dataSetChanged, build));
        build.show();
    }

    public void addWatermark(EditText editText, Spinner spinner, Spinner spinner2, EditText editText2, EditText editText3, ColorPickerView colorPickerView, String name, DataSetChanged dataSetChanged, MaterialDialog materialDialog) {
        try {
            mWatermark.setWatermarkText(editText.getText().toString());
            mWatermark.setFontFamily((Font.FontFamily) spinner.getSelectedItem());
            mWatermark.setFontStyle(getStyleValueFromName((String) spinner2.getSelectedItem()));
            if (StringUtils.getInstance().isEmpty(editText2.getText())) {
                mWatermark.setRotationAngle(0);
            } else {
                mWatermark.setRotationAngle(Integer.parseInt(editText2.getText().toString()));
            }
            if (StringUtils.getInstance().isEmpty(editText3.getText())) {
                mWatermark.setTextSize(50);
            } else {
                mWatermark.setTextSize(Integer.parseInt(editText3.getText().toString()));
            }
            mWatermark.setTextColor(new BaseColor(Color.red(colorPickerView.getColor()), Color.green(colorPickerView.getColor()), Color.blue(colorPickerView.getColor()), Color.alpha(colorPickerView.getColor())));
            String createWatermark = createWatermark(name);
            dataSetChanged.updateDataset();
            StringUtils.getInstance().getSnackbarwithAction(this.mContext, R.string.watermark_added)
                    .setAction(R.string.snackbar_viewAction, view1 -> mFileUtils.openFile(createWatermark, FileUtils.FileType.e_PDF)).show();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            StringUtils.getInstance().showSnackbar(this.mContext, R.string.cannot_add_watermark);
        }
        materialDialog.dismiss();
    }

    private String createWatermark(String wm) throws IOException, DocumentException {
        String uniqueFileName = mFileUtils.getUniqueFileName(wm.replace(this.mContext.getString(R.string.pdf_ext), this.mContext.getString(R.string.watermarked_file)));
        PdfReader pdfReader = new PdfReader(wm);
        PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(uniqueFileName));
        Phrase phrase = new Phrase(mWatermark.getWatermarkText(), new Font(mWatermark.getFontFamily(), (float) mWatermark.getTextSize(), mWatermark.getFontStyle(), mWatermark.getTextColor()));
        int numberOfPages = pdfReader.getNumberOfPages();
        for (int i = 1; i <= numberOfPages; i++) {
            Rectangle pageSizeWithRotation = pdfReader.getPageSizeWithRotation(i);
            ColumnText.showTextAligned(pdfStamper.getOverContent(i), 1, phrase, (pageSizeWithRotation.getLeft() + pageSizeWithRotation.getRight()) / 2.0f, (pageSizeWithRotation.getTop() + pageSizeWithRotation.getBottom()) / 2.0f, (float) mWatermark.getRotationAngle());
        }
        pdfStamper.close();
        pdfReader.close();
        new DatabaseHelper(mContext).insertRecord(uniqueFileName, this.mContext.getString(R.string.watermarked));
        return uniqueFileName;
    }

    public static int getStyleValueFromName(String fontFamily) {
        fontFamily.hashCode();
        char c = 65535;
        switch (fontFamily.hashCode()) {
            case -2125451728:
                if (fontFamily.equals("ITALIC")) {
                    c = 0;
                    break;
                }
                break;
            case -1174769047:
                if (fontFamily.equals("STRIKETHRU")) {
                    c = 1;
                    break;
                }
                break;
            case 2044549:
                if (fontFamily.equals("BOLD")) {
                    c = 2;
                    break;
                }
                break;
            case 559851765:
                if (fontFamily.equals("BOLDITALIC")) {
                    c = 3;
                    break;
                }
                break;
            case 1759631020:
                if (fontFamily.equals(Chunk.UNDERLINE)) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 2;
            case 1:
                return 8;
            case 2:
                return 1;
            case 3:
                return 3;
            case 4:
                return 4;
            default:
                return 0;
        }
    }
}

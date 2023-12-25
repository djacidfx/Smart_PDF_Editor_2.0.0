package com.templatemela.smartpdfreader.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.morphingbutton.MorphingButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.ads.AdsService;
import com.templatemela.smartpdfreader.customviews.MyCardView;
import com.templatemela.smartpdfreader.database.DatabaseHelper;
import com.templatemela.smartpdfreader.interfaces.OnPDFCreatedInterface;
import com.templatemela.smartpdfreader.model.ImageToPDFOptions;
import com.templatemela.smartpdfreader.model.TextToPDFOptions;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.DialogUtils;
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.PageSizeUtils;
import com.templatemela.smartpdfreader.util.PermissionsUtils;
import com.templatemela.smartpdfreader.util.StringUtils;
import com.templatemela.smartpdfreader.util.TextToPDFUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QrBarcodeScanFragment extends Fragment implements View.OnClickListener, OnPDFCreatedInterface {
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    private Activity mActivity;
    private FileUtils mFileUtils;
    private int mFontColor;
    private Font.FontFamily mFontFamily;
    private MaterialDialog mMaterialDialog;
    @BindView(R.id.pdfOpen)
    MorphingButton mOpenPdf;
    private String mPath;
    private SharedPreferences mSharedPreferences;
    private final String mTempFileName = "scan_result_temp.txt";
    @BindView(R.id.scan_barcode)
    MyCardView scanBarcode;
    @BindView(R.id.scan_qrcode)
    MyCardView scanQrcode;
    int whichOperation = 1;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_qrcode_barcode, viewGroup, false);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        ButterKnife.bind(this, inflate);
        scanQrcode.setOnClickListener(this);
        scanBarcode.setOnClickListener(this);
        mFontFamily = Font.FontFamily.valueOf(mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT, Constants.DEFAULT_FONT_FAMILY));
        mFontColor = mSharedPreferences.getInt(Constants.DEFAULT_FONT_COLOR_TEXT, Constants.DEFAULT_FONT_COLOR);
        PageSizeUtils.mPageSize = mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT, Constants.DEFAULT_PAGE_SIZE);
        getRuntimePermissions();
        AdsService.getInstance().showAdaptiveBannerAd(inflate.findViewById(R.id.layoutDelete));
        return inflate;
    }

    private void showGoogleInterstitialAds(View view) {
        switch (view.getId()) {
            case R.id.scan_barcode:
                openScanner(IntentIntegrator.ONE_D_CODE_TYPES, R.string.scan_barcode);
                return;
            case R.id.scan_qrcode:
                openScanner(IntentIntegrator.QR_CODE_TYPES, R.string.scan_qrcode);
                return;
            default:
                return;
        }
    }


    @OnClick({R.id.pdfOpen})
    public void openPdf() {
        mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult parseActivityResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (parseActivityResult == null || parseActivityResult.getContents() == null) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.scan_cancelled);
            return;
        }
        mOpenPdf.setVisibility(View.GONE);
        Toast.makeText(mActivity, " " + parseActivityResult.getContents(), Toast.LENGTH_SHORT).show();
        File cacheDir = mActivity.getCacheDir();
        File file = new File(cacheDir.getPath() + Constants.PATH_SEPERATOR + "scan_result_temp.txt");
        try {
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.print("");
            printWriter.append(parseActivityResult.getContents());
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        resultToTextPdf(Uri.fromFile(file));
    }

    @Override
    public void onClick(View view) {
        showGoogleInterstitialAds(view);
    }

    private void openScanner(Collection<String> collection, int i) {
        IntentIntegrator forSupportFragment = IntentIntegrator.forSupportFragment(this);
        forSupportFragment.setDesiredBarcodeFormats(collection);
        forSupportFragment.setPrompt(mActivity.getString(i));
        forSupportFragment.setCameraId(0);
        forSupportFragment.initiateScan();
    }

    private void resultToTextPdf(Uri uri) {
        new MaterialDialog.Builder(mActivity).title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, (materialDialog, charSequence) -> {
                    openPdfDialog(uri, charSequence);
                    materialDialog.dismiss();
                })
                .show();
    }

    public void openPdfDialog(Uri uri, CharSequence sequence) {
        if (StringUtils.getInstance().isEmpty(sequence)) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
            return;
        }
        String name = sequence.toString();
        if (!mFileUtils.isFileExist(name + getString(R.string.pdf_ext))) {
            createPdf(name, uri);
        } else {
            DialogUtils.getInstance().createOverwriteDialog(mActivity)
                    .onPositive((materialDialog, dialogAction) -> {
                        createPdf(name, uri);
                        materialDialog.dismiss();
                    }).onNegative((materialDialog, dialogAction) -> {
                resultToTextPdf(uri);
                materialDialog.dismiss();
            }).show();
        }
    }


    private void createPdf(String name, Uri uri) {
        mPath = mSharedPreferences.getString(Constants.STORAGE_LOCATION, StringUtils.getInstance().getDefaultStorageLocation());
        StringBuilder sb = new StringBuilder();
        sb.append(mPath);
        sb.append(name);
        sb.append(mActivity.getString(R.string.pdf_ext));
        mPath = sb.toString();
        try {
            String name1 = name;
            Uri uri2 = uri;
            new TextToPDFUtils(mActivity).createPdfFromTextFile(new TextToPDFOptions(name1, PageSizeUtils.mPageSize, false, "", uri2, mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, 11), mFontFamily, mFontColor, -1), Constants.textExtension);
            StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                    .setAction(R.string.snackbar_viewAction, view -> {
                        mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF);
                    }).show();
            mOpenPdf.setVisibility(View.VISIBLE);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mFileUtils = new FileUtils(mActivity);
    }

    private void resetValues() {
        ImageToPDFOptions imageToPDFOptions = new ImageToPDFOptions();
        imageToPDFOptions.setBorderWidth(mSharedPreferences.getInt(Constants.DEFAULT_IMAGE_BORDER_TEXT, 0));
        imageToPDFOptions.setQualityString(Integer.toString(mSharedPreferences.getInt(Constants.DEFAULT_COMPRESSION, 30)));
        imageToPDFOptions.setPageSize(mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT, Constants.DEFAULT_PAGE_SIZE));
        imageToPDFOptions.setPasswordProtected(false);
    }

    @Override
    public void onPDFCreationStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void onPDFCreated(boolean isCreated, String path) {
        mMaterialDialog.dismiss();
        if (!isCreated) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_folder_not_created);
            return;
        }
        new DatabaseHelper(mActivity).insertRecord(path, mActivity.getString(R.string.created));
        StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated).setAction(R.string.snackbar_viewAction, view -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF)).show();
        mPath = path;
        resetValues();
        mOpenPdf.setVisibility(View.VISIBLE);
    }

    private void getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(this, Constants.READ_WRITE_PERMISSIONS, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
    }
}

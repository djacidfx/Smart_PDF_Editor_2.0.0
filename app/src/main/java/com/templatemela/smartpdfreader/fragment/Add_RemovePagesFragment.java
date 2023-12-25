package com.templatemela.smartpdfreader.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.dd.morphingbutton.MorphingButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.activity.TM_RearrangePdfPages;
import com.templatemela.smartpdfreader.adapter.MergeFilesAdapter;
import com.templatemela.smartpdfreader.database.DatabaseHelper;
import com.templatemela.smartpdfreader.interfaces.BottomSheetPopulate;
import com.templatemela.smartpdfreader.interfaces.DataSetChanged;
import com.templatemela.smartpdfreader.interfaces.OnBackPressedInterface;
import com.templatemela.smartpdfreader.interfaces.OnPDFAddRemovePasswordInterface;
import com.templatemela.smartpdfreader.interfaces.OnPDFCompressedInterface;
import com.templatemela.smartpdfreader.interfaces.OnPdfReorderedInterface;
import com.templatemela.smartpdfreader.util.BottomSheetCallback;
import com.templatemela.smartpdfreader.util.BottomSheetUtils;
import com.templatemela.smartpdfreader.util.CommonCodeUtils;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.DialogUtils;
import com.templatemela.smartpdfreader.util.DirectoryUtils;
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.MorphButtonUtility;
import com.templatemela.smartpdfreader.util.PDFEncryptionUtility;
import com.templatemela.smartpdfreader.util.PDFUtils;
import com.templatemela.smartpdfreader.util.PopulateListUtil;
import com.templatemela.smartpdfreader.util.RealPathUtil;
import com.templatemela.smartpdfreader.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Add_RemovePagesFragment extends Fragment implements MergeFilesAdapter.OnClickListener, OnPDFCompressedInterface, BottomSheetPopulate, DataSetChanged, OnBackPressedInterface, OnPdfReorderedInterface, OnPDFAddRemovePasswordInterface {
    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;
    private static final int INTENT_REQUEST_REARRANGE_PDF = 11;
    @BindView(R.id.pdfCreate)
    MorphingButton createPdf;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    private Activity mActivity;
    private BottomSheetUtils mBottomSheetUtils;
    @BindView(R.id.compressionInfoText)
    TextView mCompressionInfoText;
    @BindView(R.id.downArrow)
    ImageView mDownArrow;
    private FileUtils mFileUtils;
    @BindView(R.id.infoText)
    TextView mInfoText;
    @BindView(R.id.layout)
    RelativeLayout mLayout;
    @BindView(R.id.lottie_progress)
    LottieAnimationView mLottieProgress;
    private MaterialDialog mMaterialDialog;
    private MorphButtonUtility mMorphButtonUtility;
    @BindView(R.id.pdfOpen)
    MorphingButton mOpenPdf;
    private String mOperation;
    private PDFUtils mPDFUtils;
    private String mPath;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    private BottomSheetBehavior mSheetBehavior;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;
    private Uri mUri;
    @BindView(R.id.view_pdf)
    CardView mViewPdf;
    String outputPath = "";
    @BindView(R.id.pages)
    EditText pagesInput;
    @BindView(R.id.selectFile)
    MorphingButton selectFileButton;

    @Override
    public void pdfStarted() {
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_remove_pages, viewGroup, false);
        ButterKnife.bind(this, inflate);

        mSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mUpArrow, isAdded()));
        mOperation = getArguments().getString(Constants.BUNDLE_DATA);
        mLottieProgress.setVisibility(View.VISIBLE);
        mBottomSheetUtils.populateBottomSheetWithPDFs(this);
        resetValues();
        return inflate;
    }


    @OnClick({R.id.pdfOpen})
    public void openPdf() {
        mFileUtils.openFile(outputPath, FileUtils.FileType.e_PDF);
    }


    @OnClick({R.id.viewFiles})
    public void onViewFilesClick(View view) {
        mBottomSheetUtils.showHideSheet(mSheetBehavior);
    }

    @OnClick({R.id.selectFile})
    public void showFileChooser() {
       // startActivityForResult(mFileUtils.getFileChooser(), INTENT_REQUEST_PICKFILE_CODE);

        mBottomSheetUtils.showHideSheet(mSheetBehavior);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) throws NullPointerException {
        if (intent != null && resultCode == -1) {
            if (requestCode == INTENT_REQUEST_PICKFILE_CODE) {
                mOpenPdf.setVisibility(View.GONE);
                mUri = intent.getData();
                setTextAndActivateButtons(RealPathUtil.getInstance().getRealPath(getContext(), intent.getData()));
            } else if (requestCode == INTENT_REQUEST_REARRANGE_PDF) {
                String result = intent.getStringExtra(Constants.RESULT);
                boolean isDuplicate = intent.getBooleanExtra(Constants.SAME_FILE, false);
                if (mPath != null) {
                    String path = setPath(result);
                    if (mPDFUtils.isPDFEncrypted(mPath)) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.encrypted_pdf);
                        return;
                    }
                    if (isDuplicate) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.file_order);
                    } else if (mPDFUtils.reorderRemovePDF(mPath, path, result)) {
                        viewPdfButton(path);
                    }
                    resetValues();
                }
            }
        }
    }

    private void showGoogleInterstitialAds() {
        if (mOperation.equals("Compress PDF")) {
            compressPDF();
            return;
        }
        PDFEncryptionUtility pDFEncryptionUtility = new PDFEncryptionUtility(mActivity);
        if (mOperation.equals("Add password")) {
            if (!mPDFUtils.isPDFEncrypted(mPath)) {
                pDFEncryptionUtility.setPasswordModule(mPath, null, this);
            } else {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.encrypted_pdf);
            }
            try {
                File file = new File(mPath);
                if (file.isFile()) {
                    String name = file.getName();
                    if (name.contains("_encrypted")) {
                        getContext().deleteFile(name);
                    } else {
                        Log.e("File", "File Contain no match: " + name);
                    }
                    Log.e("Delete", file.getAbsolutePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!mOperation.equals("Remove password")) {
            mPDFUtils.reorderPdfPages(mUri, mPath, this);
        } else if (mOperation.equals("Remove password")) {
            String uniqueFileName = mFileUtils.getUniqueFileName(mPath.replace(mActivity.getResources().getString(R.string.pdf_ext), mActivity.getString(R.string.decrypted_file)));
            outputPath = uniqueFileName;
            pDFEncryptionUtility.removePassword(mPath, uniqueFileName, this);
        } else {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.not_encrypted);
        }
    }

    private String setPath(String path) {
        if (path.length() > 50) {
            String mPath1 = mPath;
            String ext = mActivity.getString(R.string.pdf_ext);
            return mPath1.replace(ext, "_edited" + mActivity.getString(R.string.pdf_ext));
        }
        String mPath2 = mPath;
        String ext2 = mActivity.getString(R.string.pdf_ext);
        return mPath2.replace(ext2, "_edited" + path + mActivity.getString(R.string.pdf_ext));
    }

    @OnClick({R.id.pdfCreate})
    public void parse() {
        StringUtils.getInstance().hideKeyboard(mActivity);
        showGoogleInterstitialAds();
    }

    private void compressPDF() {
        try {
            int parseInt = Integer.parseInt(pagesInput.getText().toString());
            if (parseInt <= 100 && parseInt > 0) {
                String path = mPath;
                if (path != null) {
                    String string = mActivity.getString(R.string.pdf_ext);
                    mPDFUtils.compressPDF(mPath, path.replace(string, "_edited" + parseInt + mActivity.getString(R.string.pdf_ext)), 100 - parseInt, this);
                    return;
                }
            }
            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
        } catch (NumberFormatException exception) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
        }
    }

    private void resetValues() {
        mPath = null;
        pagesInput.setText(null);
        mMorphButtonUtility.initializeButton(selectFileButton, createPdf);
        mOperation.hashCode();
        char c = 65535;
        switch (mOperation.hashCode()) {
            case -1676366185:
                if (mOperation.equals("Remove password")) {
                    c = 0;
                    break;
                }
                break;
            case 1530436442:
                if (mOperation.equals("Add password")) {
                    c = 1;
                    break;
                }
                break;
            case 1603960628:
                if (mOperation.equals("Compress PDF")) {
                    c = 2;
                    break;
                }
                break;
            case 2029497407:
                if (mOperation.equals(Constants.REORDER_PAGES)) {
                    c = 3;
                    break;
                }
                break;
            case 2141576520:
                if (mOperation.equals(Constants.REMOVE_PAGES)) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 3:
            case 4:
                mInfoText.setVisibility(View.GONE);
                pagesInput.setVisibility(View.GONE);
                return;
            case 2:
                mInfoText.setText(R.string.compress_pdf_prompt);
                return;
            default:
                return;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mPDFUtils = new PDFUtils(mActivity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
    }

    @Override
    public void onItemClick(String path) {
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setTextAndActivateButtons(path);
    }

    private void setTextAndActivateButtons(String path) {
        if (path == null) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.file_access_error);
            resetValues();
            return;
        }
        mPath = path;
        mCompressionInfoText.setVisibility(View.GONE);
        mViewPdf.setVisibility(View.GONE);
        mMorphButtonUtility.setTextAndActivateButtons(path, selectFileButton, createPdf);
    }

    @Override
    public void pdfCompressionStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void pdfCompressionEnded(String path, Boolean isSuccess) {
        mMaterialDialog.dismiss();
        if (!isSuccess.booleanValue() || path == null || mPath == null) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.encrypted_pdf);
        } else {
            StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                    .setAction(R.string.snackbar_viewAction, view -> mFileUtils.openFile(path, FileUtils.FileType.e_PDF)).show();
            new DatabaseHelper(mActivity).insertRecord(path, mActivity.getString(R.string.created));
            File file = new File(mPath);
            File file2 = new File(path);
            viewPdfButton(path);
            mCompressionInfoText.setVisibility(View.VISIBLE);
            mCompressionInfoText.setText(String.format(mActivity.getString(R.string.compress_info), FileUtils.getFormattedSize(file), FileUtils.getFormattedSize(file2)));
        }
        resetValues();
    }


    private void viewPdfButton(String path) {
        mViewPdf.setVisibility(View.VISIBLE);
        mViewPdf.setOnClickListener(view -> mFileUtils.openFile(path, FileUtils.FileType.e_PDF));
    }


    public void onPopulate(ArrayList<String> arrayList) {

        if (mOperation.equals("Remove password")){
            PopulateListUtil.populateUtil(mActivity,this,new DirectoryUtils(mActivity),mLayout,mLottieProgress,mRecyclerViewFiles);
        }
        else{
            CommonCodeUtils.getInstance().populateUtil(mActivity, arrayList, this, mLayout, mLottieProgress, mRecyclerViewFiles);
        }

    }

    public void closeBottomSheet() {
        CommonCodeUtils.getInstance().closeBottomSheetUtil(mSheetBehavior);
    }

    public boolean checkSheetBehaviour() {
        return CommonCodeUtils.getInstance().checkSheetBehaviourUtil(mSheetBehavior);
    }

    @Override
    public void onPdfReorderStarted() {
        DialogUtils instance = DialogUtils.getInstance();
        mMaterialDialog = instance.createCustomAnimationDialog(mActivity, mActivity.getString(R.string.reordering_pages_dialog));
        mMaterialDialog.show();
    }

    @Override
    public void onPdfReorderCompleted(List<Bitmap> list) {
        mMaterialDialog.dismiss();
        TM_RearrangePdfPages.mImages = new ArrayList<>(list);
        list.clear();
        startActivityForResult(TM_RearrangePdfPages.jumpActivity(mActivity), INTENT_REQUEST_REARRANGE_PDF);
    }

    @Override
    public void onPdfReorderFailed() {
        mMaterialDialog.dismiss();
        StringUtils.getInstance().showSnackbar(mActivity, R.string.file_access_error);
    }
    @Override
    public void pdfEnded(String name, Boolean isSuccess) {
        if (isSuccess) {
            if (name.contains("_nanda_")) {
                outputPath = name.split("_nanda_")[1];
            }
            mOpenPdf.setVisibility(View.VISIBLE);
            return;
        }
        mOpenPdf.setVisibility(View.GONE);
    }

    @Override
    public void updateDataset() {

    }
}

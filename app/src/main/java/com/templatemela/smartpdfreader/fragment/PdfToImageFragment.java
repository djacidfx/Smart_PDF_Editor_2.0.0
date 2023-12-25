package com.templatemela.smartpdfreader.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.dd.morphingbutton.MorphingButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.activity.TM_ImagesPreviewActivity;
import com.templatemela.smartpdfreader.adapter.ExtractImagesAdapter;
import com.templatemela.smartpdfreader.adapter.MergeFilesAdapter;
import com.templatemela.smartpdfreader.interfaces.BottomSheetPopulate;
import com.templatemela.smartpdfreader.interfaces.ExtractImagesListener;
import com.templatemela.smartpdfreader.interfaces.OnBackPressedInterface;
import com.templatemela.smartpdfreader.util.BottomSheetCallback;
import com.templatemela.smartpdfreader.util.BottomSheetUtils;
import com.templatemela.smartpdfreader.util.CommonCodeUtils;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.DialogUtils;
import com.templatemela.smartpdfreader.util.ExtractImages;
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.MorphButtonUtility;
import com.templatemela.smartpdfreader.util.PDFUtils;
import com.templatemela.smartpdfreader.util.PdfToImages;
import com.templatemela.smartpdfreader.util.RealPathUtil;
import com.templatemela.smartpdfreader.util.StringUtils;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PdfToImageFragment extends Fragment implements BottomSheetPopulate, MergeFilesAdapter.OnClickListener, ExtractImagesListener, ExtractImagesAdapter.OnFileItemClickedListener, OnBackPressedInterface {
    private static final int INTENT_REQUEST_PICK_FILE_CODE = 10;
    String SCAN_PATH;
    File[] allFiles;
    private Activity mActivity;
    private BottomSheetUtils mBottomSheetUtils;
    private Context mContext;
    @BindView(R.id.createImages)
    MorphingButton mCreateImagesButton;
    @BindView(R.id.pdfToImagesText)
    TextView mCreateImagesSuccessText;
    @BindView(R.id.created_images)
    RecyclerView mCreatedImages;
    private FileUtils mFileUtils;
    private String[] mInputPassword;
    @BindView(R.id.layout)
    RelativeLayout mLayout;
    @BindView(R.id.bottom_sheet)
    LinearLayout mLayoutBottomSheet;
    @BindView(R.id.lottie_progress)
    LottieAnimationView mLottieProgress;
    private MaterialDialog mMaterialDialog;
    private MorphButtonUtility mMorphButtonUtility;
    private String mOperation;
    private ArrayList<String> mOutputFilePaths;
    private PDFUtils mPDFUtils;
    private String mPath;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    @BindView(R.id.selectFile)
    MorphingButton mSelectFileButton;
    private BottomSheetBehavior mSheetBehavior;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;
    private Uri mUri;
    @BindView(R.id.options)
    LinearLayout options;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_pdf_to_image, viewGroup, false);
        ButterKnife.bind(this, inflate);

        mOperation = getArguments().getString(Constants.BUNDLE_DATA);
        mSheetBehavior = BottomSheetBehavior.from(mLayoutBottomSheet);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mUpArrow, isAdded()));
        mLottieProgress.setVisibility(View.VISIBLE);
        mBottomSheetUtils.populateBottomSheetWithPDFs(this);
        resetView();
        return inflate;
    }


    @OnClick({R.id.viewImagesInGallery})
    public void onImagesInGalleryClick() {
        File[] listFiles = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + Constants.pdfDirectory).listFiles();
        allFiles = listFiles;
        new SingleMediaScanner(mActivity, listFiles[0]);
    }

    public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
        private File mFile;
        private MediaScannerConnection mMs;

        public SingleMediaScanner(Context context, File file) {
            mFile = file;
            mMs = new MediaScannerConnection(context, this);
            mMs.connect();
        }

        @Override
        public void onMediaScannerConnected() {
            mMs.scanFile(mFile.getAbsolutePath(), null);
        }

        @Override
        public void onScanCompleted(String str, Uri uri) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(uri);
            startActivity(intent);
            mMs.disconnect();
        }
    }


    @OnClick({R.id.shareImages})
    public void onShareFilesClick() {
        if (mOutputFilePaths != null) {
            ArrayList arrayList = new ArrayList();
            for (String mOutputFilePath : mOutputFilePaths) {
                arrayList.add(new File(mOutputFilePath));
            }
            mFileUtils.shareMultipleFiles(arrayList);
        }
    }


    @OnClick({R.id.viewFiles})
    public void onViewFilesClick() {
        mBottomSheetUtils.showHideSheet(mSheetBehavior);
    }


    @OnClick({R.id.viewImages})
    public void onViewImagesClicked() {
        mActivity.startActivity(TM_ImagesPreviewActivity.jumpActivity(mActivity, mOutputFilePaths));
    }

    @OnClick({R.id.selectFile})
    public void showFileChooser() {
        mBottomSheetUtils.showHideSheet(mSheetBehavior);
       // startActivityForResult(mFileUtils.getFileChooser(), INTENT_REQUEST_PICK_FILE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) throws NullPointerException {
        if (intent != null && resultCode == -1 && intent.getData() != null && requestCode == INTENT_REQUEST_PICK_FILE_CODE) {
            mUri = intent.getData();
            setTextAndActivateButtons(RealPathUtil.getInstance().getRealPath(getContext(), intent.getData()));
        }
    }

    @OnClick({R.id.createImages})
    public void parse() {
        if (mPDFUtils.isPDFEncrypted(mPath)) {
            mInputPassword = new String[1];
            new MaterialDialog.Builder(mActivity).title(R.string.enter_password)
                    .content(R.string.decrypt_protected_file).inputType(128).input(null, null, (materialDialog, input) -> {
                if (StringUtils.getInstance().isEmpty(input)) {
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                    return;
                }
                String name = input.toString();
                String[] passArr = mInputPassword;
                passArr[0] = name;
                pdfToImage(passArr);
            }).show();
            return;
        }
        pdfToImage(mInputPassword);
    }

    private void pdfToImage(String[] passArr) {
        if (mOperation.equals(Constants.PDF_TO_IMAGES)) {
             PdfToImages.pdfToImageConverter(mContext, passArr, mPath, mUri, this);
            return;
        }
         ExtractImages.extractImagesFromPdf(mPath, this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
        mContext = context;
        mPDFUtils = new PDFUtils(mActivity);
    }

    @Override
    public void onItemClick(String path) {
        mUri = null;
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setTextAndActivateButtons(path);
    }

    private void setTextAndActivateButtons(String path) {
        if (path == null) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.error_path_not_found);
            resetView();
            return;
        }
        mCreatedImages.setVisibility(View.GONE);
        options.setVisibility(View.GONE);
        mCreateImagesSuccessText.setVisibility(View.GONE);
        mPath = path;
        mMorphButtonUtility.setTextAndActivateButtons(path, mSelectFileButton, mCreateImagesButton);
    }

    @Override
    public void onFileItemClick(String path) {
        mFileUtils.openImage(path);
    }

    @Override
    public void resetView() {
        mPath = null;
        mMorphButtonUtility.initializeButton(mSelectFileButton, mCreateImagesButton);
    }

    @Override
    public void extractionStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void updateView(int count, ArrayList<String> arrayList) {
        mMaterialDialog.dismiss();
        resetView();
        mOutputFilePaths = arrayList;
        CommonCodeUtils.getInstance().updateView(mActivity, count, arrayList, mCreateImagesSuccessText, options, mCreatedImages, this);
    }

    @Override
    public void onPopulate(ArrayList<String> arrayList) {
        CommonCodeUtils.getInstance().populateUtil(mActivity, arrayList, this, mLayout, mLottieProgress, mRecyclerViewFiles);
    }

    @Override
    public void closeBottomSheet() {
        CommonCodeUtils.getInstance().closeBottomSheetUtil(mSheetBehavior);
    }

    @Override
    public boolean checkSheetBehaviour() {
        return CommonCodeUtils.getInstance().checkSheetBehaviourUtil(mSheetBehavior);
    }
}

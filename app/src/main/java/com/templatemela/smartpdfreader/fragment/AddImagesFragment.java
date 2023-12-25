package com.templatemela.smartpdfreader.fragment;

import static com.vincent.filepicker.Constant.REQUEST_CODE_PICK_FILE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.dd.morphingbutton.MorphingButton;
import com.esafirm.imagepicker.features.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.adapter.MergeFilesAdapter;
import com.templatemela.smartpdfreader.interfaces.BottomSheetPopulate;
import com.templatemela.smartpdfreader.interfaces.OnBackPressedInterface;
import com.templatemela.smartpdfreader.util.BottomSheetCallback;
import com.templatemela.smartpdfreader.util.BottomSheetUtils;
import com.templatemela.smartpdfreader.util.CommonCodeUtils;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.DialogUtils;
import com.templatemela.smartpdfreader.util.FileUriUtils;
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.MorphButtonUtility;
import com.templatemela.smartpdfreader.util.PDFUtils;
import com.templatemela.smartpdfreader.util.PermissionsUtils;
import com.templatemela.smartpdfreader.util.StringUtils;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddImagesFragment extends Fragment implements BottomSheetPopulate, MergeFilesAdapter.OnClickListener, OnBackPressedInterface {
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int INTENT_REQUEST_PICK_FILE_CODE = 10;
    private static final int REQUEST_PERMISSIONS_CODE = 124;
    private static final ArrayList<String> mImagesUri = new ArrayList<>();
    @BindView(R.id.addImages)
    MorphingButton addImages;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    private Activity mActivity;
    private BottomSheetUtils mBottomSheetUtils;
    @BindView(R.id.pdfCreate)
    MorphingButton mCreatePdf;
    private FileUtils mFileUtils;
    @BindView(R.id.layout)
    RelativeLayout mLayout;
    @BindView(R.id.lottie_progress)
    LottieAnimationView mLottieProgress;
    private MorphButtonUtility mMorphButtonUtility;
    @BindView(R.id.tvNoOfImages)
    TextView mNoOfImages;
    @BindView(R.id.pdfOpen)
    MorphingButton mOpenPdf;
    private String mOperation;
    private PDFUtils mPDFUtils;
    private String mPath;
    private boolean mPermissionGranted = false;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    private BottomSheetBehavior mSheetBehavior;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;
    String outputPath = "";
    @BindView(R.id.selectFile)
    MorphingButton selectFileButton;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_add_images, viewGroup, false);
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
        mBottomSheetUtils.showHideSheet(mSheetBehavior);
       // startActivityForResult(mFileUtils.getFileChooser(), INTENT_REQUEST_PICK_FILE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == -1 && intent != null) {
            if (requestCode == INTENT_REQUEST_PICK_FILE_CODE) {
                mOpenPdf.setVisibility(View.GONE);
                setTextAndActivateButtons(FileUriUtils.getInstance().getFilePath(intent.getData()));
            } else if (requestCode == INTENT_REQUEST_GET_IMAGES) {
                mImagesUri.clear();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    ClipData clipData = intent.getClipData();
                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri selectedImageUri = clipData.getItemAt(i).getUri();
                            mImagesUri.add(String.valueOf(selectedImageUri));
                        }
                    }
                    if (mImagesUri.size() > 0) {
                        mNoOfImages.setText(String.format(this.mActivity.getResources().getString(R.string.images_selected), mImagesUri.size()));
                        mNoOfImages.setVisibility(View.VISIBLE);
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_images_added);
                        mCreatePdf.setEnabled(true);
                    } else {
                        mNoOfImages.setVisibility(View.GONE);
                    }
                    mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());

                }else{
                    for (int i4 = 0; i4 < ImagePicker.getImages(intent).size(); i4++) {
                        mImagesUri.add(ImagePicker.getImages(intent).get(i4).getPath());
                    }
                    if (mImagesUri.size() > 0) {
                        mNoOfImages.setText(String.format(this.mActivity.getResources().getString(R.string.images_selected), mImagesUri.size()));
                        mNoOfImages.setVisibility(View.VISIBLE);
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_images_added);
                        mCreatePdf.setEnabled(true);
                    } else {
                        mNoOfImages.setVisibility(View.GONE);
                    }
                    mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());
                }
            }
        }
    }

    @OnClick({R.id.pdfCreate})
    public void parse() {
        StringUtils.getInstance().hideKeyboard(mActivity);
        if (mOperation.equals(Constants.ADD_IMAGES)) {
            getFileName();
        }
    }

    private void getFileName() {
        DialogUtils.getInstance().createCustomDialog(mActivity, R.string.creating_pdf, R.string.enter_file_name)
                .input(getString(R.string.example), null, (materialDialog, input) -> saveFile(input)).show();
    }

    public void saveFile(CharSequence input) {
        if (StringUtils.getInstance().isEmpty(input)) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
            return;
        }
        String name = input.toString();
        FileUtils fileUtils = new FileUtils(mActivity);
        if (!fileUtils.isFileExist(name + getString(R.string.pdf_ext))) {
            addImagesToPdf(name);
        } else {
            DialogUtils.getInstance().createOverwriteDialog(mActivity)
                    .onPositive((materialDialog12, dialogAction) -> addImagesToPdf(name))
                    .onNegative((materialDialog1, dialogAction) -> getFileName()).show();
        }
    }

    private void addImagesToPdf(String name) {
        int lastIndexOf = mPath.lastIndexOf(Constants.PATH_SEPERATOR);
        String mPath1 = mPath;
        String fPath = mPath1.substring(lastIndexOf + 1);
        outputPath = mPath1.replace(fPath, name + this.mActivity.getString(R.string.pdf_ext));
        if (mImagesUri.size() > 0) {
            MaterialDialog createAnimationDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
            createAnimationDialog.show();
            try {
                mPDFUtils.addImagesToPdf(mPath, outputPath, mImagesUri);
                mOpenPdf.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                mOpenPdf.setVisibility(View.GONE);
            }
            mMorphButtonUtility.morphToSuccess(mCreatePdf);
            resetValues();
            createAnimationDialog.dismiss();
            return;
        }
        StringUtils.getInstance().showSnackbar(mActivity,R.string.no_images_selected);
    }

    private void resetValues() {
        mPath = null;
        mImagesUri.clear();
        mMorphButtonUtility.initializeButton(selectFileButton, mCreatePdf);
        mMorphButtonUtility.initializeButton(selectFileButton, addImages);
        mNoOfImages.setVisibility(View.GONE);
    }


    @OnClick({R.id.addImages})
    public void startAddingImages() {
       getRuntimePermissions();
    }
    private void getRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            Dexter.withActivity(mActivity)
                    .withPermissions(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.CAMERA)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                Toast.makeText(mActivity, "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                                selectImages();
                            }
                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).withErrorListener(error -> {
                        Toast.makeText(mActivity, "Error occurred! ", Toast.LENGTH_SHORT).show();
                    })
                    .onSameThread().check();
        }else {
            Dexter.withActivity(mActivity)
                    .withPermissions(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                Toast.makeText(mActivity, "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                                selectImages();
                            }
                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).withErrorListener(error -> {
                        Toast.makeText(mActivity, "Error occurred! ", Toast.LENGTH_SHORT).show();
                    })
                    .onSameThread().check();
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, REQUEST_PERMISSIONS_CODE);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });
        builder.show();
    }

    private void selectImages() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), INTENT_REQUEST_GET_IMAGES);
        }else {
            ImagePicker.create(this).folderMode(true).includeAnimation(true).multi().limit(1).showCamera(true).toolbarFolderTitle("Folder").toolbarImageTitle("Tap to select").toolbarDoneButtonText("DONE").start(INTENT_REQUEST_GET_IMAGES);
        }
//        Matisse.from(this).choose(MimeType.ofImage(), false).countable(true).capture(true).captureStrategy(new CaptureStrategy(true, Constants.AUTHORITY_APP)).maxSelectable(1).imageEngine(new PicassoEngine()).forResult(INTENT_REQUEST_GET_IMAGES);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mPDFUtils = new PDFUtils(mActivity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
    }

    @Override
    public void onItemClick(String path) {
        this.mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setTextAndActivateButtons(path);
    }

    private void setTextAndActivateButtons(String path) {
        mPath = path;
        mMorphButtonUtility.setTextAndActivateButtons(path, selectFileButton, addImages);
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

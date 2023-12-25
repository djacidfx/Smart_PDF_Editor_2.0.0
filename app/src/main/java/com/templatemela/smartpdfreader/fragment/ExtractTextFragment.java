package com.templatemela.smartpdfreader.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
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
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.dd.morphingbutton.MorphingButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
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
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.MorphButtonUtility;
import com.templatemela.smartpdfreader.util.PermissionsUtils;
import com.templatemela.smartpdfreader.util.RealPathUtil;
import com.templatemela.smartpdfreader.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.models.sort.SortingTypes;

public class ExtractTextFragment extends Fragment implements MergeFilesAdapter.OnClickListener, BottomSheetPopulate, OnBackPressedInterface {
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    @BindView(R.id.extract_text)
    MorphingButton extractText;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    private Activity mActivity;
    private BottomSheetUtils mBottomSheetUtils;
    private boolean mButtonClicked = false;
    private Uri mExcelFileUri;
    private String mFileName;
    private final int mFileSelectCode = 0;
    private FileUtils mFileUtils;
    @BindView(R.id.layout)
    RelativeLayout mLayout;
    @BindView(R.id.lottie_progress)
    LottieAnimationView mLottieProgress;
    private MorphButtonUtility mMorphButtonUtility;
    private boolean mPermissionGranted = false;
    private String mRealPath;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    private SharedPreferences mSharedPreferences;
    private BottomSheetBehavior mSheetBehavior;
    @BindView(R.id.tv_extract_text_bottom)
    TextView mTextView;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_extract_text, viewGroup, false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
//        mPermissionGranted = PermissionsUtils.getInstance().checkRuntimePermissions(this, Constants.READ_WRITE_PERMISSIONS);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        ButterKnife.bind(this, inflate);
        mSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mMorphButtonUtility.morphToGrey(extractText, mMorphButtonUtility.integer());
        extractText.setEnabled(false);
        mBottomSheetUtils.populateBottomSheetWithPDFs(this);
        mLottieProgress.setVisibility(View.VISIBLE);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mUpArrow, isAdded()));
        return inflate;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mFileUtils = new FileUtils(mActivity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
    }


    @OnClick({R.id.viewFiles})
    public void onViewFilesClick(View view) {
        mBottomSheetUtils.showHideSheet(mSheetBehavior);
    }

    @OnClick({R.id.select_pdf_file})
    public void selectPdfFile() {
        if (!mButtonClicked) {
            Uri parse = Uri.parse(Environment.getRootDirectory() + Constants.PATH_SEPERATOR);
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setDataAndType(parse, "*/*");
            intent.addCategory("android.intent.category.OPENABLE");
            try {
                startActivityForResult(Intent.createChooser(intent, String.valueOf(R.string.select_file)), 0);
                mButtonClicked = true;
            } catch (ActivityNotFoundException exception) {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.install_file_manager);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mButtonClicked = false;
        if (requestCode == 0 && resultCode == -1) {
            mExcelFileUri = intent.getData();
            mRealPath = RealPathUtil.getInstance().getRealPath(getContext(), intent.getData());
            StringUtils.getInstance().showSnackbar(mActivity, getResources().getString(R.string.snackbar_pdfselected));
            mFileName = mFileUtils.getFileName(mExcelFileUri);
            if (mFileName == null || mFileName.endsWith(Constants.pdfExtension)) {
                String fName = mActivity.getResources().getString(R.string.pdf_selected) + mFileName;
                mFileName = fName;
                mTextView.setText(fName);
                mTextView.setVisibility(View.VISIBLE);
                extractText.setEnabled(true);
                mMorphButtonUtility.morphToSquare(extractText, mMorphButtonUtility.integer());
            } else {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.extension_not_supported);
                return;
            }
        }
    }

    @OnClick({R.id.extract_text})
    public void openExtractText() {
        getRuntimePermissions();
    }
    private void getRuntimePermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            Dexter.withActivity(mActivity)
                    .withPermissions(
                            Manifest.permission.READ_MEDIA_IMAGES)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                Toast.makeText(mActivity, "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                                new MaterialDialog.Builder(mActivity).title(R.string.creating_txt).content(R.string.enter_file_name).input(getString(R.string.example), null, this::createTextFile).show();
                            }
                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog();
                            }
                        }
                        public void createTextFile(MaterialDialog materialDialog, CharSequence input) {
                            if (StringUtils.getInstance().isEmpty(input)) {
                                StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                                return;
                            }
                            String name = input.toString();
                            if (!mFileUtils.isFileExist(name + Constants.textExtension)) {
                                extractTextFromPdf(name);
                            } else {
                                DialogUtils.getInstance().createOverwriteDialog(mActivity)
                                        .onPositive((materialDialog1, dialogAction) -> extractTextFromPdf(name)).onNegative((materialDialog12, dialogAction) -> openExtractText()).show();
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
        else {
            Dexter.withActivity(mActivity)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                Toast.makeText(mActivity, "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                                new MaterialDialog.Builder(mActivity).title(R.string.creating_txt).content(R.string.enter_file_name).input(getString(R.string.example), null, this::createTextFile).show();
                            }
                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog();
                            }
                        }
                        public void createTextFile(MaterialDialog materialDialog, CharSequence input) {
                            if (StringUtils.getInstance().isEmpty(input)) {
                                StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                                return;
                            }
                            String name = input.toString();
                            if (!mFileUtils.isFileExist(name + Constants.textExtension)) {
                                extractTextFromPdf(name);
                            } else {
                                DialogUtils.getInstance().createOverwriteDialog(mActivity)
                                        .onPositive((materialDialog1, dialogAction) -> extractTextFromPdf(name)).onNegative((materialDialog12, dialogAction) -> openExtractText()).show();
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
            startActivityForResult(intent, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });
        builder.show();
    }



    private void extractTextFromPdf(String name) {
        String locName = mSharedPreferences.getString(Constants.STORAGE_LOCATION, StringUtils.getInstance().getDefaultStorageLocation());
        String finalName = locName + name + Constants.textExtension;
        try {
            StringBuilder sb = new StringBuilder();
            PdfReader pdfReader = new PdfReader(mRealPath);
            int numberOfPages = pdfReader.getNumberOfPages();
            int i = 0;
            while (i < numberOfPages) {
                i++;
                sb.append(PdfTextExtractor.getTextFromPage(pdfReader, i).trim());
                sb.append("\n");
            }
            pdfReader.close();
            if (TextUtils.isEmpty(sb.toString().trim())) {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.snack_bar_empty_txt_in_pdf);
                mMorphButtonUtility.morphToGrey(extractText, mMorphButtonUtility.integer());
                extractText.setEnabled(false);
                mRealPath = null;
                mExcelFileUri = null;
                return;
            }
            FileWriter fileWriter = new FileWriter(new File(locName, name + Constants.textExtension));
            fileWriter.append(sb.toString());
            fileWriter.flush();
            fileWriter.close();
            StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_txtExtracted).
                    setAction(R.string.snackbar_viewAction, view -> mFileUtils.openFile(finalName, FileUtils.FileType.e_TXT)).show();
            mTextView.setVisibility(View.GONE);
            mButtonClicked = false;
            mMorphButtonUtility.morphToGrey(extractText, mMorphButtonUtility.integer());
            extractText.setEnabled(false);
            mRealPath = null;
            mExcelFileUri = null;
        } catch (Exception e) {
            e.printStackTrace();
            StringUtils.getInstance().showSnackbar(mActivity, R.string.remove_pages_error);
        } catch (Throwable th) {
            mMorphButtonUtility.morphToGrey(extractText, mMorphButtonUtility.integer());
            extractText.setEnabled(false);
            mRealPath = null;
            mExcelFileUri = null;
            throw th;
        }
    }

    @Override
    public void onPopulate(ArrayList<String> arrayList) {
        CommonCodeUtils.getInstance().populateUtil(mActivity, arrayList, this, mLayout, mLottieProgress, mRecyclerViewFiles);
    }

    @Override
    public void onItemClick(String path) {
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mRealPath = path;
        mFileName = FileUtils.getFileName(path);
        mFileName = getResources().getString(R.string.pdf_selected) + mFileName;
        mTextView.setText(mFileName);
        mTextView.setVisibility(View.VISIBLE);
        extractText.setEnabled(true);
        mMorphButtonUtility.morphToSquare(extractText, mMorphButtonUtility.integer());
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

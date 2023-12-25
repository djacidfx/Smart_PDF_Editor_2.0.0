package com.templatemela.smartpdfreader.fragment;

import static android.app.Activity.RESULT_OK;

import static com.vincent.filepicker.Constant.REQUEST_CODE_PICK_FILE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.airbnb.lottie.LottieAnimationView;
import com.dd.morphingbutton.MorphingButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.activity.MainActivity;
import com.templatemela.smartpdfreader.adapter.EnhancementOptionsAdapter;
import com.templatemela.smartpdfreader.adapter.MergeFilesAdapter;
import com.templatemela.smartpdfreader.ads.AdsService;
import com.templatemela.smartpdfreader.database.DatabaseHelper;
import com.templatemela.smartpdfreader.interfaces.BottomSheetPopulate;
import com.templatemela.smartpdfreader.interfaces.OnItemClickListener;
import com.templatemela.smartpdfreader.interfaces.OnPDFCreatedInterface;
import com.templatemela.smartpdfreader.model.EnhancementOptionsEntity;
import com.templatemela.smartpdfreader.util.BottomSheetCallback;
import com.templatemela.smartpdfreader.util.BottomSheetUtils;
import com.templatemela.smartpdfreader.util.CommonCodeUtils;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.DialogUtils;
import com.templatemela.smartpdfreader.util.ExcelToPDFAsync;
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.MergePdfEnhancementOptionsUtils;
import com.templatemela.smartpdfreader.util.MorphButtonUtility;
import com.templatemela.smartpdfreader.util.StringUtils;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExceltoPdfFragment extends Fragment implements MergeFilesAdapter.OnClickListener, OnPDFCreatedInterface, OnItemClickListener, BottomSheetPopulate {
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    private Activity mActivity;
    private BottomSheetUtils mBottomSheetUtils;
    private boolean mButtonClicked = false;
    @BindView(R.id.create_excel_to_pdf)
    MorphingButton mCreateExcelPdf;
    private EnhancementOptionsAdapter mEnhancementOptionsAdapter;
    private ArrayList<EnhancementOptionsEntity> mEnhancementOptionsEntityArrayList;
    @BindView(R.id.enhancement_options_recycle_view)
    RecyclerView mEnhancementOptionsRecycleView;
    private Uri mExcelFileUri;
    String excelFilePath;
    private FileUtils mFileUtils;
    @BindView(R.id.layout)
    RelativeLayout mLayout;
    @BindView(R.id.lottie_progress)
    LottieAnimationView mLottieProgress;
    private MaterialDialog mMaterialDialog;
    private MorphButtonUtility mMorphButtonUtility;
    @BindView(R.id.open_pdf)
    MorphingButton mOpenPdf;
    private String mPassword;
    private boolean mPasswordProtected = false;
    private String mPath;
    String mRealPath;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    private SharedPreferences mSharedPreferences;
    private BottomSheetBehavior mSheetBehavior;
    @BindView(R.id.tv_excel_file_name_bottom)
    TextView mTextView;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_excelto_pdf, viewGroup, false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        ButterKnife.bind(this, inflate);
        showEnhancementOptions();
        mMorphButtonUtility.morphToGrey(mCreateExcelPdf, mMorphButtonUtility.integer());
        mCreateExcelPdf.setEnabled(false);
        ButterKnife.bind(this, inflate);
        mSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mUpArrow, isAdded()));
        mLottieProgress.setVisibility(View.VISIBLE);
        mBottomSheetUtils.populateBottomSheetWithExcelFiles(this);
        AdsService.getInstance().showAdaptiveBannerAd(inflate.findViewById(R.id.layoutDelete));
        return inflate;
    }

    private void showEnhancementOptions() {
        mEnhancementOptionsRecycleView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mEnhancementOptionsEntityArrayList = MergePdfEnhancementOptionsUtils.getInstance().getEnhancementOptions(mActivity);
        mEnhancementOptionsAdapter = new EnhancementOptionsAdapter(this, mEnhancementOptionsEntityArrayList);
        mEnhancementOptionsRecycleView.setAdapter(mEnhancementOptionsAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        mActivity = activity;
        mFileUtils = new FileUtils(activity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
    }

    @OnClick({R.id.select_excel_file})
    public void selectExcelFile() {
        getRuntimePermissions();
    }

    @OnClick({R.id.create_excel_to_pdf})
    public void openExcelToPdf() {
        new MaterialDialog.Builder(mActivity).title(R.string.creating_pdf).content(R.string.enter_file_name).input(getString(R.string.example), null, (dialog, input) -> {
            if (StringUtils.getInstance().isEmpty(input)) {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                return;
            }

            if (!mFileUtils.isFileExist(input.toString() + getString(R.string.pdf_ext))) {
                convertToPdf(input.toString());
            } else {
                DialogUtils.getInstance().createOverwriteDialog(mActivity)
                        .onPositive((materialDialog12, dialogAction) -> convertToPdf(input.toString()))
                        .onNegative((materialDialog1, dialogAction) -> openExcelToPdf()).show();
            }
        }).show();
    }

    @OnClick({R.id.open_pdf})
    public void openPdf() {
        mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mButtonClicked = false;
        if(requestCode== REQUEST_CODE_PICK_FILE) {
            if (resultCode == RESULT_OK) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    if (intent != null) {
                        mExcelFileUri = intent.getData();
                        mRealPath = String.valueOf(mExcelFileUri);
                        checkFileUri();
                    }
                }else {
                    ArrayList<NormalFile> list = intent.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    mExcelFileUri= Uri.parse(list.get(0).getPath());
                    mRealPath=list.get(0).getPath();
                    checkFileUri();
                }
            }
        }
    }

    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = mActivity.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        } else {
            return null;
        }
    }

    private void checkFileUri() {
        StringUtils.getInstance().showSnackbar(mActivity, getResources().getString(R.string.excel_selected));
        String fileName = mFileUtils.getFileName(mExcelFileUri);
        if (fileName == null || fileName.endsWith(Constants.excelExtension) || fileName.endsWith(Constants.excelWorkbookExtension)) {
            mTextView.setText(getResources().getString(R.string.excel_selected) + fileName);
            mTextView.setVisibility(View.VISIBLE);
            mCreateExcelPdf.setEnabled(true);
            mCreateExcelPdf.unblockTouch();
            mMorphButtonUtility.morphToSquare(mCreateExcelPdf, mMorphButtonUtility.integer());
            mOpenPdf.setVisibility(View.GONE);
            return;
        }
        StringUtils.getInstance().showSnackbar(mActivity, R.string.extension_not_supported);
    }

    private void convertToPdf(String name) {
            String string = mSharedPreferences.getString(Constants.STORAGE_LOCATION, StringUtils.getInstance().getDefaultStorageLocation());
            mPath = string + name + mActivity.getString(R.string.pdf_ext);
            ExcelToPDFAsync.excelToPdfConverter(mRealPath, mPath, this, mPasswordProtected, mPassword);
        }

    private void getRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            Dexter.withActivity(mActivity)
                    .withPermissions(Manifest.permission.READ_MEDIA_IMAGES)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                Toast.makeText(mActivity, "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                                if (!mButtonClicked) {
                                    String[] mimetypes =
                                            { "application/vnd.ms-excel", // .xls
                                                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" // .xlsx
                                            };
                                    Uri parse = Uri.parse(Environment.getRootDirectory() + Constants.PATH_SEPERATOR);
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setDataAndType(parse, "*/*");
                                    intent.putExtra(Intent.EXTRA_MIME_TYPES,mimetypes);
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    try {
                                        startActivityForResult(Intent.createChooser(intent, String.valueOf(R.string.select_file)), REQUEST_CODE_PICK_FILE);
                                    } catch (ActivityNotFoundException exception) {
                                        StringUtils.getInstance().showSnackbar(mActivity, R.string.install_file_manager);
                                    }

                                }
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
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                Toast.makeText(mActivity, "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                                if (!mButtonClicked) {

                                    Intent intent4 = new Intent(getContext(), NormalFilePickActivity.class);
                                    intent4.putExtra(Constant.MAX_NUMBER, 1);
                                    intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[] {"xlsx", "xls"});
                                    startActivityForResult(intent4, REQUEST_CODE_PICK_FILE);
                                    mButtonClicked = true;
                                }
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
            startActivityForResult(intent, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });
        builder.show();
    }
    @Override
    public void onPDFCreationStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void onPDFCreated(boolean isCreated, String path) {
        if (mMaterialDialog != null && mMaterialDialog.isShowing()) {
            mMaterialDialog.dismiss();
        }
        if (!isCreated) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.error_pdf_not_created);
            mTextView.setVisibility(View.GONE);
            mMorphButtonUtility.morphToGrey(mCreateExcelPdf, mMorphButtonUtility.integer());
            mCreateExcelPdf.setEnabled(false);
            mExcelFileUri = null;
            return;
        }
        StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                .setAction(R.string.snackbar_viewAction, view ->
                        mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF)).show();
        new DatabaseHelper(mActivity).insertRecord(mPath, mActivity.getString(R.string.created));
        mTextView.setVisibility(View.GONE);
        mOpenPdf.setVisibility(View.VISIBLE);
        mMorphButtonUtility.morphToSuccess(mCreateExcelPdf);
        mCreateExcelPdf.blockTouch();
        mMorphButtonUtility.morphToGrey(mCreateExcelPdf, mMorphButtonUtility.integer());
        mExcelFileUri = null;
        mPasswordProtected = false;
        showEnhancementOptions();
        new Handler(Looper.getMainLooper()).postDelayed(() -> showGoogleInterstitialAds(1), 2000);
    }

    private void showGoogleInterstitialAds(int i) {
        if (i == 0) {
            setPassword();
        }
    }

    @Override
    public void onItemClick(int i) {
        if (!mCreateExcelPdf.isEnabled()) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.no_excel_file);
        } else {
            showGoogleInterstitialAds(i);
        }
    }

    private void setPassword() {
        MaterialDialog materialDialog = DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity, R.string.set_password).customView(R.layout.custom_dialog, true).neutralText(R.string.remove_dialog).build();
        final MDButton positiveButton = materialDialog.getActionButton(DialogAction.POSITIVE);
        MDButton neutralButton = materialDialog.getActionButton(DialogAction.NEUTRAL);
        View customView = materialDialog.getCustomView();
        Objects.requireNonNull(customView);
        EditText edPassword = customView.findViewById(R.id.password);
        edPassword.setText(mPassword);
        edPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                positiveButton.setEnabled(charSequence.toString().trim().length() > 4);
            }
        });
        positiveButton.setOnClickListener(view -> setPdfPasswprd(edPassword, materialDialog, view));
        if (StringUtils.getInstance().isNotEmpty(mPassword)) {
            neutralButton.setOnClickListener(view -> {
                mPassword = null;
                onPasswordRemoved();
                mPasswordProtected = false;
                materialDialog.dismiss();
                StringUtils.getInstance().showSnackbar(mActivity, R.string.password_remove);
            });
        }
        materialDialog.show();
        positiveButton.setEnabled(false);
    }

    public void setPdfPasswprd(EditText editText, MaterialDialog materialDialog, View view) {
        if (StringUtils.getInstance().isEmpty(editText.getText())) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_password_cannot_be_blank);
        } else if (editText.getText().toString().length() < 4) {
            Toast.makeText(mActivity, "Password can not be less than 4 characters.", Toast.LENGTH_SHORT).show();
        } else if (editText.getText().toString().length() > 8) {
            Toast.makeText(mActivity, "Password can not be greater than 8 characters.", Toast.LENGTH_SHORT).show();
        } else {
            mPassword = editText.toString();
            mPasswordProtected = true;
            onPasswordAdded();
            materialDialog.dismiss();
        }
    }

    private void onPasswordAdded() {
        mEnhancementOptionsEntityArrayList.get(0).setImage(mActivity.getResources().getDrawable(R.drawable.baseline_done_24));
        mEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    private void onPasswordRemoved() {
        mEnhancementOptionsEntityArrayList.get(0).setImage(mActivity.getResources().getDrawable(R.drawable.ic_add_password));
        mEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(String path) {

        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mExcelFileUri = Uri.parse("file://" + path);
        mRealPath = path;
        checkFileUri();
    }

    @Override
    public void onPopulate(ArrayList<String> arrayList) {
        CommonCodeUtils.getInstance().populateUtil(mActivity, arrayList, this, mLayout, mLottieProgress, mRecyclerViewFiles);
    }
}

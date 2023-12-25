package com.templatemela.smartpdfreader.fragment.texttopdf;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.morphingbutton.MorphingButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.adapter.EnhancementOptionsAdapter;
import com.templatemela.smartpdfreader.ads.AdsService;
import com.templatemela.smartpdfreader.interfaces.Enhancer;
import com.templatemela.smartpdfreader.interfaces.OnItemClickListener;
import com.templatemela.smartpdfreader.interfaces.OnTextToPdfInterface;
import com.templatemela.smartpdfreader.model.TextToPDFOptions;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.DialogUtils;
import com.templatemela.smartpdfreader.util.DirectoryUtils;
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.MorphButtonUtility;
import com.templatemela.smartpdfreader.util.PageSizeUtils;
import com.templatemela.smartpdfreader.util.PermissionsUtils;
import com.templatemela.smartpdfreader.util.StringUtils;
import com.templatemela.smartpdfreader.util.TextToPDFUtils;
import com.templatemela.smartpdfreader.util.TextToPdfConverter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TextToPdfFragment extends Fragment implements OnItemClickListener, OnTextToPdfInterface, TextToPdfContract.View {
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    private Activity mActivity;
    private TextToPDFOptions.Builder mBuilder;
    private int mButtonClicked = 0;
    @BindView(R.id.createtextpdf)
    MorphingButton mCreateTextPdf;
    private DirectoryUtils mDirectoryUtils;
    private List<Enhancer> mEnhancerList;
    private String mFileExtension;
    private String mFileNameWithType = null;
    private final int mFileSelectCode = 0;
    private FileUtils mFileUtils;
    private MaterialDialog mMaterialDialog;
    private MorphButtonUtility mMorphButtonUtility;
    @BindView(R.id.pdfOpen)
    MorphingButton mOpenPdf;
    private String mPath;
    private boolean mPermissionGranted = false;
    @BindView(R.id.selectFile)
    MorphingButton mSelectFile;
    private EnhancementOptionsAdapter mTextEnhancementOptionsAdapter;
    @BindView(R.id.enhancement_options_recycle_view_text)
    RecyclerView mTextEnhancementOptionsRecycleView;
    private Uri mTextFileUri = null;
    int whichOperation = 1;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_text_to_pdf, viewGroup, false);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        ButterKnife.bind(this, inflate);
        mBuilder = new TextToPDFOptions.Builder(getContext());
        addEnhancements();
        showEnhancementOptions();
        mMorphButtonUtility.morphToGrey(mCreateTextPdf, mMorphButtonUtility.integer());
        mCreateTextPdf.setEnabled(false);
        AdsService.getInstance().showAdaptiveBannerAd(inflate.findViewById(R.id.layoutDelete));
        return inflate;
    }


    @OnClick({R.id.pdfOpen})
    public void openPdf() {
        mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF);
    }

    private void addEnhancements() {
        mEnhancerList = new ArrayList();
        for (Enhancers enhancer : Enhancers.values()) {
            mEnhancerList.add(enhancer.getEnhancer(mActivity, this, mBuilder));
        }
    }

    private void showGoogleInterstitialAds() {
        if (mTextFileUri == null) {
            StringUtils.getInstance().showSnackbar(mActivity, "No Text File selected");
        } else {
            mEnhancerList.get(whichOperation).enhance();
        }
    }

    private void showEnhancementOptions() {
        mTextEnhancementOptionsRecycleView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        ArrayList arrayList = new ArrayList();
        for (Enhancer enhancementOptionsEntity : mEnhancerList) {
            arrayList.add(enhancementOptionsEntity.getEnhancementOptionsEntity());
        }
        mTextEnhancementOptionsAdapter = new EnhancementOptionsAdapter(this, arrayList);
        mTextEnhancementOptionsRecycleView.setAdapter(mTextEnhancementOptionsAdapter);
    }

    @Override
    public void onItemClick(int i) {
        if (mTextFileUri == null) {
            StringUtils.getInstance().showSnackbar(mActivity, "No Text File selected");
            return;
        }
        whichOperation = i;
        showGoogleInterstitialAds();
    }

    @OnClick({R.id.createtextpdf})
    public void openCreateTextPdf() {
        new MaterialDialog.Builder(mActivity).title(R.string.creating_pdf).content(R.string.enter_file_name).input(getString(R.string.example), mFileNameWithType, (materialDialog, charSequence) -> {
            if (StringUtils.getInstance().isEmpty(charSequence)) {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                return;
            }
            String input = charSequence.toString();
            if (!mFileUtils.isFileExist(input + getString(R.string.pdf_ext))) {
                createPdf(input);
            } else {
                DialogUtils.getInstance()
                        .createOverwriteDialog(mActivity)
                        .onPositive((materialDialog1, dialogAction) -> createPdf(input))
                        .onNegative((materialDialog12, dialogAction) -> openCreateTextPdf()).show();
            }
        }).show();
    }


    private void createPdf(String name) {
        mPath = mDirectoryUtils.getOrCreatePdfDirectory().getPath();
        mPath += Constants.PATH_SEPERATOR + name + mActivity.getString(R.string.pdf_ext);
        TextToPdfConverter.textToPdfConverter(new TextToPDFUtils(mActivity), mBuilder.setFileName(name).setPageSize(PageSizeUtils.mPageSize).setInFileUri(mTextFileUri).build(), mFileExtension, this);
    }

    @OnClick({R.id.selectFile})
    public void selectTextFile() {
       getRuntimePermissions();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mButtonClicked = 0;
        if (requestCode == 0 && resultCode == -1) {
            mTextFileUri = intent.getData();
            StringUtils.getInstance().showSnackbar(mActivity, R.string.text_file_selected);
            String fileName = mFileUtils.getFileName(mTextFileUri);
            if (fileName != null) {
                if (fileName.endsWith(Constants.textExtension)) {
                    mFileExtension = Constants.textExtension;
                } else if (fileName.endsWith(Constants.docxExtension)) {
                    mFileExtension = Constants.docxExtension;
                } else if (fileName.endsWith(Constants.docExtension)) {
                    mFileExtension = Constants.docExtension;
                } else {
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.extension_not_supported);
                    return;
                }
            }
            mFileNameWithType = mFileUtils.stripExtension(fileName) + getString(R.string.pdf_suffix);
            mSelectFile.setText(getString(R.string.text_file_name) + fileName);
            mCreateTextPdf.setEnabled(true);
            mMorphButtonUtility.morphToSquare(mCreateTextPdf, mMorphButtonUtility.integer());
            mOpenPdf.setVisibility(View.GONE);
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        } else {
            mActivity = getActivity();
        }
        mFileUtils = new FileUtils(mActivity);
        mDirectoryUtils = new DirectoryUtils(mActivity);
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
                                if (mButtonClicked == 0) {
                                    Uri parse = Uri.parse(Environment.getRootDirectory() + Constants.PATH_SEPERATOR);
                                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                                    intent.setDataAndType(parse, "*/*");
                                    intent.putExtra("android.intent.extra.MIME_TYPES", new String[]{"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword", getString(R.string.text_type)});
                                    intent.addCategory("android.intent.category.OPENABLE");
                                    try {
                                        startActivityForResult(Intent.createChooser(intent, String.valueOf(R.string.select_file)), 0);
                                    } catch (ActivityNotFoundException exception) {
                                        StringUtils.getInstance().showSnackbar(mActivity, R.string.install_file_manager);
                                    }
                                    mButtonClicked = 1;
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
                    .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                Toast.makeText(mActivity, "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                                if (mButtonClicked == 0) {
                                    Uri parse = Uri.parse(Environment.getRootDirectory() + Constants.PATH_SEPERATOR);
                                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                                    intent.setDataAndType(parse, "*/*");
                                    intent.putExtra("android.intent.extra.MIME_TYPES", new String[]{"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword", getString(R.string.text_type)});
                                    intent.addCategory("android.intent.category.OPENABLE");
                                    try {
                                        startActivityForResult(Intent.createChooser(intent, String.valueOf(R.string.select_file)), 0);
                                    } catch (ActivityNotFoundException exception) {
                                        StringUtils.getInstance().showSnackbar(mActivity, R.string.install_file_manager);
                                    }
                                    mButtonClicked = 1;
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
//        PermissionsUtils.getInstance().requestRuntimePermissions(mActivity, Constants.READ_WRITE_PERMISSIONS, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
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
    public void onPDFCreated(boolean isCreated) {
        showGoogleInterstitialAds();
        if (mMaterialDialog != null && mMaterialDialog.isShowing()) {
            mMaterialDialog.dismiss();
        }
        if (!isCreated) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.error_pdf_not_created);
            mMorphButtonUtility.morphToGrey(mCreateTextPdf, mMorphButtonUtility.integer());
            mCreateTextPdf.setEnabled(false);
            mTextFileUri = null;
            mButtonClicked = 0;
            return;
        }
        StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated).setAction(R.string.snackbar_viewAction, view -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF)).show();
        mSelectFile.setText(R.string.select_text_file);
        mMorphButtonUtility.morphToGrey(mCreateTextPdf, mMorphButtonUtility.integer());
        mCreateTextPdf.setEnabled(false);
        mTextFileUri = null;
        mButtonClicked = 0;
        mBuilder = new TextToPDFOptions.Builder(getContext());
        mOpenPdf.setVisibility(View.VISIBLE);
    }


    @Override
    public void updateView() {
        mTextEnhancementOptionsAdapter.notifyDataSetChanged();
    }
}

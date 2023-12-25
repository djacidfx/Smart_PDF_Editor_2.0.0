package com.templatemela.smartpdfreader.fragment;

import static com.vincent.filepicker.Constant.REQUEST_CODE_PICK_FILE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.dd.morphingbutton.MorphingButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.adapter.EnhancementOptionsAdapter;
import com.templatemela.smartpdfreader.adapter.MergeFilesAdapter;
import com.templatemela.smartpdfreader.interfaces.BottomSheetPopulate;
import com.templatemela.smartpdfreader.interfaces.OnBackPressedInterface;
import com.templatemela.smartpdfreader.interfaces.OnItemClickListener;
import com.templatemela.smartpdfreader.model.EnhancementOptionsEntity;
import com.templatemela.smartpdfreader.util.AddTextEnhancementOptionsUtils;
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
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTextFragment extends Fragment implements MergeFilesAdapter.OnClickListener, BottomSheetPopulate, OnBackPressedInterface, OnItemClickListener {
    private static final int INTENT_REQUEST_PICK_PDF_FILE_CODE = 10;
    private static final int INTENT_REQUEST_PICK_TEXT_FILE_CODE = 0;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    private Activity mActivity;
    private BottomSheetUtils mBottomSheetUtils;
    @BindView(R.id.create_pdf_added_text)
    MorphingButton mCreateTextPDF;
    private FileUtils mFileUtils;
    private Font.FontFamily mFontFamily;
    private int mFontSize = 0;
    private String mFontTitle;
    @BindView(R.id.layout)
    RelativeLayout mLayout;
    @BindView(R.id.lottie_progress)
    LottieAnimationView mLottieProgress;
    private MorphButtonUtility mMorphButtonUtility;
    @BindView(R.id.pdfOpen)
    MorphingButton mOpenPdf;
    String mPath = "";
    private String mPdfpath;
    private boolean mPermissionGranted;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    @BindView(R.id.select_pdf_file)
    MorphingButton mSelectPDF;
    @BindView(R.id.select_text_file)
    MorphingButton mSelectText;
    private SharedPreferences mSharedPreferences;
    private BottomSheetBehavior mSheetBehavior;
    private EnhancementOptionsAdapter mTextEnhancementOptionsAdapter;
    private ArrayList<EnhancementOptionsEntity> mTextEnhancementOptionsEntityArrayList;
    @BindView(R.id.enhancement_options_recycle_view_text)
    RecyclerView mTextEnhancementOptionsRecycleView;
    private String mTextPath;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_add_text, viewGroup, false);
        ButterKnife.bind(this, inflate);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mFontTitle = String.format(getString(R.string.edit_font_size), mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, 11));
        mFontFamily = Font.FontFamily.valueOf(mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT, Constants.DEFAULT_FONT_FAMILY));
        mFontSize = mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, 11);
        mSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mBottomSheetUtils.populateBottomSheetWithPDFsAndText(this);
        showEnhancementOptions();
        mLottieProgress.setVisibility(View.VISIBLE);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mUpArrow, isAdded()));
        resetView();
        return inflate;
    }


    @OnClick({R.id.pdfOpen})
    public void openPdf() {
        mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF);
    }

    private void showEnhancementOptions() {
        mTextEnhancementOptionsRecycleView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mTextEnhancementOptionsEntityArrayList = AddTextEnhancementOptionsUtils.getInstance().getEnhancementOptions(mActivity, mFontTitle, mFontFamily);
        mTextEnhancementOptionsAdapter = new EnhancementOptionsAdapter(this, mTextEnhancementOptionsEntityArrayList);
        mTextEnhancementOptionsRecycleView.setAdapter(mTextEnhancementOptionsAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
    }

    @OnClick({R.id.select_pdf_file})
    public void showPdfFileChooser() {
        try {
            mBottomSheetUtils.showHideSheet(mSheetBehavior);
        } catch (ActivityNotFoundException exception) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.install_file_manager);
        }
    }

    @OnClick({R.id.select_text_file})
    public void showTextFileChooser() {
        Uri parse = Uri.parse(Environment.getRootDirectory() + Constants.PATH_SEPERATOR);
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setDataAndType(parse, "application/text");
        intent.putExtra("android.intent.extra.MIME_TYPES", new String[]{"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword", getString(R.string.text_type)});
        intent.addCategory("android.intent.category.OPENABLE");
        try {
            startActivityForResult(Intent.createChooser(intent, String.valueOf(R.string.select_file)), INTENT_REQUEST_PICK_TEXT_FILE_CODE);
        } catch (ActivityNotFoundException exception) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.install_file_manager);
        }
    }

    @OnClick({R.id.create_pdf_added_text})
    public void openPdfNameDialog() {

            getRuntimePermissions();

            if (mPdfpath == null) {
                StringUtils.getInstance().showSnackbar(mActivity, "Please select PDF file.");
            } else if (mTextPath == null) {
                StringUtils.getInstance().showSnackbar(mActivity, "Please select TEXT file.");
            }
    }
    private void getRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            Dexter.withActivity(mActivity)
                    .withPermissions(
                            Manifest.permission.READ_MEDIA_IMAGES)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                Toast.makeText(mActivity, "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                                new MaterialDialog.Builder(mActivity).title(R.string.creating_pdf).content(R.string.enter_file_name)
                                        .input(getString(R.string.example),null,this::createPdf).show();
                            }
                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog();
                            }

                        }
                        public void createPdf(MaterialDialog materialDialog, CharSequence input) {
                            if (StringUtils.getInstance().isEmpty(input)) {
                                StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                                return;
                            }
                            String name = input.toString();
                            if (!mFileUtils.isFileExist(name + getString(R.string.pdf_ext))) {
                                addText(name, mFontSize, mFontFamily);
                            } else {
                                DialogUtils.getInstance().createOverwriteDialog(mActivity)
                                        .onPositive((materialDialog1, dialogAction) -> addText(name, mFontSize, mFontFamily)).onNegative((materialDialog12, dialogAction) -> openPdfNameDialog()).show();
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
                                new MaterialDialog.Builder(mActivity).title(R.string.creating_pdf).content(R.string.enter_file_name)
                                        .input(getString(R.string.example),null,this::createPdf).show();

                            }
                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog();
                            }
                        }

                        public void createPdf(MaterialDialog materialDialog, CharSequence input) {
                            if (StringUtils.getInstance().isEmpty(input)) {
                                StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                                return;
                            }
                            String name = input.toString();
                            if (!mFileUtils.isFileExist(name + getString(R.string.pdf_ext))) {
                                addText(name, mFontSize, mFontFamily);
                            } else {
                                DialogUtils.getInstance().createOverwriteDialog(mActivity)
                                        .onPositive((materialDialog1, dialogAction) -> addText(name, mFontSize, mFontFamily)).onNegative((materialDialog12, dialogAction) -> openPdfNameDialog()).show();
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
    public void onActivityResult(int requestCode, int i2, Intent intent) throws NullPointerException {
        if (intent != null && i2 == -1 && intent.getData() != null) {
            if (requestCode == INTENT_REQUEST_PICK_PDF_FILE_CODE) {
                mOpenPdf.setVisibility(View.GONE);
                mPdfpath = RealPathUtil.getInstance().getRealPath(getContext(), intent.getData());
                StringUtils.getInstance().showSnackbar(mActivity, getResources().getString(R.string.snackbar_pdfselected));
                setTextAndActivateButtons_PDF(mPdfpath);
            } else if (requestCode == INTENT_REQUEST_PICK_TEXT_FILE_CODE) {
                mOpenPdf.setVisibility(View.GONE);
                mTextPath = RealPathUtil.getInstance().getRealPath(getContext(), intent.getData());
                StringUtils.getInstance().showSnackbar(mActivity, getResources().getString(R.string.snackbar_txtselected));
                setTextAndActivateButtons_TEXT(mTextPath);
            }
        }
    }

    private void setTextAndActivateButtons_PDF(String path) {
        if (path == null) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.error_path_not_found);
        } else if (mPdfpath != null && mTextPath != null) {
            mMorphButtonUtility.setTextAndActivateButtons(path, mSelectPDF, mCreateTextPDF);
        }
    }

    private void setTextAndActivateButtons_TEXT(String textPath) {
        if (textPath == null) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.error_path_not_found);
        } else if (mPdfpath != null && mTextPath != null) {
            mMorphButtonUtility.setTextAndActivateButtons(textPath, mSelectText, mCreateTextPDF);
        }
    }

    @OnClick({R.id.viewFiles})
    public void onViewFilesClick(View view) {
        mBottomSheetUtils.showHideSheet(mSheetBehavior);
    }

    private void resetView() {
        mTextPath = null;
        mPdfpath = null;
        mMorphButtonUtility.morphToGrey(mCreateTextPDF, mMorphButtonUtility.integer());
        mCreateTextPDF.setEnabled(false);
        mFontSize = mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, 11);
        mFontFamily = Font.FontFamily.valueOf(mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT, Constants.DEFAULT_FONT_FAMILY));
        showFontSize();
        showFontFamily();
    }

    private void addText(String name, int size, Font.FontFamily fontFamily) {
        mPath = mSharedPreferences.getString(Constants.STORAGE_LOCATION, StringUtils.getInstance().getDefaultStorageLocation()) + name + Constants.pdfExtension;
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(mTextPath));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                sb.append(readLine);
                sb.append(10);
            }
            bufferedReader.close();
            FileOutputStream fileOutputStream = new FileOutputStream(mPath);
            PdfReader.unethicalreading = true;
            PdfReader pdfReader = new PdfReader(mPdfpath);
            Document document = new Document(pdfReader.getPageSize(1));
            PdfWriter instance = PdfWriter.getInstance(document, fileOutputStream);
            document.open();
            PdfContentByte directContent = instance.getDirectContent();
            for (int i2 = 1; i2 <= pdfReader.getNumberOfPages(); i2++) {
                PdfImportedPage importedPage = instance.getImportedPage(pdfReader, i2);
                document.newPage();
                directContent.addTemplate(importedPage, 0.0f, 0.0f);
            }
            document.setPageSize(pdfReader.getPageSize(1));
            document.newPage();
            document.add(new Paragraph(new Paragraph(sb.toString(), FontFactory.getFont(fontFamily.name(), (float) size))));
            document.close();
            StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated).setAction(R.string.snackbar_viewAction, view -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF)).show();
            mOpenPdf.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            mMorphButtonUtility.initializeButtonForAddText(mSelectPDF, mSelectText, mCreateTextPDF);
            resetView();
            throw th;
        }
        mMorphButtonUtility.initializeButtonForAddText(mSelectPDF, mSelectText, mCreateTextPDF);
        resetView();
    }



    @Override
    public void onItemClick(String path) {
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        if (path.toLowerCase().endsWith("txt")) {
            mTextPath = path;
            StringUtils.getInstance().showSnackbar(mActivity, getResources().getString(R.string.snackbar_txtselected));
            setTextAndActivateButtons_TEXT(mTextPath);
            return;
        }
        mPdfpath = path;
        StringUtils.getInstance().showSnackbar(mActivity, getResources().getString(R.string.snackbar_pdfselected));
        setTextAndActivateButtons_PDF(mPdfpath);
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

    @Override
    public void onItemClick(int i) {
        if (i != 0) {
            if (i == 1) {
                if (mPdfpath == null) {
                    StringUtils.getInstance().showSnackbar(mActivity, "Please select PDF file.");
                } else if (mTextPath == null) {
                    StringUtils.getInstance().showSnackbar(mActivity, "Please select TEXT file.");
                } else {
                    showGoogleInterstitialAds(1);
                }
            }
        } else if (mPdfpath == null) {
            StringUtils.getInstance().showSnackbar(mActivity, "Please select PDF file.");
        } else if (mTextPath == null) {
            StringUtils.getInstance().showSnackbar(mActivity, "Please select TEXT file.");
        } else {
            showGoogleInterstitialAds(0);
        }
    }

    private void showGoogleInterstitialAds(int i) {
        if (i == 0) {
            editFontSize();
        } else if (i == 1) {
            changeFontFamily();
        }
    }

    private void editFontSize() {
        new MaterialDialog.Builder(mActivity).title(mFontTitle).customView(R.layout.dialog_font_size, true).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive((materialDialog, dialogAction) -> edit(materialDialog, dialogAction)).show();
    }

    public void edit(MaterialDialog materialDialog, DialogAction dialogAction) {
        EditText editText = materialDialog.getCustomView().findViewById(R.id.fontInput);
        CheckBox checkBox = materialDialog.getCustomView().findViewById(R.id.cbSetFontDefault);
        try {
            int parseInt = Integer.parseInt(String.valueOf(editText.getText()));
            if (parseInt <= 1000) {
                if (parseInt >= 0) {
                    mFontSize = parseInt;
                    showFontSize();
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.font_size_changed);
                    if (checkBox.isChecked()) {
                        SharedPreferences.Editor edit = mSharedPreferences.edit();
                        edit.putInt(Constants.DEFAULT_FONT_SIZE_TEXT, mFontSize);
                        edit.apply();
                        mFontTitle = String.format(getString(R.string.edit_font_size), mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, 11));
                        return;
                    }
                    return;
                }
            }
            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
        } catch (NumberFormatException exception) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
        }
    }


    private void showFontSize() {
        String size = getString(R.string.font_size);
        mTextEnhancementOptionsEntityArrayList.get(0).setName(String.format(size, mFontSize + ""));
        mTextEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    private void changeFontFamily() {
        String fontFamily = mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT, Constants.DEFAULT_FONT_FAMILY);
        int ordinal = Font.FontFamily.valueOf(fontFamily).ordinal();
        MaterialDialog build = new MaterialDialog.Builder(mActivity)
                .title(String.format(getString(R.string.default_font_family_text), fontFamily))
                .customView(R.layout.dialog_font_family, true)
                .positiveText(R.string.ok).negativeText(R.string.cancel)
                .onPositive((materialDialog, dialogAction) -> {
            View customView = materialDialog.getCustomView();
            String charSequence = ((RadioButton) customView.findViewById(((RadioGroup) customView.findViewById(R.id.radio_group_font_family)).getCheckedRadioButtonId())).getText().toString();
            mFontFamily = Font.FontFamily.valueOf(charSequence);
            SharedPreferences.Editor edit = mSharedPreferences.edit();
            edit.putString(Constants.DEFAULT_FONT_FAMILY_TEXT, charSequence);
            edit.apply();
            showFontFamily();
        }).build();
        ((RadioButton) ((RadioGroup) build.getCustomView().findViewById(R.id.radio_group_font_family)).getChildAt(ordinal)).setChecked(true);
        build.show();
    }


    private void showFontFamily() {
        mTextEnhancementOptionsEntityArrayList.get(1).setName(getString(R.string.font_family_text) + mFontFamily.name());
        mTextEnhancementOptionsAdapter.notifyDataSetChanged();
    }
}

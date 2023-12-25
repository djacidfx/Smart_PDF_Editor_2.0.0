package com.templatemela.smartpdfreader.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.dd.morphingbutton.MorphingButton;
import com.esafirm.imagepicker.features.ImagePicker;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.activity.TM_CropImageActivity;
import com.templatemela.smartpdfreader.activity.TM_ImageEditor;
import com.templatemela.smartpdfreader.activity.TM_PreviewActivity;
import com.templatemela.smartpdfreader.activity.TM_RearrangeImages;
import com.templatemela.smartpdfreader.adapter.EnhancementOptionsAdapter;
import com.templatemela.smartpdfreader.ads.AdsService;
import com.templatemela.smartpdfreader.database.DatabaseHelper;
import com.templatemela.smartpdfreader.interfaces.OnItemClickListener;
import com.templatemela.smartpdfreader.interfaces.OnPDFCreatedInterface;
import com.templatemela.smartpdfreader.model.ImageToPDFOptions;
import com.templatemela.smartpdfreader.model.Watermark;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.CreatePdf;
import com.templatemela.smartpdfreader.util.DialogUtils;
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.ImageEnhancementOptionsUtils;
import com.templatemela.smartpdfreader.util.ImageUtils;
import com.templatemela.smartpdfreader.util.InputFilterMinMax;
import com.templatemela.smartpdfreader.util.MorphButtonUtility;
import com.templatemela.smartpdfreader.util.PageSizeUtils;
import com.templatemela.smartpdfreader.util.PermissionsUtils;
import com.templatemela.smartpdfreader.util.StringUtils;
import com.templatemela.smartpdfreader.util.WatermarkUtils;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageToPdfFragment extends Fragment implements OnItemClickListener, OnPDFCreatedInterface {
    private static final int INTENT_REQUEST_APPLY_FILTER = 10;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int INTENT_REQUEST_PREVIEW_IMAGE = 11;
    private static final int INTENT_REQUEST_REARRANGE_IMAGE = 12;
    private static final int REQUEST_PERMISSIONS_CODE = 124;
    public static ArrayList<String> mImagesUri = new ArrayList<>();
    private static final ArrayList<String> mUnarrangedImagesUri = new ArrayList<>();
    final int OPERATION_ADD_MARGIN = 11;
    final int OPERATION_BORDER_WIDTH = 8;
    final int OPERATION_CREATE_PDF = 10;
    final int OPERATION_EDIT_IMAGE = 2;
    final int OPERATION_FILTER_IMAGES = 4;
    final int OPERATION_IMAGE_COMPRESSION = 3;
    final int OPERATION_IMAGE_SCALE_TYPE = 6;
    final int OPERATION_PAGE_COLOR = 14;
    final int OPERATION_PASSWORD_PROTECTED = 1;
    final int OPERATION_PREVIEW_PDF = 7;
    final int OPERATION_REARRANGE_IMAGES = 9;
    final int OPERATION_SET_PAGE_SIZE = 5;
    final int OPERATION_SHOW_PAGE_NUMBER = 12;
    final int OPERATION_WATERMARK = 13;
    private boolean isFromZipToPDF = false;


    public Activity mActivity;
    private int mChoseId;
    @BindView(R.id.pdfCreate)
    MorphingButton mCreatePdf;
    @BindView(R.id.enhancement_options_recycle_view)
    RecyclerView mEnhancementOptionsRecycleView;
    private FileUtils mFileUtils;
    private String mHomePath;
    private boolean mIsButtonAlreadyClicked = false;
    private int mMarginBottom = 38;
    private int mMarginLeft = 50;
    private int mMarginRight = 38;
    private int mMarginTop = 50;
    private MaterialDialog mMaterialDialog;
    private MorphButtonUtility mMorphButtonUtility;
    @BindView(R.id.tvNoOfImages)
    TextView mNoOfImages;
    @BindView(R.id.pdfOpen)
    MorphingButton mOpenPdf;
    private int mPageColor;
    private String mPageNumStyle;
    private PageSizeUtils mPageSizeUtils;
    private String mPath;
    private ImageToPDFOptions mPdfOptions;
    private boolean mPermissionGranted = false;
    private SharedPreferences mSharedPreferences;
    int whichOperation = 1;




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_images_to_pdf, viewGroup, false);
        ButterKnife.bind(this, inflate);
        AdsService.getInstance().showAdaptiveBannerAd(inflate.findViewById(R.id.layoutDelete));
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mPageSizeUtils = new PageSizeUtils(mActivity);
        mPageColor = mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_ITP, -1);
        mHomePath = mSharedPreferences.getString(Constants.STORAGE_LOCATION, StringUtils.getInstance().getDefaultStorageLocation());
        resetValues();
        checkForImagesInBundle();
        if (mImagesUri.size() > 0) {
            mNoOfImages.setText(String.format(mActivity.getResources().getString(R.string.images_selected), mImagesUri.size()));
            mNoOfImages.setVisibility(View.VISIBLE);
            mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());
            mCreatePdf.setEnabled(true);
            StringUtils.getInstance().showSnackbar(mActivity, R.string.successToast);
        } else {
            mNoOfImages.setVisibility(View.GONE);
            mMorphButtonUtility.morphToGrey(mCreatePdf, mMorphButtonUtility.integer());
        }
        return inflate;
    }

    private void checkForImagesInBundle() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            if (arguments.getBoolean(Constants.OPEN_SELECT_IMAGES)) {
                startAddingImages();
            }
            ArrayList parcelableArrayList = arguments.getParcelableArrayList(getString(R.string.bundleKey));
            if (parcelableArrayList != null) {
                Iterator it2 = parcelableArrayList.iterator();
                while (it2.hasNext()) {
                    Uri uri = (Uri) it2.next();
                    if (mFileUtils.getUriRealPath(uri) == null) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.whatsappToast);
                    } else {
                        isFromZipToPDF = true;
                        mImagesUri.add(mFileUtils.getUriRealPath(uri));
                    }
                }
            }
        }
    }

    public void showEnhancementOptions() {
        mEnhancementOptionsRecycleView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mEnhancementOptionsRecycleView.setAdapter(new EnhancementOptionsAdapter(this, ImageEnhancementOptionsUtils.getInstance().getEnhancementOptions(mActivity, mPdfOptions)));
    }


    @OnClick({R.id.addImages})
    public void startAddingImages() {
        getRuntimePermissions();

    }

    private void openView() {
        switch (whichOperation) {
            case OPERATION_PASSWORD_PROTECTED:
                passwordProtectPDF();
                return;
            case OPERATION_EDIT_IMAGE:
                cropImage();
                return;
            case OPERATION_IMAGE_COMPRESSION:
                compressImage();
                return;
            case OPERATION_FILTER_IMAGES:
                startActivityForResult(TM_ImageEditor.jumpActivity(mActivity, mImagesUri), INTENT_REQUEST_APPLY_FILTER);
                return;
            case OPERATION_SET_PAGE_SIZE:
                mPageSizeUtils.showPageSizeDialog(false);
                return;
            case OPERATION_IMAGE_SCALE_TYPE:
                ImageUtils.getInstance().showImageScaleTypeDialog(mActivity, false);
                return;
            case OPERATION_PREVIEW_PDF:
                startActivityForResult(TM_PreviewActivity.jumpActivity(mActivity, mImagesUri), INTENT_REQUEST_PREVIEW_IMAGE);
                return;
            case OPERATION_BORDER_WIDTH:
                addBorder();
                return;
            case OPERATION_REARRANGE_IMAGES:
                startActivityForResult(TM_RearrangeImages.jumpActivity(mActivity, mImagesUri), INTENT_REQUEST_REARRANGE_IMAGE);
                return;
            case OPERATION_CREATE_PDF:
                createPdf(true);
                return;
            case OPERATION_ADD_MARGIN:
                addMargins();
                return;
            case OPERATION_SHOW_PAGE_NUMBER:
                addPageNumbers();
                return;
            case OPERATION_WATERMARK:
                addWatermark();
                return;
            case OPERATION_PAGE_COLOR:
                setPageColor();
                return;
            default:
                return;
        }
    }

    @OnClick({R.id.pdfCreate})
    public void pdfCreateClicked() {
        createPdf(false);
    }

    private void createPdf(boolean isCreated) {
        mPdfOptions.setImagesUri(mImagesUri);
        mPdfOptions.setPageSize(PageSizeUtils.mPageSize);
        mPdfOptions.setImageScaleType(ImageUtils.getInstance().mImageScaleType);
        mPdfOptions.setPageNumStyle(mPageNumStyle);
        mPdfOptions.setMasterPwd(mSharedPreferences.getString(Constants.MASTER_PWD_STRING, Constants.appName));
        mPdfOptions.setPageColor(mPageColor);
        DialogUtils.getInstance().createCustomDialog(mActivity, R.string.creating_pdf, R.string.enter_file_name)
                .input(getString(R.string.example), mFileUtils.getLastFileName(mImagesUri), (materialDialog, input) -> createPdf(isCreated, input)).show();
    }

    public void createPdf(boolean isCreated, CharSequence input) {
        if (StringUtils.getInstance().isEmpty(input)) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
            return;
        }
        String name = input.toString();
        FileUtils fileUtils = new FileUtils(mActivity);
        if (!fileUtils.isFileExist(name + getString(R.string.pdf_ext))) {
            mPdfOptions.setOutFileName(name);
            if (isCreated) {
                saveImagesInGrayScale();
            }
            CreatePdf.CreatePdfInitialize(mPdfOptions, mHomePath, this);
            return;
        }
        DialogUtils.getInstance().createOverwriteDialog(mActivity)
                .onPositive((materialDialog, dialogAction) -> {
                    mPdfOptions.setOutFileName(name);
                    if (isCreated) {
                        saveImagesInGrayScale();
                    }
                    CreatePdf.CreatePdfInitialize(mPdfOptions, mHomePath, ImageToPdfFragment.this);
                }).onNegative((materialDialog, dialogAction) -> createPdf(isCreated)).show();
    }


    @OnClick({R.id.pdfOpen})
    public void openPdf() {
        mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        int i3 = 0;
        mIsButtonAlreadyClicked = false;


        if (resultCode == -1 && intent != null) {
            if (requestCode != 203) {
                switch (requestCode) {
                    case INTENT_REQUEST_APPLY_FILTER:
                        mImagesUri.clear();
                        ArrayList<String> listExtra = intent.getStringArrayListExtra(Constants.RESULT);
                        int size = listExtra.size() - 1;
                        while (i3 <= size) {
                            mImagesUri.add(listExtra.get(i3));
                            i3++;
                        }
                        break;
                    case INTENT_REQUEST_PREVIEW_IMAGE:
                        mImagesUri = intent.getStringArrayListExtra(Constants.RESULT);
                        if (mImagesUri.size() > 0) {
                            mNoOfImages.setText(String.format(mActivity.getResources().getString(R.string.images_selected), mImagesUri.size()));
                            return;
                        }
                        mNoOfImages.setVisibility(View.GONE);
                        mMorphButtonUtility.morphToGrey(mCreatePdf, mMorphButtonUtility.integer());
                        mCreatePdf.setEnabled(false);
                        break;
                    case INTENT_REQUEST_REARRANGE_IMAGE:
                        mImagesUri = intent.getStringArrayListExtra(Constants.RESULT);
                        ArrayList<String> arrayList = mUnarrangedImagesUri;
                        if (!arrayList.equals(mImagesUri) && mImagesUri.size() > 0) {
                            mNoOfImages.setText(String.format(mActivity.getResources().getString(R.string.images_selected), mImagesUri.size()));
                            StringUtils.getInstance().showSnackbar(mActivity, (int) R.string.images_rearranged);
                            arrayList.clear();
                            arrayList.addAll(mImagesUri);
                        }
                        if (mImagesUri.size() == 0) {
                            mNoOfImages.setVisibility(View.GONE);
                            mMorphButtonUtility.morphToGrey(mCreatePdf, mMorphButtonUtility.integer());
                            mCreatePdf.setEnabled(false);
                            return;
                        }
                        break;
                    case INTENT_REQUEST_GET_IMAGES:

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                            if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == RESULT_OK) {
                                if (intent != null) {
                                    mImagesUri.clear();
                                    mUnarrangedImagesUri.clear();
                                    ClipData clipData = intent.getClipData();
                                    if (clipData != null) {
                                        for (int i = 0; i < clipData.getItemCount(); i++) {
                                            Uri selectedImageUri = clipData.getItemAt(i).getUri();
                                            mImagesUri.add(getPathFromURI(selectedImageUri,mActivity));
                                            Log.d("TAG", "onActivityResult: check this image path " + getPathFromURI(selectedImageUri,mActivity));
                                        }
                                    }
                                    mUnarrangedImagesUri.addAll(mImagesUri);
                                    if (mImagesUri.size() > 0) {
                                        mNoOfImages.setText(String.format(mActivity.getResources().getString(R.string.images_selected), mImagesUri.size()));
                                        mNoOfImages.setVisibility(View.VISIBLE);
                                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_images_added);
                                        mCreatePdf.setEnabled(true);
                                        mCreatePdf.unblockTouch();
                                    }
                                    mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());
                                    mOpenPdf.setVisibility(View.GONE);
//                                    else {
//                                        // Handle the single selected image URI if not multiple selections.
//                                        Uri selectedImageUri = intent.getData();
//                                        // Handle the selected image URI as needed.
//                                    }
                                }
                            }
                        }else {
                            if (!isFromZipToPDF) {
                                mImagesUri.clear();
                                mUnarrangedImagesUri.clear();
                            }
                            for (int i4 = 0; i4 < ImagePicker.getImages(intent).size(); i4++) {
                                mImagesUri.add(ImagePicker.getImages(intent).get(i4).getPath());
                            }
                            mUnarrangedImagesUri.addAll(mImagesUri);
                            if (mImagesUri.size() > 0) {
                                mNoOfImages.setText(String.format(mActivity.getResources().getString(R.string.images_selected), mImagesUri.size()));
                                mNoOfImages.setVisibility(View.VISIBLE);
                                StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_images_added);
                                mCreatePdf.setEnabled(true);
                                mCreatePdf.unblockTouch();
                            }
                            mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());
                            mOpenPdf.setVisibility(View.GONE);
                        }
                            break;
                    default:
                        return;
                }
            } else {
                HashMap hashMap = (HashMap) intent.getSerializableExtra(CropImage.CROP_IMAGE_EXTRA_RESULT);
                while (i3 < mImagesUri.size()) {
                    if (hashMap.get(Integer.valueOf(i3)) != null) {
                        mImagesUri.set(i3, ((Uri) hashMap.get(Integer.valueOf(i3))).getPath());
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_imagecropped);
                    }
                    i3++;
                }
            }
        }
    }

    @Override
    public void onItemClick(int i) {
        if (mImagesUri.size() == 0) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_no_images);
            return;
        }
        switch (i) {
            case 0:
                whichOperation = OPERATION_PASSWORD_PROTECTED;
                openView();
                return;
            case 1:
                whichOperation = OPERATION_EDIT_IMAGE;
                openView();
                return;
            case 2:
                whichOperation = OPERATION_IMAGE_COMPRESSION;
                openView();
                return;
            case 3:
                whichOperation = OPERATION_FILTER_IMAGES;
                openView();
                return;
            case 4:
                whichOperation = OPERATION_SET_PAGE_SIZE;
                openView();
                return;
            case 5:
                whichOperation = OPERATION_IMAGE_SCALE_TYPE;
                openView();
                return;
            case 6:
                whichOperation = OPERATION_PREVIEW_PDF;
                openView();
                return;
            case 7:
                whichOperation = OPERATION_BORDER_WIDTH;
                openView();
                return;
            case 8:
                whichOperation = OPERATION_REARRANGE_IMAGES;
                openView();
                return;
            case 9:
                whichOperation = OPERATION_CREATE_PDF;
                openView();
                return;
            case 10:
                whichOperation = OPERATION_ADD_MARGIN;
                openView();
                return;
            case 11:
                whichOperation = OPERATION_SHOW_PAGE_NUMBER;
                openView();
                return;
            case 12:
                whichOperation = OPERATION_WATERMARK;
                openView();
                return;
            case 13:
                whichOperation = OPERATION_PAGE_COLOR;
                openView();
                return;
            default:
                return;
        }
    }

    private void saveImagesInGrayScale() {
        ArrayList arrayList = new ArrayList();
        try {
            File externalStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(externalStorageDirectory.getAbsolutePath() + "/PDFfilter");
            file.mkdirs();
            int size = mImagesUri.size();
            for (int i = 0; i < size; i++) {
                String string = getString(R.string.filter_file_name);
                String absolutePath = new File(file, String.format(string, new Object[]{System.currentTimeMillis() + "", i + "_grayscale"})).getAbsolutePath();
                Bitmap grayscale = ImageUtils.getInstance().toGrayscale(BitmapFactory.decodeStream(new FileInputStream(new File(mImagesUri.get(i)))));
                File file2 = new File(absolutePath);
                file2.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 8192);
                grayscale.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                fileOutputStream.close();
                arrayList.add(absolutePath);
            }
            mImagesUri.clear();
            mImagesUri.addAll(arrayList);
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
    }

    private void addBorder() {
        DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity, R.string.border)
                .customView(R.layout.dialog_border_image, true)
                .onPositive((dialog1, which) -> {
                    View view = dialog1.getCustomView();
                    final EditText input = view.findViewById(R.id.border_width);
                    int value = 0;
                    try {
                        value = Integer.parseInt(String.valueOf(input.getText()));
                        if (value > 200 || value < 0) {
                            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                        } else {
                            mPdfOptions.setBorderWidth(value);
                            showEnhancementOptions();
                        }
                    } catch (NumberFormatException e) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                    }
                    final CheckBox cbSetDefault = view.findViewById(R.id.cbSetDefault);
                    if (cbSetDefault.isChecked()) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putInt(Constants.DEFAULT_IMAGE_BORDER_TEXT, value);
                        editor.apply();
                    }
                }).build().show();
    }

    private void compressImage() {
        DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity, R.string.compression_image_edit)
                .customView(R.layout.compress_image_dialog, true).onPositive((materialDialog, dialogAction) -> imageComp(materialDialog, dialogAction)).show();
    }

    public void imageComp(MaterialDialog materialDialog, DialogAction dialogAction) {
        EditText editText = materialDialog.getCustomView().findViewById(R.id.quality);
        CheckBox checkBox = materialDialog.getCustomView().findViewById(R.id.cbSetDefault);
        try {
            int parseInt = Integer.parseInt(String.valueOf(editText.getText()));
            if (parseInt <= 100) {
                if (parseInt >= 0) {
                    mPdfOptions.setQualityString(String.valueOf(parseInt));
                    if (checkBox.isChecked()) {
                        SharedPreferences.Editor edit = mSharedPreferences.edit();
                        edit.putInt(Constants.DEFAULT_COMPRESSION, parseInt);
                        edit.apply();
                    }
                    showEnhancementOptions();
                    return;
                }
            }
            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
        } catch (NumberFormatException exception) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
        }
    }

    private void passwordProtectPDF() {
        MaterialDialog build = new MaterialDialog.Builder(mActivity).title(R.string.set_password).customView(R.layout.custom_dialog, true).positiveText(android.R.string.ok).negativeText(android.R.string.cancel).neutralText(R.string.remove_dialog).build();
        final MDButton posButton = build.getActionButton(DialogAction.POSITIVE);
        MDButton neutralButton = build.getActionButton(DialogAction.NEUTRAL);
        EditText edPass = build.getCustomView().findViewById(R.id.password);
        edPass.setText(mPdfOptions.getPassword());
        edPass.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                posButton.setEnabled(charSequence.toString().trim().length() > 4);
            }
        });
        posButton.setOnClickListener(view -> setFinalPass(edPass, build, view));
        if (StringUtils.getInstance().isNotEmpty(mPdfOptions.getPassword())) {
            neutralButton.setOnClickListener(view -> {
                mPdfOptions.setPassword(null);
                mPdfOptions.setPasswordProtected(false);
                showEnhancementOptions();
                build.dismiss();
                StringUtils.getInstance().showSnackbar(mActivity, R.string.password_remove);
            });
        }
        build.show();
        posButton.setEnabled(false);
    }

    public void setFinalPass(EditText editText, MaterialDialog materialDialog, View view) {
        if (StringUtils.getInstance().isEmpty(editText.getText())) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_password_cannot_be_blank);
        } else if (editText.getText().toString().length() < 4) {
            Toast.makeText(mActivity, "Password can not be less than 4 characters.", Toast.LENGTH_SHORT).show();
        } else if (editText.getText().toString().length() > 8) {
            Toast.makeText(mActivity, "Password can not be greater than 8 characters.", Toast.LENGTH_SHORT).show();
        } else {
            mPdfOptions.setPassword(editText.getText().toString());
            mPdfOptions.setPasswordProtected(true);
            showEnhancementOptions();
            materialDialog.dismiss();
        }
    }


    private void addWatermark() {

        MaterialDialog dialog = new MaterialDialog.Builder(mActivity).title(R.string.add_watermark)
                .customView(R.layout.add_watermark_dialog, true).positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel).neutralText(R.string.remove_dialog).build();
        final MDButton positiveBtn = dialog.getActionButton(DialogAction.POSITIVE);
        MDButton neutralBtn = dialog.getActionButton(DialogAction.NEUTRAL);
        final Watermark watermark = new Watermark();
        EditText edWatermarkText = dialog.getCustomView().findViewById(R.id.watermarkText);
        EditText edAngle = dialog.getCustomView().findViewById(R.id.watermarkAngle);
        edAngle.setFilters(new InputFilter[]{new InputFilterMinMax("1", "360")});
        ColorPickerView watermarkColor = dialog.getCustomView().findViewById(R.id.watermarkColor);
        EditText edFontSize = dialog.getCustomView().findViewById(R.id.watermarkFontSize);
        Spinner spnFontFamily = dialog.getCustomView().findViewById(R.id.watermarkFontFamily);
        Spinner spnWatermarkStyle = dialog.getCustomView().findViewById(R.id.watermarkStyle);
        edFontSize.setFilters(new InputFilter[]{new InputFilterMinMax("1", "36")});
        ArrayAdapter arrayAdapter = new ArrayAdapter(mActivity, android.R.layout.simple_spinner_dropdown_item, Font.FontFamily.values());
        spnFontFamily.setAdapter(arrayAdapter);
        spnFontFamily.setOnTouchListener((view, motionEvent) -> ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0));
        spnWatermarkStyle.setOnTouchListener((view, motionEvent) -> ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0));


        ArrayAdapter arrayAdapter2 = new ArrayAdapter(mActivity, android.R.layout.simple_spinner_dropdown_item, mActivity.getResources().getStringArray(R.array.fontStyles));
        spnWatermarkStyle.setAdapter(arrayAdapter2);
        if (mPdfOptions.isWatermarkAdded()) {
            edWatermarkText.setText(mPdfOptions.getWatermark().getWatermarkText());
            edAngle.setText(String.valueOf(mPdfOptions.getWatermark().getRotationAngle()));
            edFontSize.setText(String.valueOf(mPdfOptions.getWatermark().getTextSize()));
            BaseColor textColor = mPdfOptions.getWatermark().getTextColor();
            watermarkColor.setColor(Color.argb(textColor.getAlpha(), textColor.getRed(), textColor.getGreen(), textColor.getBlue()));
            spnFontFamily.setSelection(arrayAdapter.getPosition(mPdfOptions.getWatermark().getFontFamily()));
            spnWatermarkStyle.setSelection(arrayAdapter2.getPosition(WatermarkUtils.getStyleNameFromFont(mPdfOptions.getWatermark().getFontStyle())));
        }
        edWatermarkText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                positiveBtn.setEnabled(charSequence.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtils.getInstance().isEmpty(editable)) {
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_watermark_cannot_be_blank);
                    return;
                }
                watermark.setWatermarkText(editable.toString());
                showEnhancementOptions();
            }
        });
        neutralBtn.setEnabled(mPdfOptions.isWatermarkAdded());
        positiveBtn.setEnabled(mPdfOptions.isWatermarkAdded());
        neutralBtn.setOnClickListener(view -> {
            mPdfOptions.setWatermarkAdded(false);
            showEnhancementOptions();
            dialog.dismiss();
            StringUtils.getInstance().showSnackbar(mActivity, R.string.watermark_remove);
        });
        positiveBtn.setOnClickListener(view -> setWatermark(watermark, edWatermarkText, spnFontFamily, spnWatermarkStyle, edAngle, edFontSize, watermarkColor, dialog, view));
        dialog.show();
    }

    public void setWatermark(Watermark watermark, EditText editText, Spinner spinner, Spinner spinner2, EditText editText2, EditText editText3, ColorPickerView colorPickerView, MaterialDialog materialDialog, View view) {
        watermark.setWatermarkText(editText.getText().toString());
        watermark.setFontFamily((Font.FontFamily) spinner.getSelectedItem());
        watermark.setFontStyle(WatermarkUtils.getStyleValueFromName((String) spinner2.getSelectedItem()));
        if (StringUtils.getInstance().isEmpty(editText2.getText())) {
            watermark.setRotationAngle(0);
        } else {
            watermark.setRotationAngle(Integer.parseInt(editText2.getText().toString()));
        }
        if (StringUtils.getInstance().isEmpty(editText3.getText())) {
            watermark.setTextSize(50);
        } else {
            watermark.setTextSize(Integer.parseInt(editText3.getText().toString()));
        }
        watermark.setTextColor(new BaseColor(Color.red(colorPickerView.getColor()), Color.green(colorPickerView.getColor()), Color.blue(colorPickerView.getColor()), Color.alpha(colorPickerView.getColor())));
        mPdfOptions.setWatermark(watermark);
        mPdfOptions.setWatermarkAdded(true);
        showEnhancementOptions();
        materialDialog.dismiss();
        StringUtils.getInstance().showSnackbar(mActivity, R.string.watermark_added);
    }

    private void setPageColor() {
        MaterialDialog build = new MaterialDialog.Builder(mActivity).title(R.string.page_color).customView(R.layout.dialog_color_chooser, true).positiveText(R.string.ok).neutralText(R.string.remove_dialog).negativeText(R.string.cancel).onNeutral((materialDialog, dialogAction) -> {
            mPageColor = -1;
            SharedPreferences.Editor edit = mSharedPreferences.edit();
            edit.putInt(Constants.DEFAULT_PAGE_COLOR_ITP, mPageColor);
            edit.apply();
            StringUtils.getInstance().showSnackbar(mActivity, "Page color removed.");
        }).onPositive((materialDialog, dialogAction) -> {
            View customView = materialDialog.getCustomView();
            ColorPickerView colorPickerView = customView.findViewById(R.id.color_picker);

            mPageColor = colorPickerView.getColor();
            if (((CheckBox) customView.findViewById(R.id.set_default)).isChecked()) {
                SharedPreferences.Editor edit = mSharedPreferences.edit();
                edit.putInt(Constants.DEFAULT_PAGE_COLOR_ITP, mPageColor);
                edit.apply();
            }
            StringUtils.getInstance().showSnackbar(mActivity, R.string.page_color_saved);
        }).build();
        ColorPickerView colorPickerView = build.getCustomView().findViewById(R.id.color_picker);
        colorPickerView.setColor(mPageColor);
        build.show();
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
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_folder_not_created);
            return;
        }
        new DatabaseHelper(mActivity).insertRecord(path, mActivity.getString(R.string.created));
        StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated).setAction(R.string.snackbar_viewAction, view -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF)).show();
        mOpenPdf.setVisibility(View.VISIBLE);
        mMorphButtonUtility.morphToSuccess(mCreatePdf);
        mCreatePdf.blockTouch();
        mPath = path;
        resetValues();
    }

    private void cropImage() {
        startActivityForResult(new Intent(mActivity, TM_CropImageActivity.class), 203);
    }
    private void getRuntimePermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            Dexter.withActivity(mActivity)
                    .withPermissions(Manifest.permission.CAMERA,
                            Manifest.permission.READ_MEDIA_IMAGES)
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
        else {
            Dexter.withActivity(mActivity)
                    .withPermissions(Manifest.permission.CAMERA,
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
            intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, 100);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), INTENT_REQUEST_GET_IMAGES);
        }else {
        ImagePicker.create(this).folderMode(true).includeAnimation(true).multi().limit(100).showCamera(true).toolbarFolderTitle("Folder").toolbarImageTitle("Tap to select").toolbarDoneButtonText("DONE").start(INTENT_REQUEST_GET_IMAGES);
        }

    }

    public String getPathFromURI(Uri uri, Context context) {
        if (uri == null) {
            // Handle the error or show user feedback.
            return null;
        }

        String filePath = null;
        final String scheme = uri.getScheme();

        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] projection = { MediaStore.Images.Media.DATA };
            try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Handle the exception as needed.
            }
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            filePath = uri.getPath();
        }

        return filePath;
    }

    private void resetValues() {
        mPdfOptions = new ImageToPDFOptions();
        mPdfOptions.setBorderWidth(mSharedPreferences.getInt(Constants.DEFAULT_IMAGE_BORDER_TEXT, 0));
        mPdfOptions.setQualityString(Integer.toString(mSharedPreferences.getInt(Constants.DEFAULT_COMPRESSION, 30)));
        mPdfOptions.setPageSize(mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT, Constants.DEFAULT_PAGE_SIZE));
        mPdfOptions.setPasswordProtected(false);
        mPdfOptions.setWatermarkAdded(false);
        mImagesUri.clear();
        showEnhancementOptions();
        mNoOfImages.setVisibility(View.GONE);
        ImageUtils.getInstance().mImageScaleType = mSharedPreferences.getString(Constants.DEFAULT_IMAGE_SCALE_TYPE_TEXT, Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO);
        mPdfOptions.setMargins(0, 0, 0, 0);
        mPageNumStyle = mSharedPreferences.getString(Constants.PREF_PAGE_STYLE, (String) null);
        mPageColor = mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_ITP, -1);
    }

    private void addMargins() {
        String topMargin;
        String leftMargin;
        String option;
        MaterialDialog build = new MaterialDialog.Builder(mActivity).title(R.string.add_margins)
                .customView(R.layout.add_margins_dialog, false).positiveText(R.string.ok)
                .negativeText(R.string.cancel).onPositive(this::marginDialog).build();
        View customView = build.getCustomView();
        EditText edTopMargin = customView.findViewById(R.id.topMarginEditText);
        EditText edBottomMargin = customView.findViewById(R.id.bottomMarginEditText);
        EditText edRightMargin = customView.findViewById(R.id.rightMarginEditText);
        EditText edLeftMargin = customView.findViewById(R.id.leftMarginEditText);
        edTopMargin.setFilters(new InputFilter[]{new InputFilterMinMax("1", "100")});
        edBottomMargin.setFilters(new InputFilter[]{new InputFilterMinMax("1", "100")});
        edRightMargin.setFilters(new InputFilter[]{new InputFilterMinMax("1", "100")});
        edLeftMargin.setFilters(new InputFilter[]{new InputFilterMinMax("1", "100")});
        String space = "";
        if (mPdfOptions.getMarginTop() == 0) {
            topMargin = space;
        } else {
            topMargin = mPdfOptions.getMarginTop() + space;
        }
        edTopMargin.setText(topMargin);
        if (mPdfOptions.getMarginBottom() == 0) {
            leftMargin = space;
        } else {
            leftMargin = mPdfOptions.getMarginBottom() + space;
        }
        edBottomMargin.setText(leftMargin);
        if (mPdfOptions.getMarginRight() == 0) {
            option = space;
        } else {
            option = mPdfOptions.getMarginRight() + space;
        }
        edRightMargin.setText(option);
        if (mPdfOptions.getMarginLeft() != 0) {
            space = mPdfOptions.getMarginLeft() + space;
        }
        edLeftMargin.setText(space);
        build.show();
    }

    public void marginDialog(MaterialDialog materialDialog, DialogAction dialogAction) {
        View customView = materialDialog.getCustomView();
        EditText edtTopMargin = customView.findViewById(R.id.topMarginEditText);
        EditText edBottomMargin = customView.findViewById(R.id.bottomMarginEditText);
        EditText edRightMargin = customView.findViewById(R.id.rightMarginEditText);
        EditText edLeftMargin = customView.findViewById(R.id.leftMarginEditText);
        if (edtTopMargin.getText().toString().isEmpty()) {
            mMarginTop = 0;
        } else {
            mMarginTop = Integer.parseInt(edtTopMargin.getText().toString());
        }
        if (edBottomMargin.getText().toString().isEmpty()) {
            mMarginBottom = 0;
        } else {
            mMarginBottom = Integer.parseInt(edBottomMargin.getText().toString());
        }
        if (edRightMargin.getText().toString().isEmpty()) {
            mMarginRight = 0;
        } else {
            mMarginRight = Integer.parseInt(edRightMargin.getText().toString());
        }
        if (edLeftMargin.getText().toString().isEmpty()) {
            mMarginLeft = 0;
        } else {
            mMarginLeft = Integer.parseInt(edLeftMargin.getText().toString());
        }
        mPdfOptions.setMargins(mMarginTop, mMarginBottom, mMarginRight, mMarginLeft);
    }

    private void addPageNumbers() {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        mPageNumStyle = mSharedPreferences.getString(Constants.PREF_PAGE_STYLE, null);
        mChoseId = mSharedPreferences.getInt(Constants.PREF_PAGE_STYLE_ID, -1);
        RelativeLayout relativeLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.add_pgnum_dialog, null);
        RadioButton rdOp1 = relativeLayout.findViewById(R.id.page_num_opt1);
        RadioButton rdOp2 = relativeLayout.findViewById(R.id.page_num_opt2);
        RadioButton rdOp3 = relativeLayout.findViewById(R.id.page_num_opt3);
        RadioGroup rdOp4 = relativeLayout.findViewById(R.id.radioGroup);
        CheckBox chkDefault = relativeLayout.findViewById(R.id.set_as_default);
        if (mChoseId > 0) {
            chkDefault.setChecked(true);
            rdOp4.clearCheck();
            rdOp4.check(mChoseId);
        }
        new MaterialDialog.Builder(mActivity).title(R.string.choose_page_number_style)
                .customView(relativeLayout, false).positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((materialDialog, dialogAction) -> setPageNumber(rdOp4, rdOp1, rdOp2, rdOp3, chkDefault, edit)).onNeutral((materialDialog, dialogAction) -> mPageNumStyle = null).build().show();
    }

    public void setPageNumber(RadioGroup rdOp4, RadioButton rdOp1, RadioButton rdOp2, RadioButton rdOp3, CheckBox chkDefault, SharedPreferences.Editor editor) {
        mChoseId = rdOp4.getCheckedRadioButtonId();
        if (mChoseId == rdOp1.getId()) {
            mPageNumStyle = Constants.PG_NUM_STYLE_PAGE_X_OF_N;
        } else if (mChoseId == rdOp2.getId()) {
            mPageNumStyle = Constants.PG_NUM_STYLE_X_OF_N;
        } else if (mChoseId == rdOp3.getId()) {
            mPageNumStyle = Constants.PG_NUM_STYLE_X;
        }
        chkDefault.isChecked();
        editor.putString(Constants.PREF_PAGE_STYLE, mPageNumStyle);
        editor.putInt(Constants.PREF_PAGE_STYLE_ID, mChoseId);
        editor.apply();
    }

}

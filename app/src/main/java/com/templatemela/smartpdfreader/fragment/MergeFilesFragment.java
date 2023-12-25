package com.templatemela.smartpdfreader.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.airbnb.lottie.LottieAnimationView;
import com.dd.morphingbutton.MorphingButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.adapter.EnhancementOptionsAdapter;
import com.templatemela.smartpdfreader.adapter.MergeFilesAdapter;
import com.templatemela.smartpdfreader.adapter.MergeSelectedFilesAdapter;
import com.templatemela.smartpdfreader.database.DatabaseHelper;
import com.templatemela.smartpdfreader.interfaces.BottomSheetPopulate;
import com.templatemela.smartpdfreader.interfaces.MergeFilesListener;
import com.templatemela.smartpdfreader.interfaces.OnBackPressedInterface;
import com.templatemela.smartpdfreader.interfaces.OnItemClickListener;
import com.templatemela.smartpdfreader.model.EnhancementOptionsEntity;
import com.templatemela.smartpdfreader.util.BottomSheetCallback;
import com.templatemela.smartpdfreader.util.BottomSheetUtils;
import com.templatemela.smartpdfreader.util.CommonCodeUtils;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.DialogUtils;
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.MergePdf;
import com.templatemela.smartpdfreader.util.MergePdfEnhancementOptionsUtils;
import com.templatemela.smartpdfreader.util.MorphButtonUtility;
import com.templatemela.smartpdfreader.util.RealPathUtil;
import com.templatemela.smartpdfreader.util.StringUtils;
import com.templatemela.smartpdfreader.util.ViewFilesDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MergeFilesFragment extends Fragment implements MergeFilesAdapter.OnClickListener, MergeFilesListener, MergeSelectedFilesAdapter.OnFileItemClickListener, OnItemClickListener, BottomSheetPopulate, OnBackPressedInterface {
    private static final int INTENT_REQUEST_PICK_FILE_CODE = 10;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;


    public Activity mActivity;
    private BottomSheetUtils mBottomSheetUtils;
    private String mCheckbtClickTag = "";
    @BindView(R.id.downArrow)
    ImageView mDownArrow;
    private EnhancementOptionsAdapter mEnhancementOptionsAdapter;
    private ArrayList<EnhancementOptionsEntity> mEnhancementOptionsEntityArrayList;
    @BindView(R.id.enhancement_options_recycle_view)
    RecyclerView mEnhancementOptionsRecycleView;
    private ArrayList<String> mFilePaths;
    private FileUtils mFileUtils;
    private String mHomePath;
    @BindView(R.id.layout)
    RelativeLayout mLayout;
    @BindView(R.id.lottie_progress)
    LottieAnimationView mLottieProgress;
    private MaterialDialog mMaterialDialog;
    private MergeSelectedFilesAdapter mMergeSelectedFilesAdapter;
    private MorphButtonUtility mMorphButtonUtility;
    private String mPassword;
    private boolean mPasswordProtected = false;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    @BindView(R.id.selectFiles)
    Button mSelectFiles;
    @BindView(R.id.selected_files)
    RecyclerView mSelectedFiles;
    private SharedPreferences mSharedPrefs;
    private BottomSheetBehavior mSheetBehavior;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;
    @BindView(R.id.mergebtn)
    MorphingButton mergeBtn;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_merge_files, viewGroup, false);
        ButterKnife.bind(this, inflate);

        showEnhancementOptions();
        mSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        ArrayList<String> arrayList = new ArrayList<>();
        mFilePaths = arrayList;
        mMergeSelectedFilesAdapter = new MergeSelectedFilesAdapter(mActivity, arrayList, this);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mHomePath = mSharedPrefs.getString(Constants.STORAGE_LOCATION, StringUtils.getInstance().getDefaultStorageLocation());
        mLottieProgress.setVisibility(View.VISIBLE);
        mBottomSheetUtils.populateBottomSheetWithPDFs(this);
        mSelectedFiles.setAdapter(mMergeSelectedFilesAdapter);
        mSelectedFiles.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));
        mSheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mUpArrow, isAdded()));
        setMorphingButtonState(false);
        return inflate;
    }

    private void showEnhancementOptions() {
        mEnhancementOptionsRecycleView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mEnhancementOptionsEntityArrayList = MergePdfEnhancementOptionsUtils.getInstance().getEnhancementOptions(mActivity);
        mEnhancementOptionsAdapter = new EnhancementOptionsAdapter(this, mEnhancementOptionsEntityArrayList);
        mEnhancementOptionsRecycleView.setAdapter(mEnhancementOptionsAdapter);
    }

    @Override
    public void onItemClick(int i) {
        if (mFilePaths.size() == 0) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_no_pdfs_selected);
        } else if (i == 0) {
            setPassword();
        }
    }

    private void setPassword() {
        MaterialDialog build = DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity, R.string.set_password).customView(R.layout.custom_dialog, true).neutralText(R.string.remove_dialog).build();
        final MDButton actionButton = build.getActionButton(DialogAction.POSITIVE);
        MDButton actionButton2 = build.getActionButton(DialogAction.NEUTRAL);
        View customView = build.getCustomView();
        Objects.requireNonNull(customView);
        EditText editText = customView.findViewById(R.id.password);
        editText.setText(mPassword);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                actionButton.setEnabled(charSequence.toString().trim().length() > 4);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtils.getInstance().isEmpty(editable)) {
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_password_cannot_be_blank);
                }
            }
        });
        actionButton.setOnClickListener(view -> {
            build.dismiss();
            mPassword = editText.getText().toString().trim();
            if (StringUtils.getInstance().isNotEmpty(mPassword)) {
                mPasswordProtected = true;
                onPasswordStatusChanges(true);
                StringUtils.getInstance().showSnackbar(mActivity, "Password set successfully!");
            }
        });
        actionButton2.setOnClickListener(view -> {
            mPassword = null;
            onPasswordStatusChanges(false);
            mPasswordProtected = false;
            build.dismiss();
            StringUtils.getInstance().showSnackbar(mActivity, R.string.password_remove);
        });
        build.show();
        actionButton.setEnabled(false);
    }

    private void onPasswordStatusChanges(boolean isShow) {
        mEnhancementOptionsEntityArrayList.get(0).setImage(mActivity.getResources().getDrawable(isShow ? R.drawable.baseline_done_24 : R.drawable.baseline_enhanced_encryption_24));
        mEnhancementOptionsAdapter.notifyDataSetChanged();
    }


    @OnClick({R.id.viewFiles})
    public void onViewFilesClick(View view) {
        mBottomSheetUtils.showHideSheet(mSheetBehavior);
    }


    @OnClick({R.id.selectFiles})
    public void startAddingPDF(View view) {
        startActivityForResult(mFileUtils.getFileChooser(), 10);
    }


    @OnClick({R.id.mergebtn})
    public void mergeFiles(View view) {
        new MaterialDialog.Builder(mActivity).title(R.string.creating_pdf).content(R.string.enter_file_name)
                .input(getString(R.string.example), null,
                        (materialDialog, sequence) ->
                                setToMergeFile(mSharedPrefs.getString(Constants.MASTER_PWD_STRING, Constants.appName)
                                        , mFilePaths.toArray(new String[0]), view, sequence)
                ).show();
    }

    public void setToMergeFile(String password, String[] mPaths, View view, CharSequence input) {
        if (StringUtils.getInstance().isEmpty(input)) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
            return;
        }
        if (!mFileUtils.isFileExist(input + getString(R.string.pdf_ext))) {
             MergePdf.mergerPdf(input.toString(), mHomePath, mPasswordProtected, mPassword, this, password,mPaths);
        } else {
            DialogUtils.getInstance().createOverwriteDialog(mActivity)
                    .onPositive((materialDialog, dialogAction) ->  MergePdf.mergerPdf(input.toString(), mHomePath, mPasswordProtected, mPassword, MergeFilesFragment.this, password,mPaths)).onNegative(new MaterialDialog.SingleButtonCallback() {
                public final void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                    mergeFiles(view);
                }
            }).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent != null && resultCode == -1 && intent.getData() != null && requestCode == 10) {
            mFilePaths.add(RealPathUtil.getInstance().getRealPath(getContext(), intent.getData()));
            mMergeSelectedFilesAdapter.notifyDataSetChanged();
            StringUtils.getInstance().showSnackbar(mActivity, getString(R.string.pdf_added_to_list));
            if (mFilePaths.size() > 1 && !mergeBtn.isEnabled()) {
                setMorphingButtonState(true);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (bundle != null) {
            mCheckbtClickTag = bundle.getString("savText");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(getString(R.string.btn_sav_text), mCheckbtClickTag);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mFileUtils = new FileUtils(mActivity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
    }

    @Override
    public void onItemClick(String path) {
        if (mFilePaths.contains(path)) {
            mFilePaths.remove(path);
            StringUtils.getInstance().showSnackbar(mActivity, getString(R.string.pdf_removed_from_list));
        } else {
            mFilePaths.add(path);
            StringUtils.getInstance().showSnackbar(mActivity, getString(R.string.pdf_added_to_list));
        }
        mMergeSelectedFilesAdapter.notifyDataSetChanged();
        if (mFilePaths.size() > 1) {
            if (!mergeBtn.isEnabled()) {
                setMorphingButtonState(true);
            }
        } else if (mergeBtn.isEnabled()) {
            setMorphingButtonState(false);
        }
    }

    @Override
    public void resetValues(boolean isPDFMerged, String path) {
        mMaterialDialog.dismiss();
        if (isPDFMerged) {
            StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.pdf_merged)
                    .setAction(R.string.snackbar_viewAction, view -> mFileUtils.openFile(path, FileUtils.FileType.e_PDF)).show();
            new DatabaseHelper(mActivity).insertRecord(path, mActivity.getString(R.string.created));
        } else {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.file_access_error);
        }
        setMorphingButtonState(false);
        mFilePaths.clear();
        mMergeSelectedFilesAdapter.notifyDataSetChanged();
        mPasswordProtected = false;
        showEnhancementOptions();
    }


    @Override
    public void mergeStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void viewFile(String name) {
        mFileUtils.openFile(name, FileUtils.FileType.e_PDF);
    }

    @Override
    public void removeFile(String name) {
        mFilePaths.remove(name);
        mMergeSelectedFilesAdapter.notifyDataSetChanged();
        StringUtils.getInstance().showSnackbar(mActivity, getString(R.string.pdf_removed_from_list));
        if (mFilePaths.size() < 2 && mergeBtn.isEnabled()) {
            setMorphingButtonState(false);
        }
    }

    @Override
    public void moveUp(int i) {
        Collections.swap(mFilePaths, i, i - 1);
        mMergeSelectedFilesAdapter.notifyDataSetChanged();
    }

    @Override
    public void moveDown(int i) {
        Collections.swap(mFilePaths, i, i + 1);
        mMergeSelectedFilesAdapter.notifyDataSetChanged();
    }

    private void setMorphingButtonState(Boolean enabled) {
        if (enabled.equals(true)) {
            mMorphButtonUtility.morphToSquare(mergeBtn, mMorphButtonUtility.integer());
        } else {
            mMorphButtonUtility.morphToGrey(mergeBtn, mMorphButtonUtility.integer());
        }
        mergeBtn.setEnabled(enabled);
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

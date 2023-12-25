package com.templatemela.smartpdfreader.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.dd.morphingbutton.MorphingButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.adapter.FilesListAdapter;
import com.templatemela.smartpdfreader.adapter.MergeFilesAdapter;
import com.templatemela.smartpdfreader.database.DatabaseHelper;
import com.templatemela.smartpdfreader.interfaces.BottomSheetPopulate;
import com.templatemela.smartpdfreader.interfaces.OnBackPressedInterface;
import com.templatemela.smartpdfreader.interfaces.OnPDFCreatedInterface;
import com.templatemela.smartpdfreader.util.BottomSheetCallback;
import com.templatemela.smartpdfreader.util.BottomSheetUtils;
import com.templatemela.smartpdfreader.util.CommonCodeUtils;
import com.templatemela.smartpdfreader.util.DialogUtils;
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.MorphButtonUtility;
import com.templatemela.smartpdfreader.util.RealPathUtil;
import com.templatemela.smartpdfreader.util.RemoveDuplicates;
import com.templatemela.smartpdfreader.util.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RemoveDuplicatePagesFragment extends Fragment implements MergeFilesAdapter.OnClickListener, FilesListAdapter.OnFileItemClickedListener, BottomSheetPopulate, OnPDFCreatedInterface, OnBackPressedInterface {
    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    private Activity mActivity;
    private BottomSheetUtils mBottomSheetUtils;
    @BindView(R.id.downArrow)
    ImageView mDownArrow;
    private FileUtils mFileUtils;
    @BindView(R.id.layout)
    RelativeLayout mLayout;
    @BindView(R.id.lottie_progress)
    LottieAnimationView mLottieProgress;
    private MaterialDialog mMaterialDialog;
    private MorphButtonUtility mMorphButtonUtility;
    private String mPath;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    BottomSheetBehavior mSheetBehavior;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;
    @BindView(R.id.view_pdf)
    Button mViewPdf;
    @BindView(R.id.remove)
    MorphingButton removeDuplicateButton;
    @BindView(R.id.selectFile)
    MorphingButton selectFileButton;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_remove_duplicate_pages, viewGroup, false);
        ButterKnife.bind(this, inflate);

        mSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mUpArrow, isAdded()));
        mLottieProgress.setVisibility(View.VISIBLE);
        mBottomSheetUtils.populateBottomSheetWithPDFs(this);
        resetValues();
        return inflate;
    }


    @OnClick({R.id.viewFiles})
    public void onViewFilesClick(View view) {
        mBottomSheetUtils.showHideSheet(mSheetBehavior);
    }

    @OnClick({R.id.selectFile})
    public void showFileChooser() {
        mBottomSheetUtils.showHideSheet(mSheetBehavior);
       // startActivityForResult(mFileUtils.getFileChooser(), INTENT_REQUEST_PICKFILE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) throws NullPointerException {
        if (intent != null && resultCode == -1 && intent.getData() != null && requestCode == INTENT_REQUEST_PICKFILE_CODE) {
            setTextAndActivateButtons(RealPathUtil.getInstance().getRealPath(getContext(), intent.getData()));
        }
    }

    @OnClick({R.id.remove})
    public void parse() {
         RemoveDuplicates.removeDuplicate(mPath, this);
    }

    private void resetValues() {
        mPath = null;
        mMorphButtonUtility.initializeButton(selectFileButton, removeDuplicateButton);
    }

    private void setTextAndActivateButtons(String path) {
        mPath = path;
        mMorphButtonUtility.setTextAndActivateButtons(path, selectFileButton, removeDuplicateButton);
    }

    @Override
    public void onPopulate(ArrayList<String> arrayList) {
        CommonCodeUtils.getInstance().populateUtil(mActivity, arrayList, this, mLayout, mLottieProgress, mRecyclerViewFiles);
    }

    @Override
    public void onAttach(Context context) {
        RemoveDuplicatePagesFragment.super.onAttach(context);
        mActivity = (Activity) context;
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
    }

    @Override
    public void onItemClick(String path) {
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setTextAndActivateButtons(path);
    }

    @Override
    public void onFileItemClick(String filter) {
        mFileUtils.openFile(filter, FileUtils.FileType.e_PDF);
    }

    private void viewPdfButton(String path) {
        mViewPdf.setVisibility(View.VISIBLE);
        mViewPdf.setOnClickListener(view -> mFileUtils.openFile(path, FileUtils.FileType.e_PDF));
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
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_no_duplicate_pdf);
            mViewPdf.setVisibility(View.GONE);
            return;
        }
        new DatabaseHelper(mActivity).insertRecord(path, mActivity.getString(R.string.created));
        StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_duplicate_removed)
                .setAction(R.string.snackbar_viewAction, view -> mFileUtils.openFile(path, FileUtils.FileType.e_PDF)).show();
        viewPdfButton(path);
        resetValues();
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

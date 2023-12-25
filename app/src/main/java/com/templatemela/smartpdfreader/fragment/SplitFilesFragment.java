package com.templatemela.smartpdfreader.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.dd.morphingbutton.MorphingButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.itextpdf.text.pdf.PdfReader;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.adapter.FilesListAdapter;
import com.templatemela.smartpdfreader.adapter.MergeFilesAdapter;
import com.templatemela.smartpdfreader.interfaces.BottomSheetPopulate;
import com.templatemela.smartpdfreader.interfaces.OnBackPressedInterface;
import com.templatemela.smartpdfreader.util.BottomSheetCallback;
import com.templatemela.smartpdfreader.util.BottomSheetUtils;
import com.templatemela.smartpdfreader.util.CommonCodeUtils;
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.MorphButtonUtility;
import com.templatemela.smartpdfreader.util.RealPathUtil;
import com.templatemela.smartpdfreader.util.SplitPDFUtils;
import com.templatemela.smartpdfreader.util.StringUtils;
import com.templatemela.smartpdfreader.util.ViewFilesDividerItemDecoration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplitFilesFragment extends Fragment implements MergeFilesAdapter.OnClickListener, FilesListAdapter.OnFileItemClickedListener, BottomSheetPopulate, OnBackPressedInterface {
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
    private MorphButtonUtility mMorphButtonUtility;
    private String mPath;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    private BottomSheetBehavior mSheetBehavior;
    @BindView(R.id.split_config)
    EditText mSplitConfitEditText;
    private SplitPDFUtils mSplitPDFUtils;
    @BindView(R.id.splitted_files)
    RecyclerView mSplittedFiles;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;
    @BindView(R.id.selectFile)
    MorphingButton selectFileButton;
    @BindView(R.id.splitFiles)
    MorphingButton splitFilesButton;
    @BindView(R.id.splitfiles_text)
    TextView splitFilesSuccessText;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_split_files, viewGroup, false);
        ButterKnife.bind(this, inflate);
//        ADHelper.showAdMobBanner(getActivity(), (LinearLayout) inflate.findViewById(R.id.adViewBanner));
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
        //startActivityForResult(mFileUtils.getFileChooser(), INTENT_REQUEST_PICKFILE_CODE);
        mBottomSheetUtils.showHideSheet(mSheetBehavior);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) throws NullPointerException {
        if (intent != null && resultCode == -1 && intent.getData() != null && requestCode == INTENT_REQUEST_PICKFILE_CODE) {
            setTextAndActivateButtons(RealPathUtil.getInstance().getRealPath(getContext(), intent.getData()));
        }
    }

    private void showGoogleInterstitialAds() {
        StringUtils.getInstance().hideKeyboard(mActivity);
        ArrayList<String> splitPDFByConfig = mSplitPDFUtils.splitPDFByConfig(mPath, mSplitConfitEditText.getText().toString());
        int size = splitPDFByConfig.size();
        if (size != 0) {
            String format = String.format(mActivity.getString(R.string.split_success), size);
            StringUtils.getInstance().showSnackbar(mActivity, format);
            splitFilesSuccessText.setVisibility(View.VISIBLE);
            splitFilesSuccessText.setText(format);
            FilesListAdapter filesListAdapter = new FilesListAdapter(mActivity, splitPDFByConfig, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mSplittedFiles.setVisibility(View.VISIBLE);
            mSplittedFiles.setLayoutManager(linearLayoutManager);
            mSplittedFiles.setAdapter(filesListAdapter);
            mSplittedFiles.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));
            resetValues();
        }
    }

    @OnClick({R.id.splitFiles})
    public void parse() {
        showGoogleInterstitialAds();
    }

    private void resetValues() {
        mPath = null;
        mMorphButtonUtility.initializeButton(selectFileButton, splitFilesButton);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mSplitPDFUtils = new SplitPDFUtils(mActivity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
    }

    @Override
    public void onItemClick(String path) {
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setTextAndActivateButtons(path);
    }

    private void setTextAndActivateButtons(String path) {
        mSplittedFiles.setVisibility(View.GONE);
        splitFilesSuccessText.setVisibility(View.GONE);
        mPath = path;
        try {
            if (!new PdfReader(path).isEncrypted()){
                String defaultSplitConfig = getDefaultSplitConfig(path);
                if (defaultSplitConfig != null) {
                    mMorphButtonUtility.setTextAndActivateButtons(path, selectFileButton, splitFilesButton);
                    mSplitConfitEditText.setVisibility(View.VISIBLE);
                    mSplitConfitEditText.setText(defaultSplitConfig);
                    return;
                }
            }
            else
            {
                StringUtils.getInstance().showSnackbar(getActivity(), R.string.encrypted_pdf);
            }
        } catch (IOException e) {
            e.printStackTrace();
            StringUtils.getInstance().showSnackbar(getActivity(), R.string.encrypted_pdf);
        }

        resetValues();
    }

    private String getDefaultSplitConfig(String path) {
        ParcelFileDescriptor parcelFileDescriptor;
        StringBuilder sb = new StringBuilder();
        if (path != null) {
            try {
                parcelFileDescriptor = ParcelFileDescriptor.open(new File(path), 268435456);
            } catch (Exception e) {
                e.printStackTrace();
                StringUtils.getInstance().showSnackbar(mActivity, R.string.encrypted_pdf);
                return null;
            }
        } else {
            parcelFileDescriptor = null;
        }
        if (parcelFileDescriptor != null) {
            int pageCount = 0;
            try {
                pageCount = new PdfRenderer(parcelFileDescriptor).getPageCount();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            for (int i = 1; i <= pageCount; i++) {
                sb.append(i);
                sb.append(",");
            }
        }
        return sb.toString();
    }

    @Override
    public void onFileItemClick(String filter) {
        mFileUtils.openFile(filter, FileUtils.FileType.e_PDF);
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

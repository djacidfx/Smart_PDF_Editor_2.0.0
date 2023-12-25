package com.templatemela.smartpdfreader.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.adapter.ViewFilesAdapter;
import com.templatemela.smartpdfreader.database.DatabaseHelper;
import com.templatemela.smartpdfreader.interfaces.MergeFilesListener;

public class MergeHelper implements MergeFilesListener {
    private final Activity mActivity;
    private final Context mContext;
    private final FileUtils mFileUtils;
    private final String mHomePath;
    private MaterialDialog mMaterialDialog;
    private String mPassword;
    private final boolean mPasswordProtected = false;
    private final SharedPreferences mSharedPrefs;
    private final ViewFilesAdapter mViewFilesAdapter;

    public MergeHelper(Activity activity, ViewFilesAdapter viewFilesAdapter) {
        this.mActivity = activity;
        this.mFileUtils = new FileUtils(activity);
        this.mHomePath = PreferenceManager.getDefaultSharedPreferences(activity).getString(Constants.STORAGE_LOCATION, StringUtils.getInstance().getDefaultStorageLocation());
        this.mContext = activity;
        this.mViewFilesAdapter = viewFilesAdapter;
        this.mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public void mergeFiles() {
        new MaterialDialog.Builder(mActivity).title(R.string.creating_pdf)
                .content(R.string.enter_file_name).input(this.mContext.getResources().getString(R.string.example), null,
                (materialDialog, charSequence) -> setMerge(mSharedPrefs.getString(Constants.MASTER_PWD_STRING, Constants.appName), (Object[]) mViewFilesAdapter.getSelectedFilePath().toArray(), charSequence)).show();
    }

    public void setMerge(String password, Object[] files, CharSequence input) {
        if (StringUtils.getInstance().isEmpty(input)) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
            return;
        }
        if (!mFileUtils.isFileExist(input + this.mContext.getResources().getString(R.string.pdf_ext))) {
             MergePdf.mergerPdf(input.toString(), mHomePath, false, mPassword, this, password, String.valueOf(files));
        } else {
            DialogUtils.getInstance().createOverwriteDialog(mActivity).
                    onPositive((materialDialog, dialogAction) ->  MergePdf.mergerPdf(input.toString(), mHomePath, false, mPassword, MergeHelper.this, password, String.valueOf(files))).onNegative((materialDialog, dialogAction) -> mergeFiles()).show();
        }
    }


    public void resetValues(boolean isPDFMerged, String path) {
        this.mMaterialDialog.dismiss();
        if (isPDFMerged) {
            StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.pdf_merged)
                    .setAction(R.string.snackbar_viewAction, view -> mFileUtils.openFile(path, FileUtils.FileType.e_PDF)).show();
            new DatabaseHelper(mActivity).insertRecord(path, this.mActivity.getString(R.string.created));
        } else {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.file_access_error);
        }
        mViewFilesAdapter.updateDataset();
    }


    public void mergeStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }
}

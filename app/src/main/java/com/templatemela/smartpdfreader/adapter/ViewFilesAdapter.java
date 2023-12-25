package com.templatemela.smartpdfreader.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import com.artifex.mupdf.viewer.DocumentActivity;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.material.snackbar.Snackbar;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.database.DatabaseHelper;
import com.templatemela.smartpdfreader.interfaces.DataSetChanged;
import com.templatemela.smartpdfreader.interfaces.EmptyStateChangeListener;
import com.templatemela.smartpdfreader.interfaces.ItemSelectedListener;
import com.templatemela.smartpdfreader.model.PDFFile;

import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.DialogUtils;
import com.templatemela.smartpdfreader.util.DirectoryUtils;
import com.templatemela.smartpdfreader.util.FileSortUtils;
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.PDFEncryptionUtility;
import com.templatemela.smartpdfreader.util.PDFRotationUtils;
import com.templatemela.smartpdfreader.util.PDFUtils;
import com.templatemela.smartpdfreader.util.PopulateListUtil;
import com.templatemela.smartpdfreader.util.StringUtils;
import com.templatemela.smartpdfreader.util.WatermarkUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewFilesAdapter extends RecyclerView.Adapter<ViewFilesAdapter.ViewFilesHolder> implements DataSetChanged, EmptyStateChangeListener {

    public final Activity mActivity;

    public final DatabaseHelper mDatabaseHelper;
    private final EmptyStateChangeListener mEmptyStateChangeListener;
    private List<PDFFile> mFileList;
    private final FileUtils mFileUtils;
    private final ItemSelectedListener mItemSelectedListener;
    private final PDFEncryptionUtility mPDFEncryptionUtils;
    private final PDFRotationUtils mPDFRotationUtils;
    private final PDFUtils mPDFUtils;

    public final ArrayList<Integer> mSelectedFiles = new ArrayList<>();
    private final SharedPreferences mSharedPreferences;
    private final WatermarkUtils mWatermarkUtils;

    public void filesPopulated() {
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void hideNoPermissionsView() {
    }

    @Override
    public void setEmptyStateInvisible() {
    }

    @Override
    public void setEmptyStateVisible() {
    }

    @Override
    public void showNoPermissionsView() {
    }


    public ViewFilesAdapter(Activity activity, List<PDFFile> list, EmptyStateChangeListener emptyStateChangeListener, ItemSelectedListener itemSelectedListener) {
        this.mActivity = activity;
        this.mEmptyStateChangeListener = emptyStateChangeListener;
        this.mItemSelectedListener = itemSelectedListener;
        this.mFileList = list;
        this.mFileUtils = new FileUtils(activity);
        this.mPDFUtils = new PDFUtils(activity);
        this.mPDFRotationUtils = new PDFRotationUtils(activity);
        this.mPDFEncryptionUtils = new PDFEncryptionUtility(activity);
        this.mWatermarkUtils = new WatermarkUtils(activity);
        this.mDatabaseHelper = new DatabaseHelper(activity);
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @NonNull
    @Override
    public ViewFilesHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ViewFilesHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_file, viewGroup, false), this.mItemSelectedListener);
    }

    @Override
    public void onBindViewHolder(ViewFilesHolder viewFilesHolder, int position) {
        int adapterPosition = viewFilesHolder.getAdapterPosition();
        PDFFile pDFFile = mFileList.get(adapterPosition);
        viewFilesHolder.fileName.setText(pDFFile.getPdfFile().getName());
        viewFilesHolder.fileSize.setText(FileUtils.getFormattedSize(pDFFile.getPdfFile()));
        viewFilesHolder.fileDate.setText(FileUtils.getFormattedDate(pDFFile.getPdfFile()));
        viewFilesHolder.checkBox.setChecked(mSelectedFiles.contains(adapterPosition));
        viewFilesHolder.encryptionImage.setVisibility(pDFFile.isEncrypted() ? View.VISIBLE : View.GONE);
        viewFilesHolder.ripple.setOnClickListener(view -> {
            new MaterialDialog.Builder(mActivity).title(R.string.title).items(R.array.items).itemsIds(R.array.itemIds)
                    .itemsCallback((materialDialog, view2, i, charSequence) -> {
                        performOperation(i, position, pDFFile.getPdfFile(),pDFFile.getPdfFile().getName());
                    }).show();
            notifyDataSetChanged();
        });
    }

    private void performOperation(int i, int pos, File file,String name) {
        switch (i) {
            case 0:
                mFileUtils.openFile(file.getPath(), FileUtils.FileType.e_PDF);
                return;
            case 1:
                deleteFile(pos);
                return;
            case 2:
                onRenameFileClick(pos);
                return;
            case 3:
                mFileUtils.printFile(file);
                return;
            case 4:
                mFileUtils.shareFile(file);
                return;
            case 5:
                mPDFUtils.showDetails(file);
                return;
            case 6:
                mPDFEncryptionUtils.setPassword(file.getPath(), this);
                return;
            case 7:
                mPDFEncryptionUtils.removePassword(file.getPath(), mFileUtils.getUniqueFileName(file.getPath().replace(this.mActivity.getResources().getString(R.string.pdf_ext), this.mActivity.getString(R.string.decrypted_file))), this);
                return;
            case 8:
                mPDFRotationUtils.rotatePages(file.getPath(), this);
                return;
            case 9:
                mWatermarkUtils.setWatermark(file.getPath(), this);
                return;
            default:
                return;
        }
    }

    public void checkAll() {
        mSelectedFiles.clear();
        for (int i = 0; i < mFileList.size(); i++) {
            mSelectedFiles.add(i);
        }
        notifyDataSetChanged();
    }

    public void unCheckAll() {
        mSelectedFiles.clear();
        notifyDataSetChanged();
        updateActionBarTitle();
    }

    private void updateActionBarTitle() {
        mActivity.setTitle(R.string.app_name);
    }

    public ArrayList<String> getSelectedFilePath() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (Integer mSelectedFile : mSelectedFiles) {
            int intValue = mSelectedFile;
            if (mFileList.size() > intValue) {
                arrayList.add(mFileList.get(intValue).getPdfFile().getPath());
            }
        }
        return arrayList;
    }

    @Override
    public int getItemCount() {
        if (mFileList == null) {
            return 0;
        }
        return mFileList.size();
    }

    public void setData(List<PDFFile> list) {
        mFileList = list;
        notifyDataSetChanged();
    }

    public PDFUtils getPDFUtils() {
        return mPDFUtils;
    }

    public boolean areItemsSelected() {
        return mSelectedFiles.size() > 0;
    }

    public void deleteFile() {
        deleteFiles(mSelectedFiles);
    }

    private void deleteFile(int i) {
        if (i >= 0 && i < mFileList.size()) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(Integer.valueOf(i));
            deleteFiles(arrayList);
        }
    }

    private void deleteFiles(ArrayList<Integer> arrayList) {
        new AlertDialog.Builder(this.mActivity).setCancelable(true).
                setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .setTitle(arrayList.size() > 1 ? R.string.delete_alert_selected : R.string.delete_alert_singular)
                .setPositiveButton(R.string.yes, (dialogInterface, i) ->
                        confirmDelete(arrayList, R.string.snackbar_files_deleted)
                ).create().show();
    }

    public void confirmDelete(ArrayList arrayList, int i) {
        final ArrayList arrayList2 = new ArrayList();
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            arrayList2.add(mFileList.get(size).getPdfFile().getPath());
            mFileList.remove(size);
        }
        mSelectedFiles.clear();
        arrayList.clear();
        updateActionBarTitle();
        notifyDataSetChanged();
        if (mFileList.size() == 0) {
            mEmptyStateChangeListener.setEmptyStateVisible();
        }
        final AtomicInteger atomicInteger = new AtomicInteger();
        StringUtils.getInstance().getSnackbarwithAction(mActivity, i)
                .setAction(R.string.snackbar_undoAction, view -> {
                    if (mFileList.size() == 0) {
                        mEmptyStateChangeListener.setEmptyStateInvisible();
                    }
                    updateDataset();
                    atomicInteger.set(1);
                }).addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int i) {
                if (atomicInteger.get() == 0) {
                    for (Object o : arrayList2) {
                        File file = new File((String) o);
                        mDatabaseHelper.insertRecord(file.getAbsolutePath(), mActivity.getString(R.string.deleted));
                        if (file.exists() && !file.delete()) {
                            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_file_not_deleted);
                        }
                    }
                }
            }
        }).show();
    }


    public void shareFiles() {
        ArrayList arrayList = new ArrayList();
        Iterator<Integer> it2 = mSelectedFiles.iterator();
        while (it2.hasNext()) {
            int intValue = it2.next().intValue();
            if (mFileList.size() > intValue) {
                arrayList.add(mFileList.get(intValue).getPdfFile());
            }
        }
        mFileUtils.shareMultipleFiles(arrayList);
    }


    private void onRenameFileClick(int i) {
        new MaterialDialog.Builder(mActivity).title(R.string.creating_pdf).content(R.string.enter_file_name)
                .input(mActivity.getString(R.string.example), null, (materialDialog, sequence) -> confirmFileRename(i, sequence)).show();
    }

    public void confirmFileRename(int i, CharSequence name) {
        if (name == null || name.toString().trim().isEmpty()) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
            return;
        }
        if (!mFileUtils.isFileExist(name + this.mActivity.getString(R.string.pdf_ext))) {
            renameFile(i, name.toString());
        } else {
            DialogUtils.getInstance().createOverwriteDialog(mActivity)
                    .onPositive((materialDialog1, dialogAction) ->
                            renameFile(i, name.toString())
                    )
                    .onNegative((materialDialog12, dialogAction) -> onRenameFileClick(i)).show();
        }
    }


    private void renameFile(int i, String name) {
        PDFFile pDFFile = mFileList.get(i);
        File pdfFile = pDFFile.getPdfFile();
        String path = pdfFile.getPath();
        String fPath = path.substring(0, path.lastIndexOf(47)) + Constants.PATH_SEPERATOR + name + this.mActivity.getString(R.string.pdf_ext);
        File file = new File(fPath);
        if (pdfFile.renameTo(file)) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_file_renamed);
            pDFFile.setPdfFile(file);
            notifyDataSetChanged();
            mDatabaseHelper.insertRecord(fPath, this.mActivity.getString(R.string.renamed));
            return;
        }
        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_file_not_renamed);
    }

    public void updateDataset() {
        Objects.requireNonNull(FileSortUtils.getInstance());
        PopulateListUtil.PopulateList(this, this, new DirectoryUtils(mActivity), mSharedPreferences.getInt(Constants.SORTING_INDEX, 0), null);
    }

    class ViewFilesHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.checkbox)
        CheckBox checkBox;
        @BindView(R.id.encryptionImage)
        ImageView encryptionImage;
        @BindView(R.id.fileDate)
        TextView fileDate;
        @BindView(R.id.fileName)
        TextView fileName;
        @BindView(R.id.fileSize)
        TextView fileSize;
        @BindView(R.id.fileRipple)
        MaterialRippleLayout ripple;

        ViewFilesHolder(View view, ItemSelectedListener itemSelectedListener) {
            super(view);
            ButterKnife.bind(this, view);
            checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                if (!b) {
                    mSelectedFiles.remove(Integer.valueOf(getAdapterPosition()));
                } else if (!mSelectedFiles.contains(getAdapterPosition())) {
                    mSelectedFiles.add(getAdapterPosition());
                    itemSelectedListener.isSelected(true, mSelectedFiles.size());
                }
                itemSelectedListener.isSelected(false, mSelectedFiles.size());
            });
        }
    }
}

package com.templatemela.smartpdfreader.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.PDFEncryptionUtility;
import com.templatemela.smartpdfreader.util.PDFUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MergeFilesAdapter extends RecyclerView.Adapter<MergeFilesAdapter.ViewMergeFilesHolder> {
    private final Activity mContext;

    public final ArrayList<String> mFilePaths;

    public final boolean mIsMergeFragment;

    public final OnClickListener mOnClickListener;

    boolean isRemovePwd;
    private final PDFUtils mPDFUtils;
    PDFEncryptionUtility pdfEncryptionUtility;

    public interface OnClickListener {
        void onItemClick(String path);
    }


    public MergeFilesAdapter(Activity activity, ArrayList<String> arrayList, boolean mIsMergeFragment, OnClickListener onClickListener, boolean isRemovePwd) {
        this.mContext = activity;
        this.mFilePaths = arrayList;
        this.mOnClickListener = onClickListener;
        this.mPDFUtils = new PDFUtils(activity);
        this.mIsMergeFragment = mIsMergeFragment;
        pdfEncryptionUtility=new PDFEncryptionUtility(activity);
        this.isRemovePwd = isRemovePwd;

    }

    @NonNull
    @Override
    public ViewMergeFilesHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ViewMergeFilesHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_merge_files, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewMergeFilesHolder viewMergeFilesHolder, int position) {
        boolean isPDFEncrypted = false;
        if (isRemovePwd) {
         isPDFEncrypted=   pdfEncryptionUtility.isPDFEncrypted(mFilePaths.get(position));

        } else {
            isPDFEncrypted= mPDFUtils.isPDFEncrypted(mFilePaths.get(position));
        }
        Log.e("encrypted", String.valueOf(isPDFEncrypted));
        viewMergeFilesHolder.mFileName.setText(FileUtils.getFileName(mFilePaths.get(position)));
        viewMergeFilesHolder.mEncryptionImage.setVisibility(isPDFEncrypted ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        if (mFilePaths == null) {
            return 0;
        }
        return mFilePaths.size();
    }

    public class ViewMergeFilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.itemMerge_checkbox)
        AppCompatCheckBox mCheckbox;
        @BindView(R.id.encryptionImage)
        ImageView mEncryptionImage;
        @BindView(R.id.fileName)
        TextView mFileName;

        ViewMergeFilesHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mFileName.setOnClickListener(this);
            if (mIsMergeFragment) {
                mCheckbox.setVisibility(View.VISIBLE);
            } else {
                mCheckbox.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() < mFilePaths.size()) {
                if (mIsMergeFragment) {
                    mCheckbox.toggle();
                }
                mOnClickListener.onItemClick(mFilePaths.get(getAdapterPosition()));
            }
        }

        @OnClick({R.id.itemMerge_checkbox})
        public void onCheckboxClick() {
            mOnClickListener.onItemClick(mFilePaths.get(getAdapterPosition()));
        }
    }
}

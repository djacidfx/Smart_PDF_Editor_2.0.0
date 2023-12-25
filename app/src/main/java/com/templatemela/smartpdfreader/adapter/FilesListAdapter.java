package com.templatemela.smartpdfreader.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.util.FileUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilesListAdapter extends RecyclerView.Adapter<FilesListAdapter.ViewMergeFilesHolder> {
    private final Activity mContext;

    public final ArrayList<String> mFilePaths;

    public final OnFileItemClickedListener mOnClickListener;

    public interface OnFileItemClickedListener {
        void onFileItemClick(String filter);
    }


    public FilesListAdapter(Activity activity, ArrayList<String> arrayList, OnFileItemClickedListener onFileItemClickedListener) {
        this.mContext = activity;
        this.mFilePaths = arrayList;
        this.mOnClickListener = onFileItemClickedListener;
    }

    @NonNull
    @Override
    public ViewMergeFilesHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ViewMergeFilesHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_merge_files, viewGroup, false));
    }
    @Override
    public void onBindViewHolder(ViewMergeFilesHolder viewMergeFilesHolder, int position) {
        viewMergeFilesHolder.mFileName.setText(FileUtils.getFileName(mFilePaths.get(position)));
        viewMergeFilesHolder.mEncryptionImage.setVisibility(View.GONE);
    }
    @Override
    public int getItemCount() {
        if (mFilePaths == null) {
            return 0;
        }
        return mFilePaths.size();
    }

    public class ViewMergeFilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.encryptionImage)
        ImageView mEncryptionImage;
        @BindView(R.id.fileName)
        TextView mFileName;

        ViewMergeFilesHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mFileName.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            mOnClickListener.onFileItemClick(mFilePaths.get(getAdapterPosition()));
        }
    }
}

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

public class MergeSelectedFilesAdapter extends RecyclerView.Adapter<MergeSelectedFilesAdapter.MergeSelectedFilesHolder> {
    private final Activity mContext;

    public final ArrayList<String> mFilePaths;

    public final OnFileItemClickListener mOnClickListener;

    public interface OnFileItemClickListener {
        void moveDown(int i);

        void moveUp(int i);

        void removeFile(String name);

        void viewFile(String name);
    }


    public MergeSelectedFilesAdapter(Activity activity, ArrayList<String> arrayList, OnFileItemClickListener onFileItemClickListener) {
        this.mContext = activity;
        this.mFilePaths = arrayList;
        this.mOnClickListener = onFileItemClickListener;
    }

    @NonNull
    @Override
    public MergeSelectedFilesHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new MergeSelectedFilesHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_merge_selected_files, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(MergeSelectedFilesHolder mergeSelectedFilesHolder, int position) {
        mergeSelectedFilesHolder.mFileName.setText(FileUtils.getFileName(mFilePaths.get(position)));
    }

    @Override
    public int getItemCount() {
        if (mFilePaths == null) {
            return 0;
        }
        return mFilePaths.size();
    }

    public class MergeSelectedFilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.down_file)
        ImageView mDown;
        @BindView(R.id.fileName)
        TextView mFileName;
        @BindView(R.id.remove)
        ImageView mRemove;
        @BindView(R.id.up_file)
        ImageView mUp;
        @BindView(R.id.view_file)
        ImageView mViewFile;

        MergeSelectedFilesHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mViewFile.setOnClickListener(this);
            mRemove.setOnClickListener(this);
            mUp.setOnClickListener(this);
            mDown.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.view_file) {
                mOnClickListener.viewFile(mFilePaths.get(getAdapterPosition()));
            } else if (view.getId() == R.id.up_file) {
                if (getAdapterPosition() != 0) {
                    mOnClickListener.moveUp(getAdapterPosition());
                }
            } else if (view.getId() != R.id.down_file) {
                mOnClickListener.removeFile(mFilePaths.get(getAdapterPosition()));
            } else if (mFilePaths.size() != getAdapterPosition() + 1) {
                mOnClickListener.moveDown(getAdapterPosition());
            }
        }
    }
}

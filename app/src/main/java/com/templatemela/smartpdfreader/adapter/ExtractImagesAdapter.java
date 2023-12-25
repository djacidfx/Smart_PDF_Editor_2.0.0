package com.templatemela.smartpdfreader.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.ImageUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExtractImagesAdapter extends RecyclerView.Adapter<ExtractImagesAdapter.ViewMergeFilesHolder> {
    private final Activity mContext;

    public final ArrayList<String> mFilePaths;

    public final OnFileItemClickedListener mOnClickListener;

    public interface OnFileItemClickedListener {
        void onFileItemClick(String path);
    }


    public ExtractImagesAdapter(Activity activity, ArrayList<String> arrayList, OnFileItemClickedListener onFileItemClickedListener) {
        this.mContext = activity;
        this.mFilePaths = arrayList;
        this.mOnClickListener = onFileItemClickedListener;
    }

    @NonNull
    @Override
    public ViewMergeFilesHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ViewMergeFilesHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image_extracted, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewMergeFilesHolder viewMergeFilesHolder, int position) {
        viewMergeFilesHolder.mFileName.setText(FileUtils.getFileName(mFilePaths.get(position)));
        Bitmap roundBitmapFromPath = ImageUtils.getInstance().getRoundBitmapFromPath(mFilePaths.get(position));
        if (roundBitmapFromPath != null) {
            viewMergeFilesHolder.mImagePreview.setImageBitmap(roundBitmapFromPath);
        }
    }

    @Override
    public int getItemCount() {
        if (mFilePaths == null) {
            return 0;
        }
        return mFilePaths.size();
    }

    public class ViewMergeFilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.fileName)
        TextView mFileName;
        @BindView(R.id.imagePreview)
        ImageView mImagePreview;

        ViewMergeFilesHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mFileName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() < mFilePaths.size()) {
                mOnClickListener.onFileItemClick(mFilePaths.get(getAdapterPosition()));
            }
        }
    }
}

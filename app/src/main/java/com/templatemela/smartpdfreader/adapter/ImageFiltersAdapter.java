package com.templatemela.smartpdfreader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.interfaces.OnFilterItemClickedListener;
import com.templatemela.smartpdfreader.model.FilterItem;
import com.templatemela.smartpdfreader.util.ImageUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageFiltersAdapter extends RecyclerView.Adapter<ImageFiltersAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<FilterItem> mFilterItem;

    public final OnFilterItemClickedListener mOnFilterItemClickedListener;

    public ImageFiltersAdapter(ArrayList<FilterItem> arrayList, Context context, OnFilterItemClickedListener onFilterItemClickedListener) {
        this.mFilterItem = arrayList;
        this.mContext = context;
        this.mOnFilterItemClickedListener = onFilterItemClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_filter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        int imageId = mFilterItem.get(position).getImageId();
        Bitmap decodeResource = BitmapFactory.decodeResource(mContext.getResources(), imageId);
        if (decodeResource != null) {
            viewHolder.img.setImageBitmap(ImageUtils.getInstance().getRoundBitmap(decodeResource));
        } else {
            viewHolder.img.setImageResource(imageId);
        }
        viewHolder.name.setText(mFilterItem.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mFilterItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.filter_preview)
        ImageView img;
        @BindView(R.id.filter_Name)
        TextView name;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnFilterItemClickedListener.onItemClick(view, getAdapterPosition());
        }
    }
}

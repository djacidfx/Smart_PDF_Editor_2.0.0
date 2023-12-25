package com.templatemela.smartpdfreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.model.PreviewImageOptionItem;

import java.util.ArrayList;

public class PreviewImageOptionsAdapter extends RecyclerView.Adapter<PreviewImageOptionsAdapter.ViewHolder> {
    private final Context mContext;

    public final OnItemClickListener mOnItemClickListener;
    private final ArrayList<PreviewImageOptionItem> mOptions;

    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    public PreviewImageOptionsAdapter(OnItemClickListener onItemClickListener, ArrayList<PreviewImageOptionItem> arrayList, Context context) {
        this.mOnItemClickListener = onItemClickListener;
        this.mOptions = arrayList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_preview_image_options, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.imageView.setImageDrawable(mContext.getDrawable(mOptions.get(position).getOptionImageId()));
        viewHolder.textView.setText(mOptions.get(position).getOptionName());
    }

    @Override
    public int getItemCount() {
        return mOptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imageView;
        final TextView textView;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.option_image);
            textView = view.findViewById(R.id.option_name);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}

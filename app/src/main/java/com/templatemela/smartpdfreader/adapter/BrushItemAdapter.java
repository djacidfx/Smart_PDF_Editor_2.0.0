package com.templatemela.smartpdfreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.interfaces.OnItemClickListener;
import com.templatemela.smartpdfreader.model.BrushItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrushItemAdapter extends RecyclerView.Adapter<BrushItemAdapter.BrushItemViewHolder> {
    private final List<BrushItem> mBrushItems;
    private final Context mContext;

    public final OnItemClickListener mOnItemClickListener;


    public BrushItemAdapter(Context context, OnItemClickListener onItemClickListener, List<BrushItem> list) {
        this.mBrushItems = list;
        this.mOnItemClickListener = onItemClickListener;
        this.mContext = context;
    }

    @NonNull
    @Override
    public BrushItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new BrushItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.brush_color_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BrushItemViewHolder brushItemViewHolder, int i) {
        int color = mBrushItems.get(i).getColor();
        if (i == mBrushItems.size() - 1) {
            brushItemViewHolder.doodleButton.setBackgroundResource(R.drawable.color_palette);
        } else {
            brushItemViewHolder.doodleButton.setBackgroundColor(mContext.getResources().getColor(color));
        }
    }

    @Override
    public int getItemCount() {
        return mBrushItems.size();
    }

    public class BrushItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.doodle_color)
        ImageView doodleButton;

        BrushItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}

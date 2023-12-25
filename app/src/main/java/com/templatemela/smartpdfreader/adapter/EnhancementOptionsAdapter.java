package com.templatemela.smartpdfreader.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.interfaces.OnItemClickListener;
import com.templatemela.smartpdfreader.model.EnhancementOptionsEntity;
import java.util.List;

public class EnhancementOptionsAdapter extends RecyclerView.Adapter<EnhancementOptionsAdapter.EnhancementOptionsViewHolder> {
    private final List<EnhancementOptionsEntity> mEnhancementOptionsEntityList;

    public final OnItemClickListener mOnItemClickListener;

    public EnhancementOptionsAdapter(OnItemClickListener onItemClickListener, List<EnhancementOptionsEntity> list) {
        this.mOnItemClickListener = onItemClickListener;
        this.mEnhancementOptionsEntityList = list;
    }

    @NonNull
    @Override
    public EnhancementOptionsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new EnhancementOptionsViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_view_enhancement_option, viewGroup, false));
    }
    @Override
    public void onBindViewHolder(EnhancementOptionsViewHolder enhancementOptionsViewHolder, int position) {
        enhancementOptionsViewHolder.optionImage.setImageDrawable(mEnhancementOptionsEntityList.get(position).getImage());
        enhancementOptionsViewHolder.optionName.setText(mEnhancementOptionsEntityList.get(position).getName());
    }
    @Override
    public int getItemCount() {
        return mEnhancementOptionsEntityList.size();
    }

    public class EnhancementOptionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.option_image)
        ImageView optionImage;
        @BindView(R.id.option_name)
        TextView optionName;

        EnhancementOptionsViewHolder(View view) {
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

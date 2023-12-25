package com.templatemela.smartpdfreader.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.templatemela.smartpdfreader.R;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecentListAdapter extends RecyclerView.Adapter<RecentListAdapter.RecentItemViewHolder> {
    private List<String> mKeys;
    private final View.OnClickListener mOnClickListener;
    private List<Map<String, String>> mValues;

    public RecentListAdapter(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void updateList(List<String> mKeys, List<Map<String, String>> mValues) {
        this.mKeys = mKeys;
        this.mValues = mValues;
    }

    @NonNull
    @Override
    public RecentItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new RecentItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recent_used, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(RecentItemViewHolder recentItemViewHolder, int position) {
        recentItemViewHolder.name_.setText(recentItemViewHolder.itemView.getContext().getString(Integer.parseInt(mValues.get(position).keySet().iterator().next())));
        recentItemViewHolder.icon_.setImageDrawable(recentItemViewHolder.itemView.getContext().getDrawable(Integer.parseInt(mValues.get(position).values().iterator().next())));
        recentItemViewHolder.itemView.setId(Integer.parseInt(mKeys.get(position)));
        recentItemViewHolder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class RecentItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.option_image_)
        ImageView icon_;
        @BindView(R.id.option_name_)
        TextView name_;

        private RecentItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

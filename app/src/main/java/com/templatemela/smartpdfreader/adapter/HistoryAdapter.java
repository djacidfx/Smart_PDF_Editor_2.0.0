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
import com.templatemela.smartpdfreader.database.History;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHistoryHolder> {

    public final List<History> mHistoryList;
    private final HashMap<String, Integer> mIconsOperationList;

    public final OnClickListener mOnClickListener;

    public interface OnClickListener {
        void onItemClick(String path);
    }

    public HistoryAdapter(Activity activity, List<History> list, OnClickListener onClickListener) {
        this.mHistoryList = list;
        this.mOnClickListener = onClickListener;
        HashMap<String, Integer> hashMap = new HashMap<>();
        mIconsOperationList = hashMap;
        hashMap.put(activity.getString(R.string.printed), R.drawable.ic_print);
        hashMap.put(activity.getString(R.string.created), R.drawable.ic_file_create);
        hashMap.put(activity.getString(R.string.deleted), R.drawable.ic_delete);
        hashMap.put(activity.getString(R.string.renamed), R.drawable.ic_rename);
        hashMap.put(activity.getString(R.string.rotated), R.drawable.ic_rotate_page_1);
        hashMap.put(activity.getString(R.string.encrypted), R.drawable.ic_set_password_1);
        hashMap.put(activity.getString(R.string.decrypted), R.drawable.ic_unlock);
    }

    @NonNull
    @Override
    public ViewHistoryHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ViewHistoryHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_history, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHistoryHolder viewHistoryHolder, int position) {
        File file = new File(mHistoryList.get(position).getFilePath());
        String date = mHistoryList.get(position).getDate();
        String[] split = date.split(" ");
        if (split.length >= 3) {
            String[] split2 = split[3].split(":");
            date = split[0] + ", " + split[1] + " " + split[2] + " at " + (split2[0] + ":" + split2[1]);
        }
        String operationType = mHistoryList.get(position).getOperationType();
        viewHistoryHolder.mFilename.setText(file.getName());
        viewHistoryHolder.mOperationDate.setText(date);
        viewHistoryHolder.mOperationType.setText(operationType);
        if (mIconsOperationList == null || !mIconsOperationList.containsKey(operationType)) {
            viewHistoryHolder.mOperationImage.setImageResource(R.drawable.ic_create_black_24dp);
        } else {
            viewHistoryHolder.mOperationImage.setImageResource(mIconsOperationList.get(operationType));
        }
    }

    public void deleteHistory() {
        mHistoryList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mHistoryList == null) {
            return 0;
        }
        return mHistoryList.size();
    }

    public class ViewHistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.fileName)
        TextView mFilename;
        @BindView(R.id.operationDate)
        TextView mOperationDate;
        @BindView(R.id.operationImage)
        ImageView mOperationImage;
        @BindView(R.id.operationType)
        TextView mOperationType;

        ViewHistoryHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClick(mHistoryList.get(getAdapterPosition()).getFilePath());
        }
    }
}

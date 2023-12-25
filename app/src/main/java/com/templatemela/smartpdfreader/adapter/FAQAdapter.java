package com.templatemela.smartpdfreader.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.interfaces.OnItemClickListener;
import com.templatemela.smartpdfreader.model.FAQItem;
import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {
    private final List<FAQItem> mFaqs;

    public final OnItemClickListener mOnItemClickListener;


    public FAQAdapter(List<FAQItem> list, OnItemClickListener onItemClickListener) {
        this.mFaqs = list;
        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new FAQViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_faq, viewGroup, false));
    }
    @Override
    public void onBindViewHolder(FAQViewHolder fAQViewHolder, int position) {
        FAQItem fAQItem = mFaqs.get(position);
        fAQViewHolder.question.setText(fAQItem.getQuestion());
        fAQViewHolder.answer.setText(fAQItem.getAnswer());
        fAQViewHolder.expandableView.setVisibility(fAQItem.isExpanded() ? View.VISIBLE : View.GONE);
    }
    @Override
    public int getItemCount() {
        return this.mFaqs.size();
    }

    public class FAQViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.answer)
        TextView answer;
        @BindView(R.id.expandable_view)
        ConstraintLayout expandableView;
        @BindView(R.id.question)
        TextView question;

        FAQViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            question.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}

package com.templatemela.smartpdfreader.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.adapter.FAQAdapter;
import com.templatemela.smartpdfreader.interfaces.OnItemClickListener;
import com.templatemela.smartpdfreader.model.FAQItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FAQFragment extends Fragment implements OnItemClickListener {
    private Context mContext;
    @BindView(R.id.recycler_view_faq)
    RecyclerView mFAQRecyclerView;
    private FAQAdapter mFaqAdapter;
    private List<FAQItem> mFaqs;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_faq, viewGroup, false);
        ButterKnife.bind(this, inflate);
        mContext = inflate.getContext();

        initFAQs();
        initFAQRecyclerView();
        return inflate;
    }

    private void initFAQs() {
        mFaqs = new ArrayList();
      /*  for (String split : this.mContext.getResources().getStringArray(R.array.faq_question_answers)) {
            String[] split2 = split.split("#####");
            this.mFaqs.add(new FAQItem(split2[0], split2[1]));
        }*/
    }

    private void initFAQRecyclerView() {
        mFaqAdapter = new FAQAdapter(mFaqs, this);
        mFAQRecyclerView.setAdapter(mFaqAdapter);
    }

    @Override
    public void onItemClick(int i) {
        FAQItem fAQItem = mFaqs.get(i);
        fAQItem.setExpanded(!fAQItem.isExpanded());
        mFaqAdapter.notifyItemChanged(i);
    }
}

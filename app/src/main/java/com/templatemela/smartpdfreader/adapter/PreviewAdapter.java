package com.templatemela.smartpdfreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.templatemela.smartpdfreader.R;

import java.io.File;
import java.util.ArrayList;

public class PreviewAdapter extends PagerAdapter {
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final ArrayList<String> mPreviewItems;

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }

    public PreviewAdapter(Context context, ArrayList<String> arrayList) {
        this.mContext = context;
        this.mPreviewItems = arrayList;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup viewGroup, int position) {
        View inflate = mInflater.inflate(R.layout.pdf_preview_item, viewGroup, false);
        File file = new File(mPreviewItems.get(position));
        Picasso.get().load(file).into((ImageView) inflate.findViewById(R.id.image));
        ((TextView) inflate.findViewById(R.id.tvFileName)).setText(file.getName());
        viewGroup.addView(inflate, 0);
        return inflate;
    }

    @Override
    public void destroyItem(ViewGroup viewGroup, int position, @NonNull Object obj) {
        viewGroup.removeView((View) obj);
    }

    @Override
    public int getCount() {
        return mPreviewItems.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.format(mContext.getResources().getString(R.string.showing_image), position + 1, mPreviewItems.size());
    }

    public void setData(ArrayList<String> arrayList) {
        mPreviewItems.clear();
        mPreviewItems.addAll(arrayList);
    }
}

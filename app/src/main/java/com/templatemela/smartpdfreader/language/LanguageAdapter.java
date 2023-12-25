package com.templatemela.smartpdfreader.language;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.templatemela.smartpdfreader.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.MyViewHolder> {
    private final Context mContext;
    private final List<Lang> listOfLanguages;
    private OnItemClickListener mOnItemClickListener;
    public int selectedIndex = -1;

    public LanguageAdapter(Context mContext, List<Lang> listOfLanguages) {
        this.mContext = mContext;
        this.listOfLanguages = listOfLanguages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.language_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Lang lang = listOfLanguages.get(position);
        holder.txtName.setText(lang.getName());
        Bitmap bitmap= getImageFromAssetsFile(mContext,lang.getImagePath());
        holder.countryImg.setImageBitmap(bitmap);
        if (selectedIndex != -1) {
            if (selectedIndex == position) {
                 holder.mainLayout.setBackgroundResource(R.drawable.rect_border_bg);
            } else {
                holder.mainLayout.setBackgroundResource(R.color.white);
            }
        } else {
            holder.mainLayout.setBackgroundResource(R.color.white);
        }
    }


    public  Bitmap getImageFromAssetsFile(Context mContext, String fileName) {
        Bitmap image = null;
        AssetManager am = mContext.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public int getItemCount() {
        return listOfLanguages.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final LinearLayout mainLayout;
        private final TextView txtName;
        private final ImageView countryImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            txtName = itemView.findViewById(R.id.txtName);
            countryImg = itemView.findViewById(R.id.countryImg);
            mainLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onRecItemClick(v, getLayoutPosition());
            }

            notifyDataSetChanged();
        }
    }
}

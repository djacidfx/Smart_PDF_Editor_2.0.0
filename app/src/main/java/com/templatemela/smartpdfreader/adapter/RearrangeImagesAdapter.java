package com.templatemela.smartpdfreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.templatemela.smartpdfreader.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RearrangeImagesAdapter extends RecyclerView.Adapter<RearrangeImagesAdapter.ViewHolder> {
    private final Context mContext;
    private ArrayList<String> mImagesUri;

    public final OnClickListener mOnClickListener;

    public interface OnClickListener {
        void onDownClick(int i);

        void onRemoveClick(int i);

        void onUpClick(int i);
    }


    public RearrangeImagesAdapter(OnClickListener onClickListener, ArrayList<String> arrayList, Context context) {
        this.mOnClickListener = onClickListener;
        this.mImagesUri = arrayList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rearrange_images, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        File file = new File(mImagesUri.get(position));
        if (position == 0) {
            viewHolder.buttonUp.setVisibility(View.GONE);
        } else {
            viewHolder.buttonUp.setVisibility(View.VISIBLE);
        }
        if (position == getItemCount() - 1) {
            viewHolder.buttonDown.setVisibility(View.GONE);
        } else {
            viewHolder.buttonDown.setVisibility(View.VISIBLE);
        }
        Picasso.get().load(file).into(viewHolder.imageView);
        viewHolder.pageNumber.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return mImagesUri.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.buttonDown)
        ImageButton buttonDown;
        @BindView(R.id.buttonUp)
        ImageButton buttonUp;
        @BindView(R.id.image)
        ImageView imageView;
        @BindView(R.id.removeImage)
        ImageButton mRemoveImage;
        @BindView(R.id.pageNumber)
        TextView pageNumber;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            buttonDown.setOnClickListener(this);
            buttonUp.setOnClickListener(this);
            mRemoveImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.buttonDown) {
                mOnClickListener.onDownClick(getAdapterPosition());
            } else if (id == R.id.buttonUp) {
                mOnClickListener.onUpClick(getAdapterPosition());
            } else if (id == R.id.removeImage) {
                mOnClickListener.onRemoveClick(getAdapterPosition());
            }
        }
    }

    public void positionChanged(ArrayList<String> arrayList) {
        mImagesUri = arrayList;
        notifyDataSetChanged();
    }
}

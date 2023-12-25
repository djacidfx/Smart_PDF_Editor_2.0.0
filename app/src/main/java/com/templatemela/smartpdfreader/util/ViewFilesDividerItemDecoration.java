package com.templatemela.smartpdfreader.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.templatemela.smartpdfreader.R;

public class ViewFilesDividerItemDecoration extends RecyclerView.ItemDecoration {
    private final Drawable mDivider;

    public ViewFilesDividerItemDecoration(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.files_divider);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        int paddingLeft = recyclerView.getPaddingLeft();
        int width = recyclerView.getWidth() - recyclerView.getPaddingRight();
        int childCount = recyclerView.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View childAt = recyclerView.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childAt.getLayoutParams();

            int bottom = childAt.getBottom() + params.bottomMargin;
            mDivider.setBounds(paddingLeft, bottom, width, mDivider.getIntrinsicHeight() + bottom);
            mDivider.draw(canvas);
        }
    }
}

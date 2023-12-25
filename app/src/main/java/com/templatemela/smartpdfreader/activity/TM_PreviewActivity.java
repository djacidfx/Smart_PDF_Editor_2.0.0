package com.templatemela.smartpdfreader.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.adapter.PreviewAdapter;
import com.templatemela.smartpdfreader.adapter.PreviewImageOptionsAdapter;
import com.templatemela.smartpdfreader.language.LangSupportBaseActivity;
import com.templatemela.smartpdfreader.model.PreviewImageOptionItem;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.ImageSortUtils;
import com.templatemela.smartpdfreader.util.ThemeUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TM_PreviewActivity extends LangSupportBaseActivity implements PreviewImageOptionsAdapter.OnItemClickListener {
    private static final int INTENT_REQUEST_REARRANGE_IMAGE = 1;
    private ArrayList<String> mImagesArrayList;
    private PreviewAdapter mPreviewAdapter;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle bundle) {
        ThemeUtils.getInstance().setThemeApp(this);
        TM_PreviewActivity.super.onCreate(bundle);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        mImagesArrayList = getIntent().getStringArrayListExtra(Constants.PREVIEW_IMAGES);
        mViewPager = findViewById(R.id.viewpager);
        mPreviewAdapter = new PreviewAdapter(this, mImagesArrayList);
        mViewPager.setAdapter(mPreviewAdapter);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        showOptions();
    }

    private void showOptions() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mRecyclerView.setAdapter(new PreviewImageOptionsAdapter(this, getOptions(), getApplicationContext()));
    }

    private ArrayList<PreviewImageOptionItem> getOptions() {
        ArrayList<PreviewImageOptionItem> arrayList = new ArrayList<>();
        arrayList.add(new PreviewImageOptionItem(R.drawable.ic_rearrange, getString(R.string.rearrange_text)));
        arrayList.add(new PreviewImageOptionItem(R.drawable.ic_sort, getString(R.string.sort)));
        return arrayList;
    }

    @Override
    public void onItemClick(int i) {
        if (i == 0) {
            startActivityForResult(TM_RearrangeImages.jumpActivity(this, mImagesArrayList), INTENT_REQUEST_REARRANGE_IMAGE);
        } else if (i == 1) {
            sortImages();
        }
    }

    private void sortImages() {
        new MaterialDialog.Builder(this).title(R.string.sort_by_title).items(R.array.sort_options_images).itemsCallback((materialDialog, view, i, charSequence) -> {
            ImageSortUtils.getInstance().performSortOperation(i, mImagesArrayList);
            mPreviewAdapter.setData(new ArrayList(mImagesArrayList));
            mViewPager.setAdapter(mPreviewAdapter);
        }).negativeText(R.string.cancel).show();
    }

    private void setUriResults() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(Constants.RESULT, mImagesArrayList);
        setResult(-1, intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == INTENT_REQUEST_REARRANGE_IMAGE) {
            try {
                mImagesArrayList = intent.getStringArrayListExtra(Constants.RESULT);
                mPreviewAdapter.setData(mImagesArrayList);
                mViewPager.setAdapter(mPreviewAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        setUriResults();
    }

    public static Intent jumpActivity(Context context, ArrayList<String> arrayList) {
        Intent intent = new Intent(context, TM_PreviewActivity.class);
        intent.putExtra(Constants.PREVIEW_IMAGES, arrayList);
        return intent;
    }
}

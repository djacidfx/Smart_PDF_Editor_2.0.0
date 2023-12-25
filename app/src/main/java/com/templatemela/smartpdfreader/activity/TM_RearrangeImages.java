package com.templatemela.smartpdfreader.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.adapter.RearrangeImagesAdapter;
import com.templatemela.smartpdfreader.ads.AdsService;
import com.templatemela.smartpdfreader.language.LangSupportBaseActivity;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.DialogUtils;
import com.templatemela.smartpdfreader.util.ImageSortUtils;
import com.templatemela.smartpdfreader.util.ThemeUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TM_RearrangeImages extends LangSupportBaseActivity implements RearrangeImagesAdapter.OnClickListener {
    private ArrayList<String> mImages;
    private RearrangeImagesAdapter mRearrangeImagesAdapter;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle bundle) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ThemeUtils.getInstance().setThemeApp(this);
        super.onCreate(bundle);
        setContentView(R.layout.activity_rearrange_images);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        mImages = getIntent().getStringArrayListExtra(Constants.PREVIEW_IMAGES);
        initRecyclerView(mImages);

        AdsService.getInstance().showAdaptiveBannerAd(findViewById(R.id.layoutDelete));
    }

    private void initRecyclerView(ArrayList<String> arrayList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        mRearrangeImagesAdapter = new RearrangeImagesAdapter(this, arrayList, this);
        recyclerView.setAdapter(mRearrangeImagesAdapter);
    }

    @Override
    public void onUpClick(int i) {
        mImages.add(i - 1, mImages.remove(i));
        mRearrangeImagesAdapter.positionChanged(mImages);
    }

    @Override
    public void onDownClick(int i) {
        mImages.add(i + 1, mImages.remove(i));
        mRearrangeImagesAdapter.positionChanged(mImages);
    }

    @Override
    public void onRemoveClick(int i) {
        if (mSharedPreferences.getBoolean(Constants.CHOICE_REMOVE_IMAGE, false)) {
            mImages.remove(i);
            mRearrangeImagesAdapter.positionChanged(mImages);
            return;
        }
        DialogUtils.getInstance().createWarningDialog(this, R.string.remove_image_message)
                .checkBoxPrompt(getString(R.string.dont_show_again), false, null)
                .onPositive((materialDialog, dialogAction) ->
                        {
                            if (materialDialog.isPromptCheckBoxChecked()) {
                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                edit.putBoolean(Constants.CHOICE_REMOVE_IMAGE, true);
                                edit.apply();
                            }
                            mImages.remove(i);
                            mRearrangeImagesAdapter.positionChanged(mImages);
                        }
                ).show();
    }


    private void setUriResult() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(Constants.RESULT, mImages);
        setResult(-1, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        setUriResult();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(menuItem);
        }
        setUriResult();
        return true;
    }

    public static Intent jumpActivity(Context context, ArrayList<String> arrayList) {
        Intent intent = new Intent(context, TM_RearrangeImages.class);
        intent.putExtra(Constants.PREVIEW_IMAGES, arrayList);
        return intent;
    }

    private void sortImages() {
        new MaterialDialog.Builder(this).title(R.string.sort_by_title).items(R.array.sort_options_images).itemsCallback((materialDialog, view, i, charSequence) -> {
            ImageSortUtils.getInstance().performSortOperation(i, mImages);
            mRearrangeImagesAdapter.positionChanged(mImages);
        }).negativeText(R.string.cancel).show();
    }


    @OnClick({R.id.sortLayout})
    public void sortImg() {
        sortImages();
    }
}

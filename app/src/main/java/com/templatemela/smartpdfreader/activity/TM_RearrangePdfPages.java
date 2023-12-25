package com.templatemela.smartpdfreader.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.adapter.RearrangePdfAdapter;
import com.templatemela.smartpdfreader.language.LangSupportBaseActivity;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.DialogUtils;
import com.templatemela.smartpdfreader.util.ThemeUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TM_RearrangePdfPages extends LangSupportBaseActivity implements RearrangePdfAdapter.OnClickListener {
    public static ArrayList<Bitmap> mImages;
    private ArrayList<Integer> mInitialSequence;
    private RearrangePdfAdapter mRearrangeImagesAdapter;

    private ArrayList<Integer> mSequence;
    private SharedPreferences mSharedPreferences;
    @BindView(R.id.sortLayout)
    LinearLayout sortButton;

    @Override
    public void onCreate(Bundle bundle) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ThemeUtils.getInstance().setThemeApp(this);
        super.onCreate(bundle);
        setContentView(R.layout.activity_rearrange_images);

        setSupportActionBar(findViewById(R.id.toolbar));
        setTitle(Constants.REMOVE_PAGES);
        ButterKnife.bind(this);

        mSequence = new ArrayList<>();
        mInitialSequence = new ArrayList<>();
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sortButton.setVisibility(View.GONE);
        if (mImages == null || mImages.size() < 1) {
            finish();
        } else {
            initRecyclerView(mImages);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    private void initRecyclerView(ArrayList<Bitmap> arrayList) {
        int i = 0;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        mRearrangeImagesAdapter = new RearrangePdfAdapter(this, arrayList, this);
        recyclerView.setAdapter(mRearrangeImagesAdapter);
        mSequence = new ArrayList<>();
        while (i < arrayList.size()) {
            i++;
            mSequence.add(i);
        }
        mInitialSequence.addAll(mSequence);
    }

    private void swap(int pos, int finalPos) {
        if (pos < mSequence.size()) {
            int intValue = mSequence.get(pos);
            mSequence.set(pos, mSequence.get(finalPos));
            mSequence.set(finalPos, intValue);
        }
    }

    @Override
    public void onUpClick(int pos) {
        int finalPos = pos - 1;
        mImages.add(finalPos, mImages.remove(pos));
        mRearrangeImagesAdapter.positionChanged(mImages);
        swap(pos, finalPos);
    }

    @Override
    public void onDownClick(int pos) {
        int finalPos = pos + 1;
        mImages.add(finalPos, mImages.remove(pos));
        mRearrangeImagesAdapter.positionChanged(mImages);
        swap(pos, finalPos);
    }

    @Override
    public void onRemoveClick(int pos) {
        if (mSharedPreferences.getBoolean(Constants.CHOICE_REMOVE_IMAGE, false)) {
            mImages.remove(pos);
            mRearrangeImagesAdapter.positionChanged(mImages);
            mSequence.remove(pos);
            return;
        }
        DialogUtils.getInstance().createWarningDialog(this, R.string.remove_page_message)
                .checkBoxPrompt(getString(R.string.dont_show_again), false, null)
                .onPositive((materialDialog, dialogAction) ->
                        {
                            if (materialDialog.isPromptCheckBoxChecked()) {
                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                edit.putBoolean(Constants.CHOICE_REMOVE_IMAGE, true);
                                edit.apply();
                            }
                            mImages.remove(pos);
                            mRearrangeImagesAdapter.positionChanged(mImages);
                            mSequence.remove(pos);
                        }

                ).show();
    }

    private void setIntent() {
        Intent intent = new Intent();
        StringBuilder sb = new StringBuilder();
        for (Integer integer : mSequence) {
            sb.append(integer.intValue());
            sb.append(",");
        }
        intent.putExtra(Constants.RESULT, sb.toString());
        intent.putExtra(Constants.SAME_FILE, mInitialSequence.equals(mSequence));
        setResult(-1, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        setIntent();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            setIntent();
            return true;
        } else if (menuItem.getItemId() != R.id.action_menu_done) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            setIntent();
            return true;
        }
    }

    public static Intent jumpActivity(Context context) {
        return new Intent(context, TM_RearrangePdfPages.class);
    }
}

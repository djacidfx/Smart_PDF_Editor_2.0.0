package com.templatemela.smartpdfreader.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.adapter.BrushItemAdapter;
import com.templatemela.smartpdfreader.adapter.ImageFiltersAdapter;
import com.templatemela.smartpdfreader.interfaces.OnFilterItemClickedListener;
import com.templatemela.smartpdfreader.interfaces.OnItemClickListener;
import com.templatemela.smartpdfreader.language.LangSupportBaseActivity;
import com.templatemela.smartpdfreader.model.BrushItem;
import com.templatemela.smartpdfreader.model.FilterItem;
import com.templatemela.smartpdfreader.util.BrushUtils;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.ImageFilterUtils;
import com.templatemela.smartpdfreader.util.StringUtils;
import com.templatemela.smartpdfreader.util.ThemeUtils;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

public class TM_ImageEditor extends LangSupportBaseActivity implements OnFilterItemClickedListener, OnItemClickListener {
    @BindView(R.id.doodle_colors)
    RecyclerView brushColorsView;
    @BindView(R.id.doodleSeekBar)
    SeekBar doodleSeekBar;
    @BindView(R.id.imagecount)
    TextView imageCount;
    private ArrayList<BrushItem> mBrushItems;
    private boolean mClicked = true;
    private boolean mClickedFilter = false;

    public int mCurrentImage;
    private int mDisplaySize;
    private boolean mDoodleSelected = false;
    private ArrayList<FilterItem> mFilterItems;
    private String mFilterName;
    private ArrayList<String> mFilterUris = new ArrayList<>();

    public final ArrayList<String> mImagePaths = new ArrayList<>();

    public PhotoEditor mPhotoEditor;
    @BindView(R.id.photoEditorView)
    PhotoEditorView photoEditorView;
    @BindView(R.id.previousImageButton)
    ImageView previousButton;

    @Override
    public void onCreate(Bundle bundle) {
        ThemeUtils.getInstance().setThemeApp(this);
        super.onCreate(bundle);
        setContentView(R.layout.activity_photo_editor);
        ButterKnife.bind(this);
        initValues();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void initValues() {
        mFilterUris = getIntent().getStringArrayListExtra(Constants.IMAGE_EDITOR_KEY);
        mDisplaySize = mFilterUris.size();
        mFilterItems = ImageFilterUtils.getInstance().getFiltersList(this);
        mBrushItems = BrushUtils.getInstance().getBrushItems();
        mImagePaths.addAll(mFilterUris);
        photoEditorView.getSource().setImageBitmap(BitmapFactory.decodeFile(mFilterUris.get(0)));
        changeAndShowImageCount(0);
        initRecyclerView();
        mPhotoEditor = new PhotoEditor.Builder(this, photoEditorView).setPinchTextScalable(true).build();
        doodleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                mPhotoEditor.setBrushSize((float) i);
            }
        });
        mPhotoEditor.setBrushSize(30.0f);
        mPhotoEditor.setBrushDrawingMode(false);
    }

    @OnClick({R.id.nextimageButton})
    public void nextImg() {
        if (mClicked) {
            changeAndShowImageCount((mCurrentImage + 1) % mDisplaySize);
        } else {
            StringUtils.getInstance().showSnackbar(this, R.string.save_first);
        }
    }

    @OnClick({R.id.previousImageButton})
    public void previousImg() {
        if (mClicked) {
            changeAndShowImageCount(mCurrentImage - (1 % mDisplaySize));
        } else {
            StringUtils.getInstance().showSnackbar(this, R.string.save_first);
        }
    }

    private void changeAndShowImageCount(int i) {
        int i2;
        if (i >= 0 && i < (i2 = mDisplaySize)) {
            mCurrentImage = i % i2;
            photoEditorView.getSource().setImageBitmap(BitmapFactory.decodeFile(mImagePaths.get(mCurrentImage)));
            imageCount.setText(String.format(getString(R.string.showing_image), mCurrentImage + 1, mDisplaySize));
        }
    }

    @OnClick({R.id.savecurrent})
    public void saveC() {
        mClicked = true;
        if (mClickedFilter || mDoodleSelected) {
            saveCurrentImage();
            showHideBrushEffect(false);
            mClickedFilter = false;
            mDoodleSelected = false;
        }
    }

    @OnClick({R.id.resetCurrent})
    public void resetCurrent() {
        mClicked = true;
        String curr = mFilterUris.get(mCurrentImage);
        mImagePaths.set(mCurrentImage, curr);
        photoEditorView.getSource().setImageBitmap(BitmapFactory.decodeFile(curr));
        mPhotoEditor.clearAllViews();
        mPhotoEditor.undo();
    }

    private void saveCurrentImage() {
        try {
            File externalStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(externalStorageDirectory.getAbsolutePath() + "/PDFfilter");
            file.mkdirs();
            mPhotoEditor.saveAsFile(new File(file, String.format(getString(R.string.filter_file_name), new Object[]{System.currentTimeMillis(), mFilterName})).getAbsolutePath(), new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess(String imagePath) {
                    mImagePaths.remove(mCurrentImage);
                    mImagePaths.add(mCurrentImage, imagePath);
                    photoEditorView.getSource().setImageBitmap(BitmapFactory.decodeFile(mImagePaths.get(mCurrentImage)));
                    Toast.makeText(getApplicationContext(), R.string.filter_saved, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception exc) {
                    Toast.makeText(getApplicationContext(), R.string.filter_not_saved, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new ImageFiltersAdapter(mFilterItems, this, this));
        brushColorsView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        brushColorsView.setAdapter(new BrushItemAdapter(this, this, mBrushItems));
    }

    @Override
    public void onItemClick(View view, int i) {
        mClicked = i == 0;
        if (i == 1) {
            mPhotoEditor = new PhotoEditor.Builder(this, photoEditorView).setPinchTextScalable(true).build();
            if (doodleSeekBar.getVisibility() == View.GONE && brushColorsView.getVisibility() == View.GONE) {
                showHideBrushEffect(true);
            } else if (doodleSeekBar.getVisibility() == View.VISIBLE && brushColorsView.getVisibility() == View.VISIBLE) {
                showHideBrushEffect(false);
            }
        } else {
            applyFilter(mFilterItems.get(i).getFilter());
        }
    }

    private void showHideBrushEffect(boolean isShow) {
        mPhotoEditor.setBrushDrawingMode(isShow);
        int i = 0;
        doodleSeekBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
        if (!isShow) {
            i = View.GONE;
        }
        brushColorsView.setVisibility(i);
        mDoodleSelected = true;
    }

    private void applyFilter(PhotoFilter photoFilter) {
        try {
            boolean isApply = true;
            mPhotoEditor = new PhotoEditor.Builder(this, photoEditorView).setPinchTextScalable(true).build();
            mPhotoEditor.setFilterEffect(photoFilter);
            mFilterName = photoFilter.name();
            if (photoFilter == PhotoFilter.NONE) {
                isApply = false;
            }
            mClickedFilter = isApply;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(Constants.RESULT, mImagePaths);
        setResult(-1, intent);
        finish();
    }

    @Override
    public void onItemClick(int i) {
        int color = mBrushItems.get(i).getColor();
        if (i == mBrushItems.size() - 1) {
            MaterialDialog build = new MaterialDialog.Builder(this).title(R.string.choose_color_text).customView(R.layout.color_pallete_layout, true).positiveText(R.string.ok).negativeText(R.string.cancel).build();
            MDButton actionButton = build.getActionButton(DialogAction.POSITIVE);
            actionButton.setEnabled(true);
            actionButton.setOnClickListener(view ->
                    {
                        ColorPickerView colorPickerView = build.getCustomView().findViewById(R.id.color_pallete);
                        try {
                            doodleSeekBar.setBackgroundColor(colorPickerView.getColor());
                            mPhotoEditor.setBrushColor(colorPickerView.getColor());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        build.dismiss();
                    }
            );

            build.show();
            return;
        }
        doodleSeekBar.setBackgroundColor(getResources().getColor(color));
        mPhotoEditor.setBrushColor(getResources().getColor(color));
    }

    public static Intent jumpActivity(Context context, ArrayList<String> arrayList) {
        Intent intent = new Intent(context, TM_ImageEditor.class);
        intent.putExtra(Constants.IMAGE_EDITOR_KEY, arrayList);
        return intent;
    }
}

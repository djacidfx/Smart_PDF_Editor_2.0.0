package com.templatemela.smartpdfreader.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.fragment.ImageToPdfFragment;
import com.templatemela.smartpdfreader.language.LangSupportBaseActivity;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.StringUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TM_CropImageActivity extends LangSupportBaseActivity {
    @BindView(R.id.cropButton)
    Button cropImageButton;
    @BindView(R.id.cropImageView)
    CropImageView mCropImageView;
    private final HashMap<Integer, Uri> mCroppedImageUris = new HashMap<>();
    private boolean mCurrentImageEdited = false;
    private int mCurrentImageIndex = 0;
    private boolean mFinishedClicked = false;
    @BindView(R.id.imagecount)
    TextView mImageCount;
    private ArrayList<String> mImages;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_crop_image_activity);
        ButterKnife.bind(this);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        setUpCropImageView();
        mImages = ImageToPdfFragment.mImagesUri;
        mFinishedClicked = false;
        for (int i = 0; i < mImages.size(); i++) {
            mCroppedImageUris.put(i, Uri.fromFile(new File(mImages.get(i))));
        }
        if (mImages.size() == 0) {
            finish();
        }
        setImage(0);
    }

    private void showGoogleInterstitialAds() {
        String child;
        mCurrentImageEdited = false;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + Constants.pdfDirectory);
        Uri imageUri = mCropImageView.getImageUri();
        if (imageUri == null) {
            StringUtils.getInstance().showSnackbar(this, R.string.error_uri_not_found);
            return;
        }
        String path = imageUri.getPath();
        if (path != null) {
            child = "cropped_" + FileUtils.getFileName(path);
        } else {
            child = "cropped_im";
        }
        mCropImageView.saveCroppedImageAsync(Uri.fromFile(new File(file, child)));
    }

    @OnClick({R.id.cropButton})
    public void cropButtonClicked() {
        showGoogleInterstitialAds();
    }

    @OnClick({R.id.rotateButton})
    public void rotateButtonClicked() {
        mCurrentImageEdited = true;
        mCropImageView.rotateImage(90);
    }

    @OnClick({R.id.nextimageButton})
    public void nextImageClicked() {
        if (mImages.size() != 0) {
            if (!mCurrentImageEdited) {
                int size = (mCurrentImageIndex + 1) % mImages.size();
                mCurrentImageIndex = size;
                setImage(size);
                return;
            }
            StringUtils.getInstance().showSnackbar(this, R.string.save_first);
        }
    }

    @OnClick({R.id.previousImageButton})
    public void prevImgBtnClicked() {
        if (mImages.size() != 0) {
            if (!mCurrentImageEdited) {
                if (mCurrentImageIndex == 0) {
                    mCurrentImageIndex = mImages.size();
                }
                int size = (mCurrentImageIndex - 1) % mImages.size();
                mCurrentImageIndex = size;
                setImage(size);
                return;
            }
            StringUtils.getInstance().showSnackbar(this, R.string.save_first);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_crop_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
            setResult(0);
            finish();
            return true;
        } else if (itemId == R.id.action_done) {
            this.mFinishedClicked = true;
            cropButtonClicked();
            return true;
        } else if (itemId != R.id.action_skip) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            this.mCurrentImageEdited = false;
            nextImageClicked();
            return true;
        }
    }

    private void setUpCropImageView() {
        mCropImageView.setOnCropImageCompleteListener((cropImageView, cropResult) -> {
            mCroppedImageUris.put(mCurrentImageIndex, cropResult.getUri());
            mCropImageView.setImageUriAsync(mCroppedImageUris.get(mCurrentImageIndex));
            if (mFinishedClicked) {
                Intent intent = new Intent();
                intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, mCroppedImageUris);
                setResult(-1, intent);
                finish();
            }
        });
    }

    private void setImage(int i) {
        mCurrentImageEdited = false;
        if (i >= 0 && i < mImages.size()) {
            mImageCount.setText(getString(R.string.cropImage_activityTitle) + " " + (i + 1) + " of " + mImages.size());
            mCropImageView.setImageUriAsync(mCroppedImageUris.get(i));
        }
    }
}

package com.templatemela.smartpdfreader.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;
import butterknife.ButterKnife;
import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.adapter.PreviewAdapter;
import com.templatemela.smartpdfreader.language.LangSupportBaseActivity;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.ThemeUtils;
import java.util.ArrayList;

public class TM_ImagesPreviewActivity extends LangSupportBaseActivity {

    @Override
    public void onCreate(Bundle bundle) {
        ThemeUtils.getInstance().setThemeApp(this);
        super.onCreate(bundle);

        setContentView(R.layout.activity_preview_images);

        ButterKnife.bind(this);
        ArrayList<String> previewImageList = getIntent().getStringArrayListExtra(Constants.PREVIEW_IMAGES);
        ViewPager viewpager = findViewById(R.id.viewpager);
        viewpager.setAdapter(new PreviewAdapter(this, previewImageList));
        viewpager.setPageTransformer(true, new DepthPageTransformer());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public static Intent jumpActivity(Context context, ArrayList<String> arrayList) {
        Intent intent = new Intent(context, TM_ImagesPreviewActivity.class);
        intent.putExtra(Constants.PREVIEW_IMAGES, arrayList);
        return intent;
    }
}

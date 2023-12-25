package com.templatemela.smartpdfreader.language;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.activity.MainActivity;
import com.templatemela.smartpdfreader.util.AdsUtils;

import java.util.ArrayList;
import java.util.List;

public class LanguageSelectionActivity extends AppCompatActivity implements OnItemClickListener, View.OnClickListener {
    private Context mContext;
    private RecyclerView recLanguage;
    private List<Lang> listOfLanguages = new ArrayList<>();
    private LanguageAdapter languageAdapter;
    private ImageView imgDone;
    private boolean isFromMain = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);
        if (getIntent().getExtras() != null) {
            isFromMain = getIntent().getBooleanExtra("isFromMain", false);
        }
        mContext = LanguageSelectionActivity.this;
        recLanguage = findViewById(R.id.recLanguage);
        imgDone = findViewById(R.id.imgDone);
        imgDone.setOnClickListener(this);

        listOfLanguages.clear();
        listOfLanguages = Languages.getLanguages();

        recLanguage.setLayoutManager(new GridLayoutManager(mContext, 2));
        languageAdapter = new LanguageAdapter(mContext, listOfLanguages);
        recLanguage.setAdapter(languageAdapter);
        languageAdapter.selectedIndex = 0;
        languageAdapter.setOnItemClickListener(this);
    }

    void changeLocale(String langCode) {
        LocaleUtils.changeLang(getApplicationContext(), langCode);
        SharedPreferences sharedPreferences = getSharedPreferences("MyLangPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(LocaleUtils.SELECTED_LANGUAGE, langCode);
        myEdit.putBoolean(LocaleUtils.LANGUAGE_SELECTED_FIRST_TIME, true);
        myEdit.apply();

        if (!isFromMain) {

            AdsUtils.initAd(LanguageSelectionActivity.this);
            AdsUtils.loadInterAd(LanguageSelectionActivity.this);

            AdsUtils.showInterAd(LanguageSelectionActivity.this,new Intent(LanguageSelectionActivity.this, MainActivity.class));
//            Intent intent = new Intent(LanguageSelectionActivity.this, MainActivity.class);
//            startActivity(intent);
        }

        finish();

    }

    @Override
    public void onRecItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.mainLayout:
                if (languageAdapter != null) {
                    languageAdapter.selectedIndex = position;
                    languageAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgDone:
                if (listOfLanguages.size() > 0) {
                    changeLocale(listOfLanguages.get(languageAdapter.selectedIndex).getCode());
                }
                break;
        }
    }
}

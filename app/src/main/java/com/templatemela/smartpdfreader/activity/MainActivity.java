package com.templatemela.smartpdfreader.activity;


import static android.os.Build.VERSION.SDK_INT;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.fragment.HomeFragment;
import com.templatemela.smartpdfreader.fragment.ImageToPdfFragment;
import com.templatemela.smartpdfreader.language.LangSupportBaseActivity;
import com.templatemela.smartpdfreader.language.LocaleUtils;
import com.templatemela.smartpdfreader.providers.fragmentmanagement.FragmentManagement;
import com.templatemela.smartpdfreader.util.AppUtils;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.FeedbackUtils;
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.PermissionsUtils;
import com.templatemela.smartpdfreader.util.ThemeUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends LangSupportBaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int PERMISSION_REQUEST_CODE = 0;
    private FeedbackUtils mFeedbackUtils;
    private FragmentManagement mFragmentManagement;
    private SparseIntArray mFragmentSelectedMap;
    private SwitchCompat switchNightMode;
    private NavigationView mNavigationView;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle bundle) {
        ThemeUtils.getInstance().setThemeApp(this);
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_menu);
        initializeValues();
        setXMLParsers();
        handleReceivedImagesIntent(mFragmentManagement.checkForAppShortcutClicked());
        displayFeedBackAndWhatsNew();
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
        else
        {
            getRuntimePermissions();
        }
    }

    private void setTheme(int themeMode, int prefsMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode);
        saveTheme(prefsMode);
    }
    public void saveTheme(int theme) {
        SharedPreferences sharedPreferences = getSharedPreferences(AppUtils.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(AppUtils.KEY_THEME, theme).apply();
    }


    private Bitmap resizeBitmapImageFn(Bitmap bitmap, int i) {
        int h;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > height) {
            if (i < width) {
                h = (int) (((float) height) * (((float) i) / ((float) width)));
                return Bitmap.createScaledBitmap(bitmap, i, h, true);
            }
        } else if (i < height) {
            int w = (int) (((float) width) * (((float) i) / ((float) height)));
            h = i;
            i = w;
            return Bitmap.createScaledBitmap(bitmap, i, h, true);
        }
        i = width;
        h = height;
        return Bitmap.createScaledBitmap(bitmap, i, h, true);
    }

    private void setXMLParsers() {
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
    }

    private void displayFeedBackAndWhatsNew() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int launchCount = mSharedPreferences.getInt(Constants.LAUNCH_COUNT, 0);
        if (launchCount > 0 && launchCount % 15 == 0) {
            mFeedbackUtils.rateUs();
        }
        if (launchCount != -1) {
            mSharedPreferences.edit().putInt(Constants.LAUNCH_COUNT, launchCount + 1).apply();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isStoragePermissionGranted()) {
            FileUtils.makeAndClearTemp();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("MyLangPref", MODE_PRIVATE);
        String lang_code = sharedPreferences.getString(LocaleUtils.SELECTED_LANGUAGE, LocaleUtils.ENGLISH);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(lang_code.toLowerCase()));
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);
        initializeValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourites, menu);
//        menu.getItem(0).setIcon(new BitmapDrawable(getResources(), resizeBitmapImageFn(BitmapFactory.decodeResource(getResources(), R.drawable.ic_favorite), 58)));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_favourites_item) {
            setTitle(R.string.favourites);
            this.mFragmentManagement.favouritesFragmentOption();
        } else if (menuItem.getItemId() == R.id.menu_home_item) {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            HomeFragment aPV_HomeFragment = new HomeFragment();
            setTitle(R.string.app_name);
            try {
                supportFragmentManager.beginTransaction().replace(R.id.content, aPV_HomeFragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }


    private void openWelcomeActivity() {
        if (!mSharedPreferences.getBoolean(Constants.IS_WELCOME_ACTIVITY_SHOWN, false)) {
            Intent intent = new Intent(this, TM_WelcomeActivity.class);
            mSharedPreferences.edit().putBoolean(Constants.IS_WELCOME_ACTIVITY_SHOWN, true).apply();
            startActivity(intent);
        }

        /*Intent intent = new Intent(this, TM_WelcomeActivity.class);
        mSharedPreferences.edit().putBoolean(Constants.IS_WELCOME_ACTIVITY_SHOWN, true).apply();
        startActivity(intent);*/
    }

    private void initializeValues() {
        mFeedbackUtils = new FeedbackUtils(this);
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.getMenu().clear();
        mNavigationView.inflateMenu(R.menu.activity_main_drawer);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.nav_home);
        mNavigationView.setItemIconTintList(null);
        mFragmentManagement = new FragmentManagement(this, mNavigationView);
        setTitleMap();
        setNavMenuFont();
        MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.nav_dark_theme);
        switchNightMode=menuItem.getActionView().findViewById(R.id.switch_id);
        initTheme(switchNightMode);
        switchNightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("aaaaa","changed");
                SharedPreferences sharedPreferences = getSharedPreferences(AppUtils.PREFS_NAME, Context.MODE_PRIVATE);
                if (isChecked) {
                    sharedPreferences.edit().putInt(AppUtils.KEY_THEME, AppUtils.THEME_DARK).apply();
                    setTheme(AppCompatDelegate.MODE_NIGHT_YES, AppUtils.THEME_DARK);
                } else {
                    sharedPreferences.edit().putInt(AppUtils.KEY_THEME, AppUtils.THEME_LIGHT).apply();
                    setTheme(AppCompatDelegate.MODE_NIGHT_NO, AppUtils.THEME_LIGHT);
                }
            }
        });
    }


    private void setNavMenuFont() {
        Menu m = mNavigationView.getMenu();

        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for applying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    SpannableString s = new SpannableString(subMenuItem.getTitle());
                    s.setSpan(new TypefaceSpan("font/sfpro_display_bold.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    subMenuItem.setTitle(s);
                }
            }
        }
    }

    private void handleReceivedImagesIntent(Fragment fragment) {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (type != null && type.startsWith("image/")) {
            if ("android.intent.action.SEND_MULTIPLE".equals(action)) {
                handleSendMultipleImages(intent, fragment);
            } else if ("android.intent.action.SEND".equals(action)) {
                handleSendImage(intent, fragment);
            }
        }
    }



    private void handleSendImage(Intent intent, Fragment fragment) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(intent.getParcelableExtra("android.intent.extra.STREAM"));
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getString(R.string.bundleKey), arrayList);
        fragment.setArguments(bundle);
    }

    private void handleSendMultipleImages(Intent intent, Fragment fragment) {
        ArrayList parcelableArrayListExtra = intent.getParcelableArrayListExtra("android.intent.extra.STREAM");
        if (parcelableArrayListExtra != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(getString(R.string.bundleKey), parcelableArrayListExtra);
            fragment.setArguments(bundle);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else if (mFragmentManagement.handleBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        setTitleFragment(mFragmentSelectedMap.get(menuItem.getItemId()));
        return mFragmentManagement.handleNavigationItemSelected(menuItem.getItemId());


    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        initializeValues();
    }

    private void initTheme(SwitchCompat switchNightMode) {
        int savedTheme = getSavedTheme();
        if (savedTheme == AppUtils.THEME_LIGHT) {
            switchNightMode.setChecked(false);
            setTheme(AppCompatDelegate.MODE_NIGHT_NO, AppUtils.THEME_LIGHT);
        } else if (savedTheme == AppUtils.THEME_DARK) {
            switchNightMode.setChecked(true);
            setTheme(AppCompatDelegate.MODE_NIGHT_YES,AppUtils.THEME_DARK);
        }
    }

    private int getSavedTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppUtils.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(AppUtils.KEY_THEME, AppUtils.THEME_UNDEFINED);
    }



    public void setNavigationViewSelection(int id) {
        mNavigationView.setCheckedItem(id);
    }

    private void getRuntimePermissions() {
        Dexter.withActivity(MainActivity.this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            Toast.makeText(MainActivity.this, "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(error -> {
                    Toast.makeText(MainActivity.this, "Error occurred! ", Toast.LENGTH_SHORT).show();
                })
                .onSameThread().check();


    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package",MainActivity.this.getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, PERMISSION_REQUEST_CODE);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });
        builder.show();
    }

    private boolean isStoragePermissionGranted() {
        return PermissionsUtils.getInstance().checkRuntimePermissions(this, Constants.READ_WRITE_PERMISSIONS);
    }

    public void convertImagesToPdf(ArrayList<Uri> arrayList) {
        ImageToPdfFragment imageToPdfFragment = new ImageToPdfFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getString(R.string.bundleKey), arrayList);
        imageToPdfFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, imageToPdfFragment).commit();
    }

    private void setTitleMap() {
        mFragmentSelectedMap = new SparseIntArray();
        mFragmentSelectedMap.append(R.id.nav_home, R.string.app_name);
        mFragmentSelectedMap.append(R.id.nav_camera, R.string.images_to_pdf);
        mFragmentSelectedMap.append(R.id.nav_qrcode, R.string.qr_barcode_pdf);
        mFragmentSelectedMap.append(R.id.nav_add_text, R.string.add_text);
        mFragmentSelectedMap.append(R.id.nav_gallery, R.string.viewFiles);
        mFragmentSelectedMap.append(R.id.nav_merge, R.string.merge_pdf);
        mFragmentSelectedMap.append(R.id.nav_split, R.string.split_pdf);
        mFragmentSelectedMap.append(R.id.nav_text_to_pdf, R.string.text_to_pdf);
        mFragmentSelectedMap.append(R.id.nav_history, R.string.history);
        mFragmentSelectedMap.append(R.id.nav_add_password, R.string.add_password);

        mFragmentSelectedMap.append(R.id.nav_about, R.string.about_us);
        mFragmentSelectedMap.append(R.id.nav_privacy, R.string.privacy);
        mFragmentSelectedMap.append(R.id.nav_settings, R.string.settings);
        mFragmentSelectedMap.append(R.id.nav_extract_images, R.string.extract_images);
        mFragmentSelectedMap.append(R.id.nav_pdf_to_images, R.string.pdf_to_images);
        mFragmentSelectedMap.append(R.id.nav_remove_pages, R.string.remove_pages);
        mFragmentSelectedMap.append(R.id.nav_rearrange_pages, R.string.reorder_pages);
        mFragmentSelectedMap.append(R.id.nav_compress_pdf, R.string.compress_pdf);
        mFragmentSelectedMap.append(R.id.nav_add_images, R.string.add_images);
        mFragmentSelectedMap.append(R.id.nav_remove_duplicate_pages, R.string.remove_duplicate_pages);
        mFragmentSelectedMap.append(R.id.nav_invert_pdf, R.string.invert_pdf);
        mFragmentSelectedMap.append(R.id.nav_add_watermark, R.string.add_watermark);
        mFragmentSelectedMap.append(R.id.nav_zip_to_pdf, R.string.zip_to_pdf);
        mFragmentSelectedMap.append(R.id.nav_rotate_pages, R.string.rotate_pages);
        mFragmentSelectedMap.append(R.id.nav_excel_to_pdf, R.string.excel_to_pdf);
        mFragmentSelectedMap.append(R.id.nav_faq, R.string.faqs);
    }

    private void setTitleFragment(int id) {
        if (id != 0) {
            setTitle(id);
        }
    }
}

package com.templatemela.smartpdfreader.providers.fragmentmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.activity.TM_WelcomeActivity;
import com.templatemela.smartpdfreader.fragment.AboutUsFragment;
import com.templatemela.smartpdfreader.fragment.AddImagesFragment;
import com.templatemela.smartpdfreader.fragment.AddTextFragment;
import com.templatemela.smartpdfreader.fragment.Add_RemovePagesFragment;
import com.templatemela.smartpdfreader.fragment.ExceltoPdfFragment;
import com.templatemela.smartpdfreader.fragment.ExtractTextFragment;
import com.templatemela.smartpdfreader.fragment.FAQFragment;
import com.templatemela.smartpdfreader.fragment.FavouritesFragment;
import com.templatemela.smartpdfreader.fragment.HistoryFragment;
import com.templatemela.smartpdfreader.fragment.HomeFragment;
import com.templatemela.smartpdfreader.fragment.ImageToPdfFragment;
import com.templatemela.smartpdfreader.fragment.InvertPdfFragment;
import com.templatemela.smartpdfreader.fragment.MergeFilesFragment;
import com.templatemela.smartpdfreader.fragment.PdfToImageFragment;
import com.templatemela.smartpdfreader.fragment.PrivacyPolicyFragment;
import com.templatemela.smartpdfreader.fragment.QrBarcodeScanFragment;
import com.templatemela.smartpdfreader.fragment.RemoveDuplicatePagesFragment;
import com.templatemela.smartpdfreader.fragment.SettingsFragment;
import com.templatemela.smartpdfreader.fragment.SplitFilesFragment;
import com.templatemela.smartpdfreader.fragment.ViewFilesFragment;
import com.templatemela.smartpdfreader.fragment.ZipToPdfFragment;
import com.templatemela.smartpdfreader.fragment.texttopdf.TextToPdfFragment;
import com.templatemela.smartpdfreader.language.LanguageSelectionActivity;
import com.templatemela.smartpdfreader.util.AdsUtils;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.FeedbackUtils;
import com.templatemela.smartpdfreader.util.FragmentUtils;

import java.util.Objects;

public class FragmentManagement implements IFragmentManagement {
    private final FragmentActivity mContext;
    private boolean mDoubleBackToExitPressedOnce = false;
    private final FeedbackUtils mFeedbackUtils;
    private final FragmentUtils mFragmentUtils;
    private final NavigationView mNavigationView;

    public FragmentManagement(FragmentActivity fragmentActivity, NavigationView navigationView) {
        mContext = fragmentActivity;
        mNavigationView = navigationView;
        mFeedbackUtils = new FeedbackUtils(fragmentActivity);
        mFragmentUtils = new FragmentUtils(fragmentActivity);
    }

    public void favouritesFragmentOption() {
        Fragment findFragmentById = mContext.getSupportFragmentManager().findFragmentById(R.id.content);
        FragmentTransaction replace = mContext.getSupportFragmentManager().beginTransaction().replace(R.id.content, new FavouritesFragment());
        if (!(findFragmentById instanceof HomeFragment)) {
            replace.addToBackStack(mFragmentUtils.getFragmentName(findFragmentById));
        }
        replace.commit();
    }

    public Fragment checkForAppShortcutClicked() {
        Fragment homeFragment = new HomeFragment();
        if (mContext.getIntent().getAction() != null) {
            String action = mContext.getIntent().getAction();
            Objects.requireNonNull(action);
            String action1 = action;
            action1.hashCode();
            char c = 65535;
            switch (action1.hashCode()) {
                case 49072390:
                    if (action1.equals(Constants.ACTION_MERGE_PDF)) {
                        c = 0;
                        break;
                    }
                    break;
                case 432305787:
                    if (action1.equals(Constants.ACTION_TEXT_TO_PDF)) {
                        c = 1;
                        break;
                    }
                    break;
                case 1915295350:
                    if (action1.equals(Constants.ACTION_SELECT_IMAGES)) {
                        c = 2;
                        break;
                    }
                    break;
                case 1967267522:
                    if (action1.equals(Constants.ACTION_VIEW_FILES)) {
                        c = 3;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    homeFragment = new MergeFilesFragment();
                    setNavigationViewSelection(R.id.nav_merge);
                    break;
                case 1:
                    homeFragment = new TextToPdfFragment();
                    setNavigationViewSelection(R.id.nav_text_to_pdf);
                    break;
                case 2:
                    homeFragment = new ImageToPdfFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.OPEN_SELECT_IMAGES, true);
                    homeFragment.setArguments(bundle);
                    break;
                case 3:
                    homeFragment = new ViewFilesFragment();
                    setNavigationViewSelection(R.id.nav_gallery);
                    break;
                default:
                    homeFragment = new HomeFragment();
                    break;
            }
        }
        if (areImagesReceived()) {
            homeFragment = new ImageToPdfFragment();
        }
        mContext.getSupportFragmentManager().beginTransaction().replace(R.id.content, homeFragment).commit();
        return homeFragment;
    }

    public boolean handleBackPressed() {
        Fragment content = mContext.getSupportFragmentManager().findFragmentById(R.id.content);
        if (content instanceof HomeFragment) {
            return checkDoubleBackPress();
        }
        if (mFragmentUtils.handleFragmentBottomSheetBehavior(content)) {
            return false;
        }
        handleBackStackEntry();
        return false;
    }

    public boolean handleNavigationItemSelected(int i) {
        Fragment fragment = null;
        FragmentManager supportFragmentManager = mContext.getSupportFragmentManager();
        Bundle bundle = new Bundle();
        switch (i) {
            case R.id.nav_about:
                fragment = new AboutUsFragment();
                break;
                case R.id.nav_privacy:
                fragment = new PrivacyPolicyFragment();
                break;
            case R.id.nav_add_images:
                fragment = new AddImagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.ADD_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_add_password:
                fragment = new Add_RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, "Add password");
                fragment.setArguments(bundle);
                break;
            case R.id.nav_add_text:
                fragment = new AddTextFragment();
                break;
            case R.id.nav_add_watermark:
                fragment = new ViewFilesFragment();
                bundle.putInt(Constants.BUNDLE_DATA, 23);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_camera:
                fragment = new ImageToPdfFragment();
                break;
            case R.id.nav_compress_pdf:
                fragment = new Add_RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, "Compress PDF");
                fragment.setArguments(bundle);
                break;
            case R.id.nav_excel_to_pdf:
                fragment = new ExceltoPdfFragment();
                break;
            case R.id.nav_extract_images:
                fragment = new PdfToImageFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.EXTRACT_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_faq:
                fragment = new FAQFragment();
                break;
            case R.id.nav_gallery:
                fragment = new ViewFilesFragment();
                break;
            case R.id.nav_help:
                Intent intent = new Intent(mContext, TM_WelcomeActivity.class);
                intent.putExtra(Constants.SHOW_WELCOME_ACT, true);
                mContext.startActivity(intent);
                break;
            case R.id.nav_history:
                fragment = new HistoryFragment();
                break;
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_invert_pdf:
                fragment = new InvertPdfFragment();
                break;
            case R.id.nav_merge:
                fragment = new MergeFilesFragment();
                break;
            case R.id.nav_pdf_to_images:
                fragment = new PdfToImageFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.PDF_TO_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_qrcode:
                fragment = new QrBarcodeScanFragment();
                break;
            case R.id.nav_rateus:
                mFeedbackUtils.openWebPage("https://play.google.com/store/apps/details?id=" + mContext.getPackageName());
                break;
            case R.id.nav_rearrange_pages:
                fragment = new Add_RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.REORDER_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_remove_duplicate_pages:
                fragment = new RemoveDuplicatePagesFragment();
                break;
            case R.id.nav_remove_pages:
                fragment = new Add_RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.REMOVE_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_rotate_pages:
                fragment = new ViewFilesFragment();
                bundle.putInt(Constants.BUNDLE_DATA, 20);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            case R.id.nav_share:
                mFeedbackUtils.shareApplication();
                break;
            case R.id.nav_split:
                fragment = new SplitFilesFragment();
                break;
            case R.id.nav_text_extract:
                fragment = new ExtractTextFragment();
                break;
            case R.id.nav_text_to_pdf:
                fragment = new TextToPdfFragment();
                break;
            case R.id.nav_zip_to_pdf:
                fragment = new ZipToPdfFragment();
                break;
            case R.id.nav_lang:

                mContext.setTitle(R.string.app_name);
                Intent intent1 = new Intent(mContext, LanguageSelectionActivity.class);
                intent1.putExtra("isFromMain", true);
                AdsUtils.initAd(mContext);
                AdsUtils.loadInterAd(mContext);
                AdsUtils.showInterAd(mContext, intent1);
                break;

            default:
                fragment = null;
        }
        if (fragment != null) {
            try {
                AdsUtils.initAd(mContext);
                AdsUtils.loadInterAd(mContext);
                AdsUtils.showAdWithChangeFragment(mContext, supportFragmentManager, fragment);//supportFragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean checkDoubleBackPress() {
        if (mDoubleBackToExitPressedOnce) {
            return true;
        }
        mDoubleBackToExitPressedOnce = true;
        Toast.makeText(mContext, R.string.confirm_exit_message, Toast.LENGTH_SHORT).show();
        return false;
    }

    private void handleBackStackEntry() {
        int backStackEntryCount = mContext.getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 0) {
            mContext.setTitle(mContext.getSupportFragmentManager().getBackStackEntryAt(backStackEntryCount - 1).getName());
            mContext.getSupportFragmentManager().popBackStack();
            return;
        }
        mContext.getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();
        mContext.setTitle(R.string.app_name);
        setNavigationViewSelection(R.id.nav_home);
    }


    private boolean areImagesReceived() {
        String type = mContext.getIntent().getType();
        return type != null && type.startsWith("image/");
    }

    private void setNavigationViewSelection(int i) {
        mNavigationView.setCheckedItem(i);
    }
}

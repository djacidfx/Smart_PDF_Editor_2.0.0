package com.templatemela.smartpdfreader.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.activity.MainActivity;
import com.templatemela.smartpdfreader.activity.TM_FavouritesActivity;
import com.templatemela.smartpdfreader.customviews.MyCardView;
import com.templatemela.smartpdfreader.fragment.texttopdf.TextToPdfFragment;
import com.templatemela.smartpdfreader.model.HomePageItem;
import com.templatemela.smartpdfreader.util.CommonCodeUtils;
import com.templatemela.smartpdfreader.util.Constants;

import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FavouritesFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {
    @BindView(R.id.favourites)
    LottieAnimationView favouritesAnimation;
    @BindView(R.id.favourites_text)
    TextView favouritesText;
    private Activity mActivity;
    private boolean mDoesFavouritesExist;
    private Map<Integer, HomePageItem> mFragmentPositionMap;
    private SharedPreferences mSharedpreferences;
    @BindView(R.id.add_images_fav)
    MyCardView pref_add_images;
    @BindView(R.id.add_password_fav)
    MyCardView pref_add_password;
    @BindView(R.id.add_text_fav)
    MyCardView pref_add_text;
    @BindView(R.id.add_watermark_fav)
    MyCardView pref_add_watermark;
    @BindView(R.id.compress_pdf_fav)
    MyCardView pref_compress;
    @BindView(R.id.excel_to_pdf_fav)
    MyCardView pref_excel_to_pdf;
    @BindView(R.id.extract_images_fav)
    MyCardView pref_extract_img;
    @BindView(R.id.extract_text_fav)
    MyCardView pref_extract_txt;
    @BindView(R.id.view_history_fav)
    MyCardView pref_history;
    @BindView(R.id.images_to_pdf_fav)
    MyCardView pref_img_to_pdf;
    @BindView(R.id.invert_pdf_fav)
    MyCardView pref_invert_pdf;
    @BindView(R.id.merge_pdf_fav)
    MyCardView pref_merge_pdf;
    @BindView(R.id.pdf_to_images_fav)
    MyCardView pref_pdf_to_img;
    @BindView(R.id.qr_barcode_to_pdf_fav)
    MyCardView pref_qr_barcode;
    @BindView(R.id.remove_duplicates_pages_pdf_fav)
    MyCardView pref_rem_dup_pages;
    @BindView(R.id.remove_password_fav)
    MyCardView pref_rem_pass;
    @BindView(R.id.remove_pages_fav)
    MyCardView pref_remove_pages;
    @BindView(R.id.rearrange_pages_fav)
    MyCardView pref_reorder_pages;
    @BindView(R.id.rotate_pages_fav)
    MyCardView pref_rot_pages;
    @BindView(R.id.split_pdf_fav)
    MyCardView pref_split_pdf;
    @BindView(R.id.text_to_pdf_fav)
    MyCardView pref_text_to_pdf;
    @BindView(R.id.view_files_fav)
    MyCardView pref_view_files;
    @BindView(R.id.zip_to_pdf_fav)
    MyCardView pref_zip_to_pdf;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.favourites_fragment, viewGroup, false);
        ButterKnife.bind(this, inflate);
        mSharedpreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mSharedpreferences.registerOnSharedPreferenceChangeListener(this);
        initializeValues();
        setHasOptionsMenu(true);
        return inflate;
    }

    private void initializeValues() {
        mDoesFavouritesExist = false;
        checkFavourites(mSharedpreferences);
        mFragmentPositionMap = CommonCodeUtils.getInstance().fillNavigationItemsMap(false);
        pref_img_to_pdf.setOnClickListener(this);
        pref_text_to_pdf.setOnClickListener(this);
        pref_qr_barcode.setOnClickListener(this);
        pref_view_files.setOnClickListener(this);
        pref_history.setOnClickListener(this);
        pref_extract_txt.setOnClickListener(this);
        pref_add_text.setOnClickListener(this);
        pref_split_pdf.setOnClickListener(this);
        pref_merge_pdf.setOnClickListener(this);
        pref_compress.setOnClickListener(this);
        pref_remove_pages.setOnClickListener(this);
        pref_reorder_pages.setOnClickListener(this);
        pref_extract_img.setOnClickListener(this);
        pref_pdf_to_img.setOnClickListener(this);
        pref_add_password.setOnClickListener(this);
        pref_rem_pass.setOnClickListener(this);
        pref_rot_pages.setOnClickListener(this);
        pref_add_watermark.setOnClickListener(this);
        pref_add_images.setOnClickListener(this);
        pref_rem_dup_pages.setOnClickListener(this);
        pref_invert_pdf.setOnClickListener(this);
        pref_excel_to_pdf.setOnClickListener(this);
        pref_zip_to_pdf.setOnClickListener(this);
    }

    @OnClick({R.id.fav_add_fab})
    public void onAddFavouriteButtonClicked() {
        startActivity(new Intent(getContext(), TM_FavouritesActivity.class));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_favourites_item).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    private void checkFavourites(SharedPreferences sharedPreferences) {
        mSharedpreferences = sharedPreferences;
        viewVisibility(pref_img_to_pdf, Constants.IMAGE_TO_PDF_KEY);
        viewVisibility(pref_text_to_pdf, Constants.TEXT_TO_PDF_KEY);
        viewVisibility(pref_qr_barcode, Constants.QR_BARCODE_KEY);
        viewVisibility(pref_view_files, Constants.VIEW_FILES_KEY);
        viewVisibility(pref_history, "History");
        viewVisibility(pref_add_text, Constants.ADD_TEXT_KEY);
        viewVisibility(pref_add_password, "Add password");
        viewVisibility(pref_rem_pass, "Remove password");
        viewVisibility(pref_rot_pages, Constants.ROTATE_PAGES_KEY);
        viewVisibility(pref_add_watermark, Constants.ADD_WATERMARK_KEY);
        viewVisibility(pref_add_images, Constants.ADD_IMAGES_KEY);
        viewVisibility(pref_merge_pdf, Constants.MERGE_PDF_KEY);
        viewVisibility(pref_split_pdf, Constants.SPLIT_PDF_KEY);
        viewVisibility(pref_invert_pdf, Constants.INVERT_PDF_KEY);
        viewVisibility(pref_compress, "Compress PDF");
        viewVisibility(pref_rem_dup_pages, Constants.REMOVE_DUPLICATE_PAGES_KEY);
        viewVisibility(pref_remove_pages, Constants.REMOVE_PAGES_KEY);
        viewVisibility(pref_reorder_pages, Constants.REORDER_PAGES_KEY);
        viewVisibility(pref_extract_txt, Constants.EXTRACT_TEXT_KEY);
        viewVisibility(pref_extract_img, Constants.EXTRACT_IMAGES_KEY);
        viewVisibility(pref_pdf_to_img, Constants.PDF_TO_IMAGES_KEY);
        viewVisibility(pref_excel_to_pdf, Constants.EXCEL_TO_PDF_KEY);
        viewVisibility(pref_zip_to_pdf, Constants.ZIP_TO_PDF_KEY);
        if (!mDoesFavouritesExist) {
            favouritesAnimation.setVisibility(View.VISIBLE);
            favouritesText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    private void viewVisibility(View view, String val) {
        if (mSharedpreferences.getBoolean(val, false)) {
            view.setVisibility(View.VISIBLE);
            mDoesFavouritesExist = true;
            favouritesAnimation.setVisibility(View.GONE);
            favouritesText.setVisibility(View.GONE);
            return;
        }
        view.setVisibility(View.GONE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        checkFavourites(sharedPreferences);
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        Fragment fragment2;
        FragmentManager fragmentManager = getFragmentManager();
        Bundle bundle = new Bundle();
        setTitleFragment(mFragmentPositionMap.get(Integer.valueOf(view.getId())).getTitleString());
        switch (view.getId()) {
            case R.id.add_images_fav:
                fragment2 = new AddImagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.ADD_IMAGES);
                fragment2.setArguments(bundle);
                break;
            case R.id.add_password_fav:
                fragment2 = new Add_RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, "Add password");
                fragment2.setArguments(bundle);
                break;
            case R.id.add_text_fav:
                fragment = new AddTextFragment();
                break;
            case R.id.add_watermark_fav:
                fragment2 = new ViewFilesFragment();
                bundle.putInt(Constants.BUNDLE_DATA, 23);
                fragment2.setArguments(bundle);
                break;
            case R.id.compress_pdf_fav:
                fragment2 = new Add_RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, "Compress PDF");
                fragment2.setArguments(bundle);
                break;
            case R.id.excel_to_pdf_fav:
                fragment = new ExceltoPdfFragment();
                break;
            case R.id.extract_images_fav:
                fragment2 = new PdfToImageFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.EXTRACT_IMAGES);
                fragment2.setArguments(bundle);
                break;
            case R.id.extract_text_fav:
                fragment = new ExtractTextFragment();
                break;
            case R.id.images_to_pdf_fav:
                fragment = new ImageToPdfFragment();
                break;
            case R.id.invert_pdf_fav:
                fragment = new InvertPdfFragment();
                break;
            case R.id.merge_pdf_fav:
                fragment = new MergeFilesFragment();
                break;
            case R.id.pdf_to_images_fav:
                fragment2 = new PdfToImageFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.PDF_TO_IMAGES);
                fragment2.setArguments(bundle);
                break;
            case R.id.qr_barcode_to_pdf_fav:
                fragment = new QrBarcodeScanFragment();
                break;
            case R.id.rearrange_pages_fav:
                fragment2 = new Add_RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.REORDER_PAGES);
                fragment2.setArguments(bundle);
                break;
            case R.id.remove_duplicates_pages_pdf_fav:
                fragment = new RemoveDuplicatePagesFragment();
                break;
            case R.id.remove_pages_fav:
                fragment2 = new Add_RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.REMOVE_PAGES);
                fragment2.setArguments(bundle);
                break;
            case R.id.remove_password_fav:
                fragment2 = new Add_RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, "Remove password");
                fragment2.setArguments(bundle);
                break;
            case R.id.rotate_pages_fav:
                fragment2 = new ViewFilesFragment();
                bundle.putInt(Constants.BUNDLE_DATA, 20);
                fragment2.setArguments(bundle);
                break;
            case R.id.split_pdf_fav:
                fragment = new SplitFilesFragment();
                break;
            case R.id.text_to_pdf_fav:
                fragment = new TextToPdfFragment();
                break;
            case R.id.view_files_fav:
                fragment = new ViewFilesFragment();
                break;
            case R.id.view_history_fav:
                fragment = new HistoryFragment();
                break;
            case R.id.zip_to_pdf_fav:
                fragment = new ZipToPdfFragment();
                break;
            default:
                fragment = null;
                break;
        }
        if (fragment != null && fragmentManager != null) {
            try {
                ((MainActivity) mActivity).setNavigationViewSelection(Objects.requireNonNull(mFragmentPositionMap.get(view.getId())).getNavigationItemId());
                fragmentManager.beginTransaction().replace(R.id.content, fragment).addToBackStack(getString(R.string.favourites)).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setTitleFragment(int i) {
        if (i != 0) {
            mActivity.setTitle(i);
        }
    }
}

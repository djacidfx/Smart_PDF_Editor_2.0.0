package com.templatemela.smartpdfreader.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.activity.MainActivity;
import com.templatemela.smartpdfreader.adapter.RecentListAdapter;
import com.templatemela.smartpdfreader.ads.AdsService;
import com.templatemela.smartpdfreader.customviews.MyCardView;
import com.templatemela.smartpdfreader.fragment.texttopdf.TextToPdfFragment;
import com.templatemela.smartpdfreader.model.HomePageItem;
import com.templatemela.smartpdfreader.util.AdsUtils;
import com.templatemela.smartpdfreader.util.CommonCodeUtils;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.RecentUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONException;

public class HomeFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.add_images)
    MyCardView addImages;
    @BindView(R.id.add_password)
    MyCardView addPassword;
    @BindView(R.id.add_text)
    MyCardView addText;
    @BindView(R.id.add_watermark)
    MyCardView addWatermark;
    @BindView(R.id.compress_pdf)
    MyCardView compressPdf;
    @BindView(R.id.excel_to_pdf)
    MyCardView excelToPdf;
    @BindView(R.id.extract_images)
    MyCardView extractImages;
    @BindView(R.id.extract_text)
    MyCardView extractText;
    Fragment fragment = null;
    @BindView(R.id.images_to_pdf)
    MyCardView imagesToPdf;
    @BindView(R.id.invert_pdf)
    MyCardView invertPdf;

    private Activity mActivity;
    private RecentListAdapter mAdapter;
    private Map<Integer, HomePageItem> mFragmentPositionMap;
    @BindView(R.id.pdf_to_images)
    MyCardView mPdfToImages;
    @BindView(R.id.merge_pdf)
    MyCardView mergePdf;
    @BindView(R.id.qr_barcode_to_pdf)
    MyCardView qrbarcodeToPdf;
    @BindView(R.id.rearrange_pages)
    MyCardView rearrangePages;
    @BindView(R.id.ll_recent)
    ViewGroup recentLayout;
    @BindView(R.id.recent_list)
    RecyclerView recentList;
    @BindView(R.id.remove_duplicates_pages_pdf)
    MyCardView removeDuplicatePages;
    @BindView(R.id.remove_pages)
    MyCardView removePages;
    @BindView(R.id.remove_password)
    MyCardView removePassword;
    @BindView(R.id.rotate_pages)
    MyCardView rotatePdf;
    @BindView(R.id.split_pdf)
    MyCardView splitPdf;
    @BindView(R.id.text_to_pdf)
    MyCardView textToPdf;
    @BindView(R.id.view_files)
    MyCardView viewFiles;
    @BindView(R.id.view_history)
    MyCardView viewHistory;
    @BindView(R.id.zip_to_pdf)
    MyCardView zipToPdf;


    @BindView(R.id.recent_lbl)
    TextView recentLabel;

    @BindView(R.id.txtNewPdf)
    TextView txtNewPdf;

    @BindView(R.id.textViewPdf)
    TextView textViewPdf ;

    @BindView(R.id.enhance_pdf)
    TextView enhance_pdf ;


    @BindView(R.id.modify_pdf)
    TextView modify_pdf ;

    @BindView(R.id.txtMore)
    TextView txtMore ;



    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        imagesToPdf.setOptionText(R.string.images_to_pdf);
        textToPdf.setOptionText(R.string.text_to_pdf);
        qrbarcodeToPdf.setOptionText(R.string.qr_barcode_pdf);
        excelToPdf.setOptionText(R.string.excel_to_pdf);
        viewFiles.setOptionText(R.string.viewFiles);
        viewHistory.setOptionText(R.string.history);
        addPassword.setOptionText(R.string.add_password);
        removePassword.setOptionText(R.string.remove_password);
        removePages.setOptionText(R.string.remove_pages);
        addText.setOptionText(R.string.add_text);
        rotatePdf.setOptionText(R.string.rotate_pages);
        addWatermark.setOptionText(R.string.add_watermark);
        addImages.setOptionText(R.string.add_images);
        mergePdf.setOptionText(R.string.merge_pdf);
        splitPdf.setOptionText(R.string.split_pdf);
        invertPdf.setOptionText(R.string.invert_pdf);
        compressPdf.setOptionText(R.string.compress_pdf);
        removeDuplicatePages.setOptionText(R.string.remove_duplicate_pages);
        rearrangePages.setOptionText(R.string.reorder_pages);
        extractImages.setOptionText(R.string.extract_images);
        mPdfToImages.setOptionText(R.string.pdf_to_images);
        extractText.setOptionText(R.string.extract_text);
        zipToPdf.setOptionText(R.string.zip_to_pdf);

        txtNewPdf.setText(R.string.create_new_pdfs);
        textViewPdf.setText(R.string.view_pdfs);
        enhance_pdf.setText(R.string.enhance_created_pdfs);
        modify_pdf.setText(R.string.modify_existing_pdfs);
        txtMore.setText(R.string.more_options);
        recentLabel.setText(R.string.lbl_recently_used);
        try {
            LinkedHashMap<String, Map<String, String>> list = RecentUtil.getInstance().getList(PreferenceManager.getDefaultSharedPreferences(mActivity));
            if (!list.isEmpty()) {
                recentLabel.setVisibility(View.VISIBLE);
                recentLayout.setVisibility(View.VISIBLE);
                mAdapter.updateList(new ArrayList(list.keySet()), new ArrayList(list.values()));
                mAdapter.notifyDataSetChanged();
                return;
            }
             recentLabel.setVisibility(View.GONE);
            recentLayout.setVisibility(View.GONE);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter = new RecentListAdapter(this);
        recentList.setAdapter(mAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_home_, viewGroup, false);
        ButterKnife.bind(this, inflate);
        mFragmentPositionMap = CommonCodeUtils.getInstance().fillNavigationItemsMap(true);

        imagesToPdf.setOnClickListener(this);
        qrbarcodeToPdf.setOnClickListener(this);
        textToPdf.setOnClickListener(this);
        viewFiles.setOnClickListener(this);
        viewHistory.setOnClickListener(this);
        splitPdf.setOnClickListener(this);
        mergePdf.setOnClickListener(this);
        compressPdf.setOnClickListener(this);
        removePages.setOnClickListener(this);
        rearrangePages.setOnClickListener(this);
        extractImages.setOnClickListener(this);
        mPdfToImages.setOnClickListener(this);
        addPassword.setOnClickListener(this);
        removePassword.setOnClickListener(this);
        rotatePdf.setOnClickListener(this);
        addWatermark.setOnClickListener(this);
        addImages.setOnClickListener(this);
        removeDuplicatePages.setOnClickListener(this);
        invertPdf.setOnClickListener(this);
        zipToPdf.setOnClickListener(this);
        excelToPdf.setOnClickListener(this);
        extractText.setOnClickListener(this);
        addText.setOnClickListener(this);
        mAdapter = new RecentListAdapter(this);
        recentList.setAdapter(mAdapter);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        AdsService.getInstance().showAdaptiveBannerAd(view.findViewById(R.id.layoutDelete));
//        AdsService.getInstance().showNativeAd(view.findViewById(R.id.adView), R.layout.ad_unified, AdsService.NativeAdType.NATIVE_AD_TYPE_MEDIA);

        AdsService.getInstance().showNativeAdView(view.findViewById(R.id.adView),mActivity);

        try {
            LinkedHashMap<String, Map<String, String>> list = RecentUtil.getInstance().getList(PreferenceManager.getDefaultSharedPreferences(mActivity));
            if (!list.isEmpty()) {
                recentLabel.setVisibility(View.VISIBLE);
                recentLayout.setVisibility(View.VISIBLE);
                mAdapter.updateList(new ArrayList(list.keySet()), new ArrayList(list.values()));
                mAdapter.notifyDataSetChanged();
                return;
            }
            recentLabel.setVisibility(View.GONE);
            recentLayout.setVisibility(View.GONE);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onClick(View view) {
        openFragment(view);
    }



    public void openFragment(View view) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Bundle bundle = new Bundle();
        highlightNavigationDrawerItem(mFragmentPositionMap.get(view.getId()).getNavigationItemId());
        setTitleFragment(mFragmentPositionMap.get(view.getId()).getTitleString());
        HashMap hashMap = new HashMap();
        hashMap.put(String.valueOf(mFragmentPositionMap.get(view.getId()).getTitleString()), String.valueOf(mFragmentPositionMap.get(Integer.valueOf(view.getId())).getmDrawableId()));
        try {
            RecentUtil.getInstance().addFeatureInRecentList(PreferenceManager.getDefaultSharedPreferences(mActivity), view.getId(), hashMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (view.getId()) {
            case R.id.add_images:
                fragment = new AddImagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.ADD_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.add_password:
                fragment = new Add_RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, "Add password");
                fragment.setArguments(bundle);
                break;
            case R.id.add_text:
                fragment = new AddTextFragment();
                break;
            case R.id.add_watermark:
                fragment = new ViewFilesFragment();
                bundle.putInt(Constants.BUNDLE_DATA, 23);
                fragment.setArguments(bundle);
                break;
            case R.id.compress_pdf:
                fragment = new Add_RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, "Compress PDF");
                fragment.setArguments(bundle);
                break;
            case R.id.excel_to_pdf:
                fragment = new ExceltoPdfFragment();
                break;
            case R.id.extract_images:
                fragment = new PdfToImageFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.EXTRACT_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.extract_text:
                fragment = new ExtractTextFragment();
                break;
            case R.id.images_to_pdf:
                fragment = new ImageToPdfFragment();
                break;
            case R.id.invert_pdf:
                fragment = new InvertPdfFragment();
                break;
            case R.id.merge_pdf:
                fragment = new MergeFilesFragment();
                break;
            case R.id.pdf_to_images:
                fragment = new PdfToImageFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.PDF_TO_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.qr_barcode_to_pdf:
                fragment = new QrBarcodeScanFragment();
                break;
            case R.id.rearrange_pages:
                fragment = new Add_RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.REORDER_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.remove_duplicates_pages_pdf:
                fragment = new RemoveDuplicatePagesFragment();
                break;
            case R.id.remove_pages:
                fragment = new Add_RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, Constants.REMOVE_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.remove_password:
                fragment = new Add_RemovePagesFragment();
                bundle.putString(Constants.BUNDLE_DATA, "Remove password");
                fragment.setArguments(bundle);
                break;
            case R.id.rotate_pages:
                fragment = new ViewFilesFragment();
                bundle.putInt(Constants.BUNDLE_DATA, 20);
                fragment.setArguments(bundle);
                break;
            case R.id.split_pdf:
                fragment = new SplitFilesFragment();
                break;
            case R.id.text_to_pdf:
                fragment = new TextToPdfFragment();
                break;
            case R.id.view_files:
                fragment = new ViewFilesFragment();
                break;
            case R.id.view_history:
                fragment = new HistoryFragment();
                break;
            case R.id.zip_to_pdf:
                fragment = new ZipToPdfFragment();
                break;
        }
        try {
            if (fragment != null && fragmentManager != null) {

                AdsUtils.initAd(getContext());
                AdsUtils.loadInterAd(getContext());
                AdsUtils.showAdWithChangeFragment(getContext(),fragmentManager,fragment);
            //    fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }


    private void highlightNavigationDrawerItem(int i) {
        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).setNavigationViewSelection(i);
        }
    }

    private void setTitleFragment(int i) {
        if (i != 0) {
            mActivity.setTitle(i);
        }
    }
}

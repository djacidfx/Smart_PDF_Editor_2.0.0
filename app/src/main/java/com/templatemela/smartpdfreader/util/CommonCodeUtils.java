package com.templatemela.smartpdfreader.util;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.adapter.ExtractImagesAdapter;
import com.templatemela.smartpdfreader.adapter.MergeFilesAdapter;
import com.templatemela.smartpdfreader.model.HomePageItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommonCodeUtils {
    public void populateUtil(Activity activity, ArrayList<String> arrayList, MergeFilesAdapter.OnClickListener onClickListener, RelativeLayout relativeLayout, LottieAnimationView lottieAnimationView, RecyclerView recyclerView) {
        if (arrayList == null || arrayList.size() == 0) {
            relativeLayout.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            MergeFilesAdapter mergeFilesAdapter = new MergeFilesAdapter(activity, arrayList, false, onClickListener,false);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setAdapter(mergeFilesAdapter);
            recyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(activity));
        }
        lottieAnimationView.setVisibility(View.GONE);
    }





    public void updateView(Activity activity, int i, ArrayList<String> arrayList, TextView textView, LinearLayout linearLayout, RecyclerView recyclerView, ExtractImagesAdapter.OnFileItemClickedListener onFileItemClickedListener) {
        if (i == 0) {
            StringUtils.getInstance().showSnackbar(activity, R.string.extract_images_failed);
            return;
        }
        String format = String.format(activity.getString(R.string.extract_images_success), i);
        StringUtils.getInstance().showSnackbar(activity, format);
        textView.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        ExtractImagesAdapter extractImagesAdapter = new ExtractImagesAdapter(activity, arrayList, onFileItemClickedListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        textView.setText(format);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(extractImagesAdapter);
        recyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(activity));
    }

    public void closeBottomSheetUtil(BottomSheetBehavior bottomSheetBehavior) {
        if (checkSheetBehaviourUtil(bottomSheetBehavior)) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public boolean checkSheetBehaviourUtil(BottomSheetBehavior bottomSheetBehavior) {
        return bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }

    public Map<Integer, HomePageItem> fillNavigationItemsMap(boolean isFav) {
        HashMap hashMap = new HashMap();
        hashMap.put(isFav ? R.id.images_to_pdf : R.id.images_to_pdf_fav, new HomePageItem(R.id.nav_camera, R.drawable.ic_image_to_pdf, R.string.images_to_pdf));
        hashMap.put(isFav ? R.id.qr_barcode_to_pdf : R.id.qr_barcode_to_pdf_fav, new HomePageItem(R.id.nav_qrcode, R.drawable.ic_qr_barcode, R.string.qr_barcode_pdf));
        hashMap.put(isFav ? R.id.excel_to_pdf : R.id.excel_to_pdf_fav, new HomePageItem(R.id.nav_excel_to_pdf, R.drawable.ic_excel_to_pdf, R.string.excel_to_pdf));
        hashMap.put(isFav ? R.id.view_files : R.id.view_files_fav, new HomePageItem(R.id.nav_gallery, R.drawable.ic_view_files, R.string.viewFiles));
        hashMap.put(isFav ? R.id.rotate_pages : R.id.rotate_pages_fav, new HomePageItem(R.id.nav_rotate_pages, R.drawable.ic_rotate_page, R.string.rotate_pages));
        hashMap.put(isFav ? R.id.extract_text : R.id.extract_text_fav, new HomePageItem(R.id.nav_text_extract, R.drawable.ic_extract_text, R.string.extract_text));
        hashMap.put(isFav ? R.id.add_watermark : R.id.add_watermark_fav, new HomePageItem(R.id.nav_add_watermark, R.drawable.ic_add_watermark, R.string.add_watermark));
        hashMap.put(isFav ? R.id.merge_pdf : R.id.merge_pdf_fav, new HomePageItem(R.id.nav_merge, R.drawable.ic_merge_pdf, R.string.merge_pdf));
        hashMap.put(isFav ? R.id.split_pdf : R.id.split_pdf_fav, new HomePageItem(R.id.nav_split, R.drawable.ic_split_pdf, R.string.split_pdf));
        hashMap.put(isFav ? R.id.text_to_pdf : R.id.text_to_pdf_fav, new HomePageItem(R.id.nav_text_to_pdf, R.drawable.ic_text_to_pdf, R.string.text_to_pdf));
        hashMap.put(isFav ? R.id.compress_pdf : R.id.compress_pdf_fav, new HomePageItem(R.id.nav_compress_pdf, R.drawable.ic_compress_pdf, R.string.compress_pdf));
        hashMap.put(isFav ? R.id.remove_pages : R.id.remove_pages_fav, new HomePageItem(R.id.nav_remove_pages, R.drawable.ic_remove_pages, R.string.remove_pages));
        hashMap.put(isFav ? R.id.rearrange_pages : R.id.rearrange_pages_fav, new HomePageItem(R.id.nav_rearrange_pages, R.drawable.ic_reorder_pages, R.string.reorder_pages));
        hashMap.put(isFav ? R.id.extract_images : R.id.extract_images_fav, new HomePageItem(R.id.nav_extract_images, R.drawable.ic_extrace_images, R.string.extract_images));
        hashMap.put(isFav ? R.id.view_history : R.id.view_history_fav, new HomePageItem(R.id.nav_history, R.drawable.ic_history, R.string.history));
        hashMap.put(isFav ? R.id.pdf_to_images : R.id.pdf_to_images_fav, new HomePageItem(R.id.nav_pdf_to_images, R.drawable.ic_pdf_to_image, R.string.pdf_to_images));
        hashMap.put(isFav ? R.id.add_password : R.id.add_password_fav, new HomePageItem(R.id.nav_add_password, R.drawable.ic_add_password, R.string.add_password));
      //  hashMap.put(isFav ? R.id.remove_password : R.id.remove_password_fav, new HomePageItem(R.id.nav_remove_password, R.drawable.ic_remove_password, R.string.remove_password));
        hashMap.put(isFav ? R.id.add_images : R.id.add_images_fav, new HomePageItem(R.id.nav_add_images, R.drawable.ic_add_image, R.string.add_images));
        hashMap.put(isFav ? R.id.remove_duplicates_pages_pdf : R.id.remove_duplicates_pages_pdf_fav, new HomePageItem(R.id.nav_remove_duplicate_pages, R.drawable.ic_remove_duplicate, R.string.remove_duplicate_pages));
        hashMap.put(isFav ? R.id.invert_pdf : R.id.invert_pdf_fav, new HomePageItem(R.id.nav_invert_pdf, R.drawable.ic_invert_pdf, R.string.invert_pdf));
        hashMap.put(isFav ? R.id.zip_to_pdf : R.id.zip_to_pdf_fav, new HomePageItem(R.id.nav_zip_to_pdf, R.drawable.ic_zip_to_pdf, R.string.zip_to_pdf));
        hashMap.put(isFav ? R.id.add_text : R.id.add_text_fav, new HomePageItem(R.id.nav_add_text, R.drawable.ic_text_to_pdf, R.string.add_text));
        return hashMap;
    }

    private static class SingletonHolder {
        static final CommonCodeUtils INSTANCE = new CommonCodeUtils();

        private SingletonHolder() {
        }
    }

    public static CommonCodeUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }
}

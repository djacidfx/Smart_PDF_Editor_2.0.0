package com.templatemela.smartpdfreader.util;

import android.app.Activity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.templatemela.smartpdfreader.interfaces.BottomSheetPopulate;

public class BottomSheetUtils {
    private final Activity mContext;

    public BottomSheetUtils(Activity activity) {
        this.mContext = activity;
    }

    public void showHideSheet(BottomSheetBehavior bottomSheetBehavior) {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void populateBottomSheetWithPDFs(BottomSheetPopulate bottomSheetPopulate) {
         PopulateBottomSheetList.populateBottomFiles(bottomSheetPopulate, new DirectoryUtils(mContext),false);
    }

    public void populateBottomSheetWithPDFsAndText(BottomSheetPopulate bottomSheetPopulate) {
         PopulateBottomSheetList.populateBottomFiles(bottomSheetPopulate, new DirectoryUtils(mContext), true);
    }

    public void populateBottomSheetWithExcelFiles(BottomSheetPopulate bottomSheetPopulate) {
         PopulateBottomSheetListWithExcelFiles.populateExcelFiles(bottomSheetPopulate, new DirectoryUtils(mContext));
    }
}

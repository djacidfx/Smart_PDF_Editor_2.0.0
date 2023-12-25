package com.templatemela.smartpdfreader.util;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.fragment.Add_RemovePagesFragment;
import com.templatemela.smartpdfreader.fragment.ExceltoPdfFragment;
import com.templatemela.smartpdfreader.fragment.ExtractTextFragment;
import com.templatemela.smartpdfreader.fragment.HistoryFragment;
import com.templatemela.smartpdfreader.fragment.ImageToPdfFragment;
import com.templatemela.smartpdfreader.fragment.InvertPdfFragment;
import com.templatemela.smartpdfreader.fragment.MergeFilesFragment;
import com.templatemela.smartpdfreader.fragment.PdfToImageFragment;
import com.templatemela.smartpdfreader.fragment.QrBarcodeScanFragment;
import com.templatemela.smartpdfreader.fragment.RemoveDuplicatePagesFragment;
import com.templatemela.smartpdfreader.fragment.SplitFilesFragment;
import com.templatemela.smartpdfreader.fragment.ViewFilesFragment;
import com.templatemela.smartpdfreader.fragment.ZipToPdfFragment;
import com.templatemela.smartpdfreader.fragment.AddImagesFragment;
import com.templatemela.smartpdfreader.fragment.texttopdf.TextToPdfFragment;

public class FragmentUtils {
    private final Context mContext;

    public FragmentUtils(Context context) {
        this.mContext = context;
    }

    public String getFragmentName(Fragment fragment) {
        String string = this.mContext.getString(R.string.app_name);
        if (fragment instanceof ImageToPdfFragment) {
            return this.mContext.getString(R.string.images_to_pdf);
        }
        if (fragment instanceof TextToPdfFragment) {
            return this.mContext.getString(R.string.text_to_pdf);
        }
        if (fragment instanceof QrBarcodeScanFragment) {
            return this.mContext.getString(R.string.qr_barcode_pdf);
        }
        if (fragment instanceof ExceltoPdfFragment) {
            return this.mContext.getString(R.string.excel_to_pdf);
        }
        if (fragment instanceof ViewFilesFragment) {
            return checkViewFilesFragmentCode(fragment.getArguments());
        }
        if (fragment instanceof HistoryFragment) {
            return this.mContext.getString(R.string.history);
        }
        if (fragment instanceof ExtractTextFragment) {
            return this.mContext.getString(R.string.extract_text);
        }
        if (fragment instanceof AddImagesFragment) {
            return this.mContext.getString(R.string.add_images);
        }
        if (fragment instanceof MergeFilesFragment) {
            return this.mContext.getString(R.string.merge_pdf);
        }
        if (fragment instanceof SplitFilesFragment) {
            return this.mContext.getString(R.string.split_pdf);
        }
        if (fragment instanceof InvertPdfFragment) {
            return this.mContext.getString(R.string.invert_pdf);
        }
        if (fragment instanceof RemoveDuplicatePagesFragment) {
            return this.mContext.getString(R.string.remove_duplicate);
        }
        if (fragment instanceof Add_RemovePagesFragment) {
            return fragment.getArguments() != null ? fragment.getArguments().getString(Constants.BUNDLE_DATA) : null;
        } else if (fragment instanceof PdfToImageFragment) {
            return this.mContext.getString(R.string.pdf_to_images);
        } else {
            return fragment instanceof ZipToPdfFragment ? this.mContext.getString(R.string.zip_to_pdf) : string;
        }
    }

    public boolean handleFragmentBottomSheetBehavior(Fragment fragment) {
        if (fragment instanceof InvertPdfFragment) {
            InvertPdfFragment invertPdfFragment = (InvertPdfFragment) fragment;
            boolean checkSheetBehaviour = invertPdfFragment.checkSheetBehaviour();
            if (!checkSheetBehaviour) {
                return false;
            }
            invertPdfFragment.closeBottomSheet();
            return true;
        } else if (fragment instanceof MergeFilesFragment) {
            MergeFilesFragment mergeFilesFragment = (MergeFilesFragment) fragment;
            boolean checkSheetBehaviour2 = mergeFilesFragment.checkSheetBehaviour();
            if (!checkSheetBehaviour2) {
                return false;
            }
            mergeFilesFragment.closeBottomSheet();
            return true;
        } else if (fragment instanceof RemoveDuplicatePagesFragment) {
            RemoveDuplicatePagesFragment removeDuplicatePagesFragment = (RemoveDuplicatePagesFragment) fragment;
            boolean checkSheetBehaviour3 = removeDuplicatePagesFragment.checkSheetBehaviour();
            if (!checkSheetBehaviour3) {
                return false;
            }
            removeDuplicatePagesFragment.closeBottomSheet();
            return true;
        } else if (fragment instanceof Add_RemovePagesFragment) {
            Add_RemovePagesFragment add_removePagesFragment = (Add_RemovePagesFragment) fragment;
            boolean checkSheetBehaviour4 = add_removePagesFragment.checkSheetBehaviour();
            if (!checkSheetBehaviour4) {
                return false;
            }
            add_removePagesFragment.closeBottomSheet();
            return true;
        } else if (fragment instanceof AddImagesFragment) {
            AddImagesFragment addImagesFragment = (AddImagesFragment) fragment;
            boolean checkSheetBehaviour5 = addImagesFragment.checkSheetBehaviour();
            if (!checkSheetBehaviour5) {
                return false;
            }
            addImagesFragment.closeBottomSheet();
            return true;
        } else if (fragment instanceof PdfToImageFragment) {
            PdfToImageFragment pdfToImageFragment = (PdfToImageFragment) fragment;
            boolean checkSheetBehaviour6 = pdfToImageFragment.checkSheetBehaviour();
            if (!checkSheetBehaviour6) {
                return false;
            }
            pdfToImageFragment.closeBottomSheet();
            return true;
        } else if (!(fragment instanceof SplitFilesFragment)) {
            return false;
        } else {
            SplitFilesFragment splitFilesFragment = (SplitFilesFragment) fragment;
            boolean checkSheetBehaviour7 = splitFilesFragment.checkSheetBehaviour();
            if (!checkSheetBehaviour7) {
                return false;
            }
            splitFilesFragment.closeBottomSheet();
            return true;
        }
    }

    private String checkViewFilesFragmentCode(Bundle bundle) {
        if (bundle != null) {
            int i = bundle.getInt(Constants.BUNDLE_DATA);
            if (i == 20) {
                return Constants.ROTATE_PAGES_KEY;
            }
            if (i == 23) {
                return Constants.ADD_WATERMARK_KEY;
            }
        }
        return this.mContext.getString(R.string.viewFiles);
    }
}

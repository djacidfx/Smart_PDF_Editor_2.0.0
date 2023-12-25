package com.templatemela.smartpdfreader.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.templatemela.smartpdfreader.adapter.MergeFilesAdapter;
import com.templatemela.smartpdfreader.adapter.ViewFilesAdapter;
import com.templatemela.smartpdfreader.interfaces.EmptyStateChangeListener;
import com.templatemela.smartpdfreader.model.PDFFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PopulateListUtil {

    public static final Handler mHandler = new Handler(Looper.getMainLooper());

    public static void PopulateList(ViewFilesAdapter viewFilesAdapter, EmptyStateChangeListener emptyStateChangeListener, DirectoryUtils directoryUtils, int mCurrentSortingIndex, String mQuery) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            populateListView(viewFilesAdapter, emptyStateChangeListener, directoryUtils, mCurrentSortingIndex, mQuery);
            handler.post(() -> {
                //UI Thread work here
            });
        });
    }

    public static void populateListView(ViewFilesAdapter mAdapter, EmptyStateChangeListener mEmptyStateChangeListener, DirectoryUtils mDirectoryUtils, int mCurrentSortingIndex, String mQuery) {
        ArrayList<File> arrayList;
        if (TextUtils.isEmpty(mQuery)) {
            arrayList = mDirectoryUtils.getPdfFromOtherDirectories();
        } else {
            arrayList = mDirectoryUtils.searchPDF(mQuery);
        }
        if (arrayList == null) {
            mHandler.post(mEmptyStateChangeListener::showNoPermissionsView);
        } else if (arrayList.size() == 0) {
            mHandler.post(() -> mEmptyStateChangeListener.setEmptyStateVisible());
            mHandler.post(() -> mAdapter.setData(null));
        } else {

            mHandler.post(mEmptyStateChangeListener::setEmptyStateInvisible);
            FileSortUtils.getInstance().performSortOperation(mCurrentSortingIndex, arrayList);
            List<PDFFile> pdfFilesWithEncryptionStatus = getPdfFilesWithEncryptionStatus(arrayList, mAdapter);
            mHandler.post(() -> mEmptyStateChangeListener.hideNoPermissionsView());
            mHandler.post(() -> mAdapter.setData(pdfFilesWithEncryptionStatus));
            mHandler.post(() -> mEmptyStateChangeListener.filesPopulated());
        }
    }

    public static void populateUtil(Activity activity, MergeFilesAdapter.OnClickListener onClickListener, DirectoryUtils mDirectoryUtils, RelativeLayout relativeLayout, LottieAnimationView lottieAnimationView, RecyclerView recyclerView) {
        ArrayList<String> arrayList1 = mDirectoryUtils.getPdfList();

        if (arrayList1 == null || arrayList1.size() == 0) {
            relativeLayout.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            MergeFilesAdapter mergeFilesAdapter = new MergeFilesAdapter(activity, arrayList1, false, onClickListener,true);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setAdapter(mergeFilesAdapter);
            recyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(activity));
        }
        lottieAnimationView.setVisibility(View.GONE);
    }





    public static List<PDFFile> getPdfFilesWithEncryptionStatus(List<File> list, ViewFilesAdapter mAdapter) {
        ArrayList arrayList = new ArrayList(list.size());
        for (File next : list) {
            arrayList.add(new PDFFile(next, mAdapter.getPDFUtils().isPDFEncrypted(next.getPath())));
        }
        return arrayList;
    }
}

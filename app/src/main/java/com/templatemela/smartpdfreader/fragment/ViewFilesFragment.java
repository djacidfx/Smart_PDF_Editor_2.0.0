package com.templatemela.smartpdfreader.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.activity.MainActivity;
import com.templatemela.smartpdfreader.adapter.ViewFilesAdapter;
import com.templatemela.smartpdfreader.ads.AdsService;
import com.templatemela.smartpdfreader.interfaces.EmptyStateChangeListener;
import com.templatemela.smartpdfreader.interfaces.ItemSelectedListener;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.DialogUtils;
import com.templatemela.smartpdfreader.util.DirectoryUtils;
import com.templatemela.smartpdfreader.util.FileSortUtils;
import com.templatemela.smartpdfreader.util.MergeHelper;
import com.templatemela.smartpdfreader.util.PermissionsUtils;
import com.templatemela.smartpdfreader.util.PopulateListUtil;
import com.templatemela.smartpdfreader.util.StringUtils;
import com.templatemela.smartpdfreader.util.ViewFilesDividerItemDecoration;

import java.io.File;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewFilesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, EmptyStateChangeListener, ItemSelectedListener {
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 10;
    @BindView(R.id.emptyStatusView)
    ConstraintLayout emptyView;

    private Activity mActivity;
    private AlertDialog.Builder mAlertDialogBuilder;
    private int mCountFiles = 0;
    private int mCurrentSortingIndex;
    private DirectoryUtils mDirectoryUtils;
    private boolean mIsAllFilesSelected = false;
    private boolean mIsMergeRequired = false;
    private MergeHelper mMergeHelper;

    public SearchView mSearchView;
    private SharedPreferences mSharedPreferences;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipeView;
    private ViewFilesAdapter mViewFilesAdapter;
    @BindView(R.id.filesRecyclerView)
    RecyclerView mViewFilesListRecyclerView;
    @BindView(R.id.no_permissions_view)
    RelativeLayout noPermissionsLayout;



    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_view_files, viewGroup, false);
        ButterKnife.bind(this, inflate);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        Objects.requireNonNull(FileSortUtils.getInstance());
        mCurrentSortingIndex = mSharedPreferences.getInt(Constants.SORTING_INDEX, 0);
        mViewFilesAdapter = new ViewFilesAdapter(mActivity, null, this, this);
        mAlertDialogBuilder = new AlertDialog.Builder(mActivity).setCancelable(true).setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());
        mViewFilesListRecyclerView.setLayoutManager(new LinearLayoutManager(inflate.getContext()));
        mViewFilesListRecyclerView.setAdapter(mViewFilesAdapter);
        mViewFilesListRecyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(inflate.getContext()));
        mSwipeView.setOnRefreshListener(this);
        if (getArguments() != null) {
            DialogUtils.getInstance().showFilesInfoDialog(mActivity, getArguments().getInt(Constants.BUNDLE_DATA));
        }
        checkIfListEmpty();
        mMergeHelper = new MergeHelper(mActivity, mViewFilesAdapter);

        AdsService.getInstance().showAdaptiveBannerAd(inflate.findViewById(R.id.layoutDelete));


        return inflate;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        MenuItem menuItem;
        super.onCreateOptionsMenu(menu, menuInflater);
        boolean isVisible = true;
        if (!mIsMergeRequired) {
            menuInflater.inflate(R.menu.activity_view_files_actions, menu);
            MenuItem findItem = menu.findItem(R.id.action_search);
            menuItem = menu.findItem(R.id.select_all);
            mSearchView = (SearchView) findItem.getActionView();
            mSearchView.setQueryHint(getString(R.string.search_hint));
            mSearchView.setSubmitButtonEnabled(true);
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    setDataForQueryChange(query);
                    mSearchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    setDataForQueryChange(newText);
                    return true;
                }
            });
            mSearchView.setOnCloseListener(() -> {
                populatePdfList(null);
                return false;
            });
            mSearchView.setIconifiedByDefault(true);
        } else {
            menuInflater.inflate(R.menu.activity_view_files_actions_if_selected, menu);
            MenuItem item_merge = menu.findItem(R.id.item_merge);
            if (mCountFiles <= 1) {
                isVisible = false;
            }
            item_merge.setVisible(isVisible);
            menuItem = menu.findItem(R.id.select_all);
        }
        if (mIsAllFilesSelected) {
            menuItem.setIcon(R.drawable.ic_check_box_white_24dp);
        }
    }

    public void setDataForQueryChange(String query) {
        populatePdfList(query);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId != R.id.select_all) {
            switch (itemId) {
                case R.id.item_delete:
                    if (!mViewFilesAdapter.areItemsSelected()) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_no_pdfs_selected);
                    } else {
                        deleteFiles();
                    }
                    break;
                case R.id.item_merge:
                    if (mViewFilesAdapter.getItemCount() > 1) {
                        mMergeHelper.mergeFiles();
                        break;
                    }
                    break;
                case R.id.item_share:
                    if (!mViewFilesAdapter.areItemsSelected()) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_no_pdfs_selected);
                    } else {
                        mViewFilesAdapter.shareFiles();
                    }
                    break;
                case R.id.item_sort:
                    displaySortDialog();
                    break;
            }
        } else if (mViewFilesAdapter.getItemCount() <= 0) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_no_pdfs_selected);
        } else if (mIsAllFilesSelected) {
            mViewFilesAdapter.unCheckAll();
        } else {
            mViewFilesAdapter.checkAll();
        }
        return true;
    }

    private void deleteFiles() {
        mViewFilesAdapter.deleteFile();
    }

    private void checkIfListEmpty() {
        boolean isEmpty;
        onRefresh();
        File[] listFiles = mDirectoryUtils.getOrCreatePdfDirectory().listFiles();
        if (listFiles == null) {
            showNoPermissionsView();
            return;
        }
        int length = listFiles.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                isEmpty = false;
                break;
            } else if (!listFiles[i].isDirectory()) {
                isEmpty = true;
                break;
            } else {
                i++;
            }
        }
        if (!isEmpty) {
            setEmptyStateVisible();
            mCountFiles = 0;
            updateToolbar();
        }
    }

    @Override
    public void onRefresh() {
        populatePdfList(null);
        mSwipeView.setRefreshing(false);
    }

    private void populatePdfList(String query) {
        PopulateListUtil.PopulateList(mViewFilesAdapter, this, new DirectoryUtils(mActivity), mCurrentSortingIndex, query);
    }

    private void displaySortDialog() {
        mAlertDialogBuilder.setTitle(R.string.sort_by_title).setItems(R.array.sort_options, (dialogInterface, i) ->
                {
                    mCurrentSortingIndex = i;
                    mSharedPreferences.edit().putInt(Constants.SORTING_INDEX, i).apply();
                    populatePdfList(null);
                }
        );
        mAlertDialogBuilder.create().show();
    }

    @Override
    public void setEmptyStateVisible() {
        emptyView.setVisibility(View.VISIBLE);
        noPermissionsLayout.setVisibility(View.GONE);
    }

    @Override
    public void setEmptyStateInvisible() {
        emptyView.setVisibility(View.GONE);
        noPermissionsLayout.setVisibility(View.GONE);
    }

    @Override
    public void showNoPermissionsView() {
        emptyView.setVisibility(View.GONE);
        noPermissionsLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoPermissionsView() {
        noPermissionsLayout.setVisibility(View.GONE);
    }

    public void filesPopulated() {
        if (mIsMergeRequired) {
            mIsMergeRequired = false;
            mIsAllFilesSelected = false;
            mActivity.invalidateOptionsMenu();
        }
    }

    @OnClick({R.id.getStarted})
    public void loadHome() {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();
        mActivity.setTitle(Constants.appName);
        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).setNavigationViewSelection(R.id.nav_home);
        }
    }

    @OnClick({R.id.provide_permissions})
    public void providePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(this, Constants.READ_WRITE_CAMERA_PERMISSIONS, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        if (grantResults.length < 1) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_insufficient_permissions);
        } else if (requestCode != PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT) {
        } else {
            if (grantResults[0] == 0) {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_permissions_given);
                onRefresh();
                return;
            }
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_insufficient_permissions);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        ViewFilesFragment.super.onAttach(context);
        mActivity = (Activity) context;
        mDirectoryUtils = new DirectoryUtils(mActivity);
    }

    @Override
    public void onCreate(Bundle bundle) {
        ViewFilesFragment.super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override
    public void isSelected(Boolean selected, int i) {
        mCountFiles = i;
        updateToolbar();
    }

    private void updateToolbar() {
        String title;
        if (((AppCompatActivity) mActivity).getSupportActionBar() != null) {
            int i = mCountFiles;
            if (i == 0) {
                title = mActivity.getResources().getString(R.string.viewFiles);
            } else {
                title = String.valueOf(i);
            }
            mActivity.setTitle(title);
            int c = mCountFiles;
            boolean isSelect = false;
            mIsMergeRequired = c > 1;
            if (c == mViewFilesAdapter.getItemCount()) {
                isSelect = true;
            }
            mIsAllFilesSelected = isSelect;
            mActivity.invalidateOptionsMenu();
        }
    }
}

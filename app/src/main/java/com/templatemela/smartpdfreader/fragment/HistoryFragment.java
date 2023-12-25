package com.templatemela.smartpdfreader.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.activity.MainActivity;
import com.templatemela.smartpdfreader.adapter.HistoryAdapter;
import com.templatemela.smartpdfreader.database.AppDatabase;
import com.templatemela.smartpdfreader.database.History;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.DialogUtils;
import com.templatemela.smartpdfreader.util.FileUtils;
import com.templatemela.smartpdfreader.util.StringUtils;
import com.templatemela.smartpdfreader.util.ViewFilesDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnClickListener {

    public Activity mActivity;
    @BindView(R.id.emptyStatusView)
    LinearLayout mEmptyStatusLayout;
    private boolean[] mFilterOptionState;
    public HistoryAdapter mHistoryAdapter;
    public List<History> mHistoryList;
    @BindView(R.id.historyRecyclerView)
    RecyclerView mHistoryRecyclerView;
    @BindView(R.id.layoutDelete)
    LinearLayout layoutDelete;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_history, viewGroup, false);
        ButterKnife.bind(this, inflate);
        mFilterOptionState = new boolean[getResources().getStringArray(R.array.filter_options_history).length];
        Arrays.fill(mFilterOptionState, true);
        loadHistory(mActivity, new String[][]{new String[0]});
        layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHistoryList != null && !mHistoryList.isEmpty()) {
                    confirmToDeleteHistory(mHistoryList);
                } else {
                    StringUtils.getInstance().showSnackbar(mActivity, "No history available.");
                }
            }
        });
        return inflate;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_history_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.actionFilterHistory:
                openFilterDialog();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void openFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        String[] filterArr = getResources().getStringArray(R.array.filter_options_history);
        builder.setMultiChoiceItems(filterArr, mFilterOptionState, (dialogInterface, i, b) -> mFilterOptionState[i] = b);
        builder.setTitle(getString(R.string.title_filter_history_dialog));
        builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> setFilter(filterArr));
        builder.setNeutralButton(getString(R.string.select_all), null);
        AlertDialog create = builder.create();
        create.setOnShowListener(dialogInterface -> setAllFilter(create, dialogInterface));
        create.show();
    }


    public void setFilter(String[] filterArr) {
        if (mHistoryList == null || mHistoryList.isEmpty()) {
            StringUtils.getInstance().showSnackbar(mActivity, "No history available.");
            return;
        }
        ArrayList arrayList = new ArrayList();
        int i2 = 0;
        while (true) {
            if (i2 < mFilterOptionState.length) {
                if (mFilterOptionState[i2]) {
                    arrayList.add(filterArr[i2]);
                }
                i2++;
            } else {
                loadHistory(mActivity, new String[][]{(String[]) arrayList.toArray(new String[0])});
                return;
            }
        }
    }

    public void setAllFilter(AlertDialog alertDialog, DialogInterface dialogInterface) {
        alertDialog.getButton(-3).setOnClickListener(view -> {
            Arrays.fill(mFilterOptionState, true);
            ListView listView = alertDialog.getListView();
            for (int i = 0; i < listView.getCount(); i++) {
                listView.setItemChecked(i, true);
            }
            loadHistory(mActivity, new String[][]{new String[0]});
        });
    }


    private void confirmToDeleteHistory(List<History> mHistoryList) {
        DialogUtils.getInstance().createWarningDialog(mActivity, R.string.delete_history_message)
                .onPositive((materialDialog, dialogAction) -> {
                    {
                        deleteHistory(mHistoryList);
                    }
                }).show();
    }

    @OnClick({R.id.getStarted})
    public void loadHome() {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();
        this.mActivity.setTitle(Constants.appName);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setNavigationViewSelection(R.id.nav_home);
        }
    }

    @Override
    public void onItemClick(String path) {
        FileUtils fileUtils = new FileUtils(mActivity);
        if (new File(path).exists()) {
            fileUtils.openFile(path, FileUtils.FileType.e_PDF);
        } else {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.pdf_does_not_exist_message);
        }
    }

    private void loadHistory(Context mContext, String[]... params) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            AppDatabase database = AppDatabase.getDatabase(mActivity.getApplicationContext());
            if (params[0].length == 0) {
                mHistoryList = database.historyDao().getAllHistory();
            } else {
                mHistoryList = database.historyDao().getHistoryByOperationType(params[0]);
            }
            handler.post(() -> {
                //UI Thread work here
                if (mHistoryList == null || mHistoryList.isEmpty()) {
                    mEmptyStatusLayout.setVisibility(View.VISIBLE);
                } else {
                    mEmptyStatusLayout.setVisibility(View.GONE);
                    mHistoryRecyclerView.setVisibility(View.VISIBLE);
                    layoutDelete.setVisibility(View.VISIBLE);
                    mHistoryAdapter = new HistoryAdapter(mActivity, mHistoryList, HistoryFragment.this);
                    mHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                    mHistoryRecyclerView.setAdapter(mHistoryAdapter);
                    mHistoryRecyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(mContext));
                }
            });
        });
    }

    private void deleteHistory(List<History> mHistoryList) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            for (int i = 0; i < mHistoryList.size(); i++) {
                AppDatabase.getDatabase(mActivity.getApplicationContext()).historyDao().deleteHistory(mHistoryList.get(i));
            }

            handler.post(() -> {
                //UI Thread work here
                if (mHistoryAdapter != null) {
                    mHistoryAdapter.deleteHistory();
                }
                mEmptyStatusLayout.setVisibility(View.VISIBLE);
            });
        });
    }
}

package com.templatemela.smartpdfreader.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.preferences.TextToPdfPreferences;

import java.util.HashMap;
import java.util.Map;

public class PageSizeUtils {
    public static String mPageSize;
    private final Context mActivity;
    private final String mDefaultPageSize;
    private final HashMap<Integer, Integer> mPageSizeToString;
    private final TextToPdfPreferences mPreferences;

    public PageSizeUtils(Context context) {
        mActivity = context;
        mPreferences = new TextToPdfPreferences(context);
        mDefaultPageSize = mPreferences.getPageSize();
        mPageSize = mPreferences.getPageSize();
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        mPageSizeToString = hashMap;
        hashMap.put(R.id.page_size_default, R.string.a4);
        hashMap.put(R.id.page_size_legal, R.string.legal);
        hashMap.put(R.id.page_size_executive, R.string.executive);
        hashMap.put(R.id.page_size_ledger, R.string.ledger);
        hashMap.put(R.id.page_size_tabloid, R.string.tabloid);
        hashMap.put(R.id.page_size_letter, R.string.letter);
    }

    private String getPageSize(int id, String size1, String size2) {
        switch (id) {
            case R.id.page_size_a0_a10:
                mPageSize = size1.substring(0, size1.indexOf(" "));
                break;
            case R.id.page_size_b0_b10:
                mPageSize = size2.substring(0, size2.indexOf(" "));
                break;
            default:
                mPageSize = mActivity.getString(mPageSizeToString.get(Integer.valueOf(id)).intValue());
                break;
        }
        return mPageSize;
    }

    public MaterialDialog showPageSizeDialog(boolean isShow) {
        MaterialDialog pageSizeDialog = getPageSizeDialog(isShow);
        View customView = pageSizeDialog.getCustomView();
        RadioGroup rg = customView.findViewById(R.id.radio_group_page_size);
        Spinner spn1 = customView.findViewById(R.id.spinner_page_size_a0_a10);
        Spinner spn2 = customView.findViewById(R.id.spinner_page_size_b0_b10);
        ((RadioButton) customView.findViewById(R.id.page_size_default)).setText(String.format(this.mActivity.getString(R.string.default_page_size), mDefaultPageSize));
        if (isShow) {
            customView.findViewById(R.id.set_as_default).setVisibility(View.GONE);
        }
        if (mPageSize.equals(mDefaultPageSize)) {
            rg.check(R.id.page_size_default);
        } else if (mPageSize.startsWith("A")) {
            rg.check(R.id.page_size_a0_a10);
            spn1.setSelection(Integer.parseInt(mPageSize.substring(1)));
        } else if (mPageSize.startsWith("B")) {
            rg.check(R.id.page_size_b0_b10);
            spn2.setSelection(Integer.parseInt(mPageSize.substring(1)));
        } else {
            Integer key = getKey(mPageSizeToString, mPageSize);
            if (key != null) {
                rg.check(key);
            }
        }
        pageSizeDialog.show();
        return pageSizeDialog;
    }

    private MaterialDialog getPageSizeDialog(boolean isShow) {
        return DialogUtils.getInstance().createCustomDialogWithoutContent((Activity) mActivity, R.string.set_page_size_text)
                .customView(R.layout.set_page_size_dialog, true).onPositive((materialDialog, dialogAction) -> {
                    View customView = materialDialog.getCustomView();
                    mPageSize = getPageSize(((RadioGroup) customView.findViewById(R.id.radio_group_page_size)).getCheckedRadioButtonId(), ((Spinner) customView.findViewById(R.id.spinner_page_size_a0_a10)).getSelectedItem().toString(), ((Spinner) customView.findViewById(R.id.spinner_page_size_b0_b10)).getSelectedItem().toString());
                    CheckBox checkBox = customView.findViewById(R.id.set_as_default);
                    if (isShow || checkBox.isChecked()) {
                        mPreferences.setPageSize(mPageSize);
                    }
                    StringUtils.getInstance().showSnackbar((Activity) mActivity, "Page size saved.");
                }).build();
    }


    private Integer getKey(HashMap<Integer, Integer> hashMap, String key) {
        for (Map.Entry next : hashMap.entrySet()) {
            if (key.equals(mActivity.getString(((Integer) next.getValue()).intValue()))) {
                return (Integer) next.getKey();
            }
        }
        return null;
    }
}

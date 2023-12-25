package com.templatemela.smartpdfreader.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Font;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.adapter.EnhancementOptionsAdapter;
import com.templatemela.smartpdfreader.folder.FolderPicker;
import com.templatemela.smartpdfreader.interfaces.OnItemClickListener;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.DialogUtils;
import com.templatemela.smartpdfreader.util.ImageUtils;
import com.templatemela.smartpdfreader.util.PageSizeUtils;
import com.templatemela.smartpdfreader.util.SettingsOptions;
import com.templatemela.smartpdfreader.util.StringUtils;
import com.templatemela.smartpdfreader.util.ThemeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends Fragment implements OnItemClickListener {
    private Activity mActivity;
    @BindView(R.id.settings_list)
    RecyclerView mEnhancementOptionsRecycleView;
    private SharedPreferences mSharedPreferences;
    @BindView(R.id.storagelocation)
    TextView storageLocation;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_settings, viewGroup, false);
        ButterKnife.bind(this, inflate);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        storageLocation.setText(mSharedPreferences.getString(Constants.STORAGE_LOCATION, StringUtils.getInstance().getDefaultStorageLocation()));
        showSettingsOptions();
        return inflate;
    }

    private void showGoogleInterstitialAds(int i) {
        switch (i) {
            case 0:
                changeCompressImage();
                return;
            case 1:
                setPageSize();
                return;
            case 2:
                editFontSize();
                return;
            case 3:
                changeFontFamily();
                return;
            case 4:
                ImageUtils.getInstance().showImageScaleTypeDialog(mActivity, true);
                return;
            case 5:
                changeMasterPassword();
                return;
            case 6:
                setShowPageNumber();
                return;
                default:
                return;
        }
    }


    @OnClick({R.id.storagelocation})
    public void modifyStorageLocation() {
        startActivityForResult(new Intent(mActivity, FolderPicker.class), 1);
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1 && intent.getExtras() != null) {
            mSharedPreferences.edit().putString(Constants.STORAGE_LOCATION, intent.getExtras().getString("data") + Constants.PATH_SEPERATOR).apply();
            StringUtils.getInstance().showSnackbar(mActivity, R.string.storage_location_modified);
            storageLocation.setText(mSharedPreferences.getString(Constants.STORAGE_LOCATION, StringUtils.getInstance().getDefaultStorageLocation()));
        }
        super.onActivityResult(i, i2, intent);
    }

    private void showSettingsOptions() {
        mEnhancementOptionsRecycleView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mEnhancementOptionsRecycleView.setAdapter(new EnhancementOptionsAdapter(this, SettingsOptions.getEnhancementOptions(mActivity)));
    }

    @Override
    public void onItemClick(int i) {
        showGoogleInterstitialAds(i);
    }

    private void changeMasterPassword() {
        MaterialDialog build = DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity, R.string.change_master_pwd).customView(R.layout.dialog_change_master_pwd, true).onPositive(new MaterialDialog.SingleButtonCallback() {
            public final void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                String obj = ((EditText) materialDialog.getCustomView().findViewById(R.id.value)).getText().toString();
                if (!obj.isEmpty()) {
                    mSharedPreferences.edit().putString(Constants.MASTER_PWD_STRING, obj).apply();
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.password_changed);
                    return;
                }
                StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
            }
        }).build();
        ((TextView) build.getCustomView().findViewById(R.id.content)).setText(String.format(this.mActivity.getString(R.string.current_master_pwd), new Object[]{this.mSharedPreferences.getString(Constants.MASTER_PWD_STRING, Constants.appName)}));
        build.show();
    }

    private void changeCompressImage() {
        MaterialDialog build = DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity, R.string.compression_image_edit).customView(R.layout.compress_image_dialog, true).onPositive((materialDialog, dialogAction) -> compressImage(materialDialog, dialogAction)).build();
        build.getCustomView().findViewById(R.id.cbSetDefault).setVisibility(View.GONE);
        build.show();
    }

    public void compressImage(MaterialDialog materialDialog, DialogAction dialogAction) {
        try {
            int parseInt = Integer.parseInt(String.valueOf(((EditText) materialDialog.getCustomView().findViewById(R.id.quality)).getText()));
            if (parseInt <= 100) {
                if (parseInt >= 0) {
                    SharedPreferences.Editor edit = mSharedPreferences.edit();
                    edit.putInt(Constants.DEFAULT_COMPRESSION, parseInt);
                    edit.apply();
                    showSettingsOptions();
                    return;
                }
            }
            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
        } catch (NumberFormatException exception) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
        }
    }

    private void editFontSize() {
        MaterialDialog build = DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity, R.string.font_size_edit).customView(R.layout.dialog_font_size, true).onPositive((materialDialog, dialogAction) -> setFontSize(materialDialog, dialogAction)).build();
        build.getCustomView().findViewById(R.id.cbSetFontDefault).setVisibility(View.GONE);
        build.show();
    }

    public void setFontSize(MaterialDialog materialDialog, DialogAction dialogAction) {
        try {
            int parseInt = Integer.parseInt(String.valueOf(((EditText) materialDialog.getCustomView().findViewById(R.id.fontInput)).getText()));
            if (parseInt <= 36) {
                if (parseInt >= 0) {
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.font_size_changed);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();
                    edit.putInt(Constants.DEFAULT_FONT_SIZE_TEXT, parseInt);
                    edit.apply();
                    showSettingsOptions();
                    return;
                }
            }
            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
        } catch (NumberFormatException exception) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
        }
    }

    private void changeFontFamily() {
        int ordinal = Font.FontFamily.valueOf(mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT, Constants.DEFAULT_FONT_FAMILY)).ordinal();
        MaterialDialog build = DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity, R.string.font_family_edit).customView(R.layout.dialog_font_family, true).onPositive((materialDialog, dialogAction) -> setFontFamily(materialDialog)).build();
        View customView = build.getCustomView();
        ((RadioButton) ((RadioGroup) customView.findViewById(R.id.radio_group_font_family)).getChildAt(ordinal)).setChecked(true);
        customView.findViewById(R.id.cbSetDefault).setVisibility(View.GONE);
        build.show();
    }

    public void setFontFamily(MaterialDialog materialDialog) {
        View customView = materialDialog.getCustomView();
        String charSequence = ((RadioButton) customView.findViewById(((RadioGroup) customView.findViewById(R.id.radio_group_font_family)).getCheckedRadioButtonId())).getText().toString();
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(Constants.DEFAULT_FONT_FAMILY_TEXT, charSequence);
        edit.apply();
        showSettingsOptions();
    }

    private void setPageSize() {
        new PageSizeUtils(mActivity).showPageSizeDialog(true).setOnDismissListener(dialogInterface -> showSettingsOptions());
    }

    private void setTheme() {
        MaterialDialog build = DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity, R.string.theme_edit).customView(R.layout.dialog_theme_default, true).onPositive((materialDialog, dialogAction) -> {
            View customView = materialDialog.getCustomView();
            ThemeUtils.getInstance().saveTheme(mActivity, ((RadioButton) customView.findViewById(((RadioGroup) customView.findViewById(R.id.radio_group_themes)).getCheckedRadioButtonId())).getText().toString());
            mActivity.recreate();
        }).build();
        ((RadioButton) ((RadioGroup) build.getCustomView().findViewById(R.id.radio_group_themes)).getChildAt(ThemeUtils.getInstance().getSelectedThemePosition(mActivity))).setChecked(true);
        build.show();
    }


    private void setShowPageNumber() {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        int i = mSharedPreferences.getInt(Constants.PREF_PAGE_STYLE_ID, -1);
        RelativeLayout relativeLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.add_pgnum_dialog, null);
        RadioButton rdOp1 = relativeLayout.findViewById(R.id.page_num_opt1);
        RadioButton rdOp2 = relativeLayout.findViewById(R.id.page_num_opt2);
        RadioButton rdOp3 = relativeLayout.findViewById(R.id.page_num_opt3);
        RadioGroup rg1 = relativeLayout.findViewById(R.id.radioGroup);
        CheckBox chkDefault = relativeLayout.findViewById(R.id.set_as_default);
        if (i > 0) {
            chkDefault.setChecked(true);
            rg1.clearCheck();
            rg1.check(i);
        }
        new MaterialDialog.Builder(mActivity).title(R.string.choose_page_number_style)
                .customView(relativeLayout, false).positiveText(R.string.ok).negativeText(R.string.cancel)
                .neutralText(R.string.remove_dialog).onNeutral((materialDialog, dialogAction) -> {
            edit.putInt(Constants.PREF_PAGE_STYLE_ID, -1);
            edit.apply();
        }).onPositive((materialDialog, dialogAction) -> pageNumberDialog(rg1, rdOp1, rdOp2, rdOp3, chkDefault, edit)).build().show();
    }

    static void pageNumberDialog(RadioGroup rg1, RadioButton rdOp1, RadioButton rdOp2, RadioButton rdOp3, CheckBox chkDefault, SharedPreferences.Editor editor) {
        String pageStyle;
        int checkedRadioButtonId = rg1.getCheckedRadioButtonId();
        if (checkedRadioButtonId == rdOp1.getId()) {
            pageStyle = Constants.PG_NUM_STYLE_PAGE_X_OF_N;
        } else if (checkedRadioButtonId == rdOp2.getId()) {
            pageStyle = Constants.PG_NUM_STYLE_X_OF_N;
        } else {
            pageStyle = checkedRadioButtonId == rdOp3.getId() ? Constants.PG_NUM_STYLE_X : null;
        }
        if (chkDefault.isChecked()) {
            editor.putString(Constants.PREF_PAGE_STYLE, pageStyle);
            editor.putInt(Constants.PREF_PAGE_STYLE_ID, checkedRadioButtonId);
            editor.apply();
            return;
        }
        editor.putString(Constants.PREF_PAGE_STYLE, null);
        editor.putInt(Constants.PREF_PAGE_STYLE_ID, -1);
        editor.commit();
    }
}

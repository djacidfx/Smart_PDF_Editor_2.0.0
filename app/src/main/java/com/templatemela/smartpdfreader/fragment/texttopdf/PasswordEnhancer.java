package com.templatemela.smartpdfreader.fragment.texttopdf;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.interfaces.Enhancer;
import com.templatemela.smartpdfreader.model.EnhancementOptionsEntity;
import com.templatemela.smartpdfreader.model.TextToPDFOptions;
import com.templatemela.smartpdfreader.util.DialogUtils;
import com.templatemela.smartpdfreader.util.StringUtils;

public class PasswordEnhancer implements Enhancer {
    private final Activity mActivity;
    private final TextToPDFOptions.Builder mBuilder;
    private final EnhancementOptionsEntity mEnhancementOptionsEntity;
    private TextToPdfContract.View mView;

    PasswordEnhancer(Activity activity, TextToPdfContract.View view, TextToPDFOptions.Builder builder) {
        mActivity = activity;
        mBuilder = builder;
        builder.setPasswordProtected(false);
        mEnhancementOptionsEntity = new EnhancementOptionsEntity(activity, R.drawable.ic_set_password, R.string.set_password);
        mView = view;
    }

    @Override
    public void enhance() {
        MaterialDialog materialDialog = DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity, R.string.set_password)
                .customView(R.layout.custom_dialog, true).neutralText(R.string.remove_dialog).build();
        final MDButton mdButton = materialDialog.getActionButton(DialogAction.POSITIVE);
        MDButton neutralBtn = materialDialog.getActionButton(DialogAction.NEUTRAL);
        EditText editText = materialDialog.getCustomView().findViewById(R.id.password);
        editText.setText(mBuilder.getPassword());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mdButton.setEnabled(charSequence.toString().trim().length() > 4);
            }
        });
        mdButton.setOnClickListener(view -> addPass(editText, materialDialog, view));
        if (StringUtils.getInstance().isNotEmpty(mBuilder.getPassword())) {
            neutralBtn.setOnClickListener(view -> removePass(materialDialog, view));
        }
        materialDialog.show();
        mdButton.setEnabled(false);
    }

    public void addPass(EditText editText, MaterialDialog materialDialog, View view) {
        if (StringUtils.getInstance().isEmpty(editText.getText())) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_password_cannot_be_blank);
        } else if (editText.getText().toString().length() < 4) {
            Toast.makeText(mActivity, "Password can not be less than 4 characters.", Toast.LENGTH_SHORT).show();
        } else if (editText.getText().toString().length() > 8) {
            Toast.makeText(mActivity, "Password can not be greater than 8 characters.", Toast.LENGTH_SHORT).show();
        } else {
            mBuilder.setPassword(editText.getText().toString());
            mBuilder.setPasswordProtected(true);
            onPasswordAdded();
            materialDialog.dismiss();
        }
    }

    public void removePass(MaterialDialog materialDialog, View view) {
        mBuilder.setPassword(null);
        onPasswordRemoved();
        mBuilder.setPasswordProtected(false);
        materialDialog.dismiss();
        StringUtils.getInstance().showSnackbar(mActivity, R.string.password_remove);
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }


    private void onPasswordAdded() {
        mEnhancementOptionsEntity.setImage(mActivity.getResources().getDrawable(R.drawable.baseline_done_24));
        mView.updateView();
    }

    private void onPasswordRemoved() {
        mEnhancementOptionsEntity.setImage(mActivity.getResources().getDrawable(R.drawable.ic_add_password));
        mView.updateView();
    }
}

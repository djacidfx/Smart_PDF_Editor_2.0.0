package com.templatemela.smartpdfreader.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.database.DatabaseHelper;
import com.templatemela.smartpdfreader.interfaces.DataSetChanged;
import com.templatemela.smartpdfreader.interfaces.OnPDFAddRemovePasswordInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class PDFEncryptionUtility {

    public final Activity mContext;
    private final MaterialDialog mDialog;
    private final FileUtils mFileUtils;

    public String mPassword;
    private final SharedPreferences mSharedPrefs;

    public PDFEncryptionUtility(Activity activity) {
        mContext = activity;
        mFileUtils = new FileUtils(activity);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        mDialog = new MaterialDialog.Builder(activity).customView(R.layout.custom_dialog, true).positiveText(android.R.string.ok).negativeText(android.R.string.cancel).build();
    }

    public void setPassword(String path, DataSetChanged dataSetChanged) {
        this.mDialog.setTitle(R.string.set_password);
        final MDButton actionButton = mDialog.getActionButton(DialogAction.POSITIVE);
        ((EditText) mDialog.getCustomView().findViewById(R.id.password)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                actionButton.setEnabled(charSequence.toString().trim().length() > 4);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtils.getInstance().isEmpty(editable)) {
                    StringUtils.getInstance().showSnackbar(mContext, R.string.snackbar_password_cannot_be_blank);
                } else {
                    mPassword = editable.toString();
                }
            }
        });
        mDialog.show();
        actionButton.setEnabled(false);
        actionButton.setOnClickListener(view -> setPass(path, dataSetChanged));
    }

    public void setPass(String name, DataSetChanged dataSetChanged) {
        StringUtils.getInstance().getSnackbarwithAction(mContext, R.string.snackbar_pdfCreated)
                .setAction(R.string.snackbar_viewAction, view -> {
                    try {
                        mFileUtils.openFile(doEncryption(name, mPassword), FileUtils.FileType.e_PDF);
                    } catch (IOException | DocumentException exception) {
                        exception.printStackTrace();
                    }
                }).show();
        if (dataSetChanged != null) {
            dataSetChanged.updateDataset();
        }
        mDialog.dismiss();
    }


    public void setPasswordModule(String name, DataSetChanged dataSetChanged, OnPDFAddRemovePasswordInterface onPDFAddRemovePasswordInterface) {
        mDialog.setTitle(R.string.set_password);
        final MDButton actionButton = mDialog.getActionButton(DialogAction.POSITIVE);
        ((EditText) mDialog.getCustomView().findViewById(R.id.password)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                actionButton.setEnabled(charSequence.toString().trim().length() > 4);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtils.getInstance().isEmpty(editable)) {
                    StringUtils.getInstance().showSnackbar(mContext, R.string.snackbar_password_cannot_be_blank);
                } else {
                    mPassword = editable.toString();
                }
            }
        });
        this.mDialog.show();
        actionButton.setEnabled(false);
        actionButton.setOnClickListener(view -> passModule(name, onPDFAddRemovePasswordInterface, dataSetChanged, view));
    }

    public void passModule(String name, OnPDFAddRemovePasswordInterface onPDFAddRemovePasswordInterface, DataSetChanged dataSetChanged, View view) {
        try {
            String doEncryption = doEncryption(name, mPassword);
            StringUtils.getInstance().getSnackbarwithAction(mContext, R.string.snackbar_pdfCreated)
                    .setAction(R.string.snackbar_viewAction, view1 -> mFileUtils.openFile(doEncryption, FileUtils.FileType.e_PDF)).show();
            onPDFAddRemovePasswordInterface.pdfEnded("_nanda_" + doEncryption, true);
            if (dataSetChanged != null) {
                dataSetChanged.updateDataset();
            }
        } catch (DocumentException | IOException e) {
            onPDFAddRemovePasswordInterface.pdfEnded("", false);
            e.printStackTrace();
            StringUtils.getInstance().showSnackbar(mContext, R.string.cannot_add_password);
        }
        this.mDialog.dismiss();
    }


    private String doEncryption(String name, String password) throws IOException, DocumentException {
        String na = mSharedPrefs.getString(Constants.MASTER_PWD_STRING, Constants.appName);
        String uniqueFileName = mFileUtils.getUniqueFileName(name.replace(mContext.getString(R.string.pdf_ext), mContext.getString(R.string.encrypted_file)));
        PdfReader pdfReader = new PdfReader("file://" + name);
        PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(uniqueFileName));
        pdfStamper.setEncryption(password.getBytes(), na.getBytes(), 2068, 2);
        pdfStamper.close();
        pdfReader.close();
        new DatabaseHelper(mContext).insertRecord(uniqueFileName, this.mContext.getString(R.string.encrypted));
        return uniqueFileName;
    }

     public boolean isPDFEncrypted(String name) {
        try {
            if (new PdfReader(name, this.mContext.getString(R.string.app_name).getBytes()).isEncrypted()) {
                return true;
            }
            StringUtils.getInstance().showSnackbar(mContext, R.string.not_encrypted);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }

    public void removePassword(String path, String name, DataSetChanged dataSetChanged) {

        Log.e("path",path);
        if (isPDFEncrypted(path)) {
            final String[] sArr = new String[1];
            mDialog.setTitle(R.string.enter_password);
            final MDButton mDialogActionButton = mDialog.getActionButton(DialogAction.POSITIVE);
            View customView = mDialog.getCustomView();
            Objects.requireNonNull(customView);
            EditText edPass = customView.findViewById(R.id.password);
            ((TextView) mDialog.getCustomView().findViewById(R.id.enter_password)).setText(R.string.decrypt_message);
            edPass.setText("");
            edPass.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    mDialogActionButton.setEnabled(charSequence.toString().trim().length() > 4);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    sArr[0] = editable.toString();
                }
            });
            mDialog.show();
            mDialogActionButton.setEnabled(false);
            mDialogActionButton.setOnClickListener(view -> {
                if (!removePasswordUsingDefMasterPassword(path, name, dataSetChanged, sArr) && !removePasswordUsingInputMasterPassword(path, name, dataSetChanged, sArr)) {
                    StringUtils.getInstance().showSnackbar(mContext, R.string.master_password_changed);
                }
                mDialog.dismiss();

            });
        }
    }

    public void removePasswordModule(String path, String name, DataSetChanged dataSetChanged, OnPDFAddRemovePasswordInterface onPDFAddRemovePasswordInterface) {
        if (isPDFEncrypted(path)) {
            final String[] pass = new String[1];
            mDialog.setTitle(R.string.enter_password);
            final MDButton mDialogActionButton = mDialog.getActionButton(DialogAction.POSITIVE);
            View customView = mDialog.getCustomView();
            Objects.requireNonNull(customView);
            EditText editText = customView.findViewById(R.id.password);
            ((TextView) mDialog.getCustomView().findViewById(R.id.enter_password)).setText(R.string.decrypt_message);
            editText.setText("");
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    mDialogActionButton.setEnabled(charSequence.toString().trim().length() > 4);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    pass[0] = editable.toString();
                }
            });
            mDialog.show();
            mDialogActionButton.setEnabled(false);
            mDialogActionButton.setOnClickListener(view -> removePass(path, name, dataSetChanged, pass, onPDFAddRemovePasswordInterface));
        }
    }

    public void removePass(String path, String name, DataSetChanged dataSetChanged, String[] sArr, OnPDFAddRemovePasswordInterface onPDFAddRemovePasswordInterface) {
        if (removePasswordUsingDefMasterPassword(path, name, dataSetChanged, sArr)) {
            onPDFAddRemovePasswordInterface.pdfEnded("", true);
        } else if (!removePasswordUsingInputMasterPassword(path, name, dataSetChanged, sArr)) {
            onPDFAddRemovePasswordInterface.pdfEnded("", false);
            StringUtils.getInstance().showSnackbar(mContext, R.string.master_password_changed);
        } else {
            onPDFAddRemovePasswordInterface.pdfEnded("", true);
        }
        mDialog.dismiss();
    }

    public String removeDefPasswordForImages(String path, String[] passArr) {
        try {
            PdfReader pdfReader = new PdfReader(path, mSharedPrefs.getString(Constants.MASTER_PWD_STRING, Constants.appName).getBytes());
            String uniqueFileName = mFileUtils.getUniqueFileName(path.replace(mContext.getResources().getString(R.string.pdf_ext), mContext.getString(R.string.decrypted_file)));
            if (Arrays.equals(passArr[0].getBytes(), pdfReader.computeUserPassword())) {
                new PdfStamper(pdfReader, new FileOutputStream(uniqueFileName)).close();
                pdfReader.close();
                return uniqueFileName;
            }
            File file = new File(path);
            if (!file.isFile()) {
                return null;
            }
            String name = file.getName();
            if (name.contains("encrypted")) {
                mContext.deleteFile(name);
            }
            Log.e("Delete", file.getAbsolutePath());
            return null;
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean removePasswordUsingDefMasterPassword(String path, String name, DataSetChanged dataSetChanged, String[] pass) {
        try {
            PdfReader pdfReader = new PdfReader(path, mSharedPrefs.getString(Constants.MASTER_PWD_STRING, Constants.appName).getBytes());
            if (Arrays.equals(pass[0].getBytes(), pdfReader.computeUserPassword())) {
                new PdfStamper(pdfReader, new FileOutputStream(name)).close();
                pdfReader.close();
                if (dataSetChanged != null) {
                    dataSetChanged.updateDataset();
                }
                new DatabaseHelper(mContext).insertRecord(name, mContext.getString(R.string.decrypted));

                Toast.makeText(mContext , mContext.getString(R.string.create_pdf_msg)+name, Toast.LENGTH_SHORT).show();
              //  StringUtils.getInstance().getSnackbarwithAction(mContext, R.string.snackbar_pdfCreated).show();
                return true;
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean removePasswordUsingInputMasterPassword(String path, String name, DataSetChanged dataSetChanged, String[] password) {
        try {
            PdfReader pdfReader = new PdfReader(path, password[0].getBytes());
            new PdfStamper(pdfReader, new FileOutputStream(name)).close();
            pdfReader.close();
            if (dataSetChanged != null) {
                dataSetChanged.updateDataset();
            }
            new DatabaseHelper(mContext).insertRecord(name, mContext.getString(R.string.decrypted));
            Toast.makeText(mContext , mContext.getString(R.string.create_pdf_msg)+name, Toast.LENGTH_SHORT).show();
         /*   StringUtils.getInstance().getSnackbarwithAction(mContext, R.string.snackbar_pdfCreated).
                    setAction(R.string.snackbar_viewAction, view -> mFileUtils.openFile(name, FileUtils.FileType.e_PDF)).show();*/
            return true;
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}

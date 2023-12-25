package com.templatemela.smartpdfreader.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dd.morphingbutton.MorphingButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.PermissionsUtils;
import com.templatemela.smartpdfreader.util.RealPathUtil;
import com.templatemela.smartpdfreader.util.ResultUtils;
import com.templatemela.smartpdfreader.util.StringUtils;
import com.templatemela.smartpdfreader.util.ZipToPdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.models.sort.SortingTypes;

public class ZipToPdfFragment extends Fragment {
    private static final int INTENT_REQUEST_PICK_FILE_CODE = 10;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 145;
    @BindView(R.id.zip_to_pdf)
    MorphingButton convertButton;
    @BindView(R.id.progressBar)
    ProgressBar extractionProgress;
    private Activity mActivity;
    private String mPath;
    private boolean mPermissionGranted = false;
    @BindView(R.id.selectFile)
    MorphingButton selectFileButton;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_zip_to_pdf, viewGroup, false);
        ButterKnife.bind(this, inflate);
        mActivity = getActivity();
        return inflate;
    }

    @OnClick({R.id.selectFile})
    public void showFileChooser() {
        getRuntimePermissions();

    }

    private void getRuntimePermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            Dexter.withActivity(mActivity)
                    .withPermissions(
                            Manifest.permission.READ_MEDIA_IMAGES)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                Toast.makeText(mActivity, "All the permissions are granted..", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setDataAndType(Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + Constants.PATH_SEPERATOR), "application/zip");
                                    startActivityForResult(Intent.createChooser(intent, getString(R.string.merge_file_select)), INTENT_REQUEST_PICK_FILE_CODE);
                            }
                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).withErrorListener(error -> {
                        Toast.makeText(mActivity, "Error occurred! ", Toast.LENGTH_SHORT).show();
                    })
                    .onSameThread().check();

        }
        else {
            Dexter.withActivity(mActivity)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                Toast.makeText(mActivity, "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                                String[] zips = {"zip", "rar"};
                                String[] pdfs = {"aac"};

                                FilePickerBuilder.getInstance()
                                        .setMaxCount(1)
                                        .setActivityTheme(R.style.FilePickerTheme)
                                        .setActivityTitle("Please select Zip File")
                                        .setImageSizeLimit(5) //Provide Size in MB
                                        .setVideoSizeLimit(20)
                                        .addFileSupport("ZIP", zips)
                                        .enableDocSupport(false)
                                        .enableSelectAll(true)
                                        .sortDocumentsBy(SortingTypes.NAME)
                                        .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                                        .pickFile(mActivity);
                            }
                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).withErrorListener(error -> {
                        Toast.makeText(mActivity, "Error occurred! ", Toast.LENGTH_SHORT).show();
                    })
                    .onSameThread().check();
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) throws NullPointerException {

        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == RESULT_OK && intent != null) {
                    ArrayList<Uri> dataList = intent.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                    if (dataList != null) {
                        String realPath = getRealPathFromURI( dataList.get(0),getContext());
                        mPath = realPath;
                        if (realPath != null) {
                            convertButton.setVisibility(View.VISIBLE);
                        }
                        Log.e("test",dataList.toString());

                    }
                }
                break;

            case INTENT_REQUEST_PICK_FILE_CODE:
                if (requestCode == INTENT_REQUEST_PICK_FILE_CODE && resultCode == RESULT_OK){
                    if (intent != null){
                        Uri uri = intent.getData();
                        Log.d("TAG", "onActivityResult: check this zip file uri " + uri);

                            String realPath = getRealPathFromURI(uri,mActivity);
                            mPath = realPath;
                            if (realPath != null) {
                                convertButton.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                break;
                }

            }


    private  String getRealPathFromURI(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }
    public  String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        Log.i("URI",uri+"");
        String result = uri+"";
        // DocumentProvider
        //  if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        if (isKitKat && (result.contains("media.documents"))) {
            String[] ary = result.split("/");
            int length = ary.length;
            String imgary = ary[length-1];
            final String[] dat = imgary.split("%3A");
            final String docId = dat[1];
            final String type = dat[0];
            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
            } else if ("audio".equals(type)) {
            }
            final String selection = "_id=?";
            final String[] selectionArgs = new String[] {
                    dat[1]
            };
            return getDataColumn(context, contentUri, selection, selectionArgs);
        } else if ("content".equalsIgnoreCase(uri.toString())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @OnClick({R.id.zip_to_pdf})
    public void convertZipToPdf() {
        extractionProgress.setVisibility(View.VISIBLE);
        selectFileButton.blockTouch();
        convertButton.blockTouch();
        ZipToPdf.getInstance().convertZipToPDF(mPath, mActivity);
        extractionProgress.setVisibility(View.GONE);
        selectFileButton.unblockTouch();
        convertButton.unblockTouch();
    }

}

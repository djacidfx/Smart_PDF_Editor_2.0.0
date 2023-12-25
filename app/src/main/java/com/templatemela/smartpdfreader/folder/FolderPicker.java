package com.templatemela.smartpdfreader.folder;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FolderPicker extends Activity {
    Comparator<FilePojo> comparatorAscending = new Comparator<FilePojo>() {
        @Override
        public int compare(FilePojo filePojo, FilePojo filePojo2) {
            return filePojo.getName().compareTo(filePojo2.getName());
        }
    };
    ArrayList<FilePojo> filesList;
    ArrayList<FilePojo> folderAndFileList;
    ArrayList<FilePojo> foldersList;
    String location = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    boolean pickFiles;
    Intent receivedIntent;
    TextView tv_location;
    TextView tv_title;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.fp_main_layout);
        if (!isExternalStorageReadable()) {
            Toast.makeText(this, "Storage access permission not given", Toast.LENGTH_SHORT).show();
            finish();
        }
        tv_title = findViewById(R.id.fp_tv_title);
        tv_location = findViewById(R.id.fp_tv_location);
        try {
            receivedIntent = getIntent();
            if (receivedIntent.hasExtra("title") && receivedIntent.getExtras().getString("title") != null) {
                tv_title.setText(receivedIntent.getExtras().getString("title"));
            }
            if (receivedIntent.hasExtra("location") && receivedIntent.getExtras().getString("location") != null && new File(receivedIntent.getExtras().getString("location")).exists()) {
                location = receivedIntent.getExtras().getString("location");
            }
            if (receivedIntent.hasExtra("pickFiles")) {
                pickFiles = receivedIntent.getExtras().getBoolean("pickFiles");
                if (pickFiles) {
                    findViewById(R.id.fp_btn_select).setVisibility(View.GONE);
                    findViewById(R.id.fp_btn_new).setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadLists(location);
    }


    public boolean isExternalStorageReadable() {
        String externalStorageState = Environment.getExternalStorageState();
        return "mounted".equals(externalStorageState) || "mounted_ro".equals(externalStorageState);
    }


    public void loadLists(String location) {
        try {
            File file = new File(location);
            if (!file.isDirectory()) {
                exit();
            }
            tv_location.setText("Location : " + file.getAbsolutePath());
            File[] listFiles = file.listFiles();
            foldersList = new ArrayList<>();
            filesList = new ArrayList<>();
            for (File file2 : listFiles) {
                if (file2.isDirectory()) {
                    foldersList.add(new FilePojo(file2.getName(), true));
                } else {
                    filesList.add(new FilePojo(file2.getName(), false));
                }
            }
            Collections.sort(foldersList, comparatorAscending);
            folderAndFileList = new ArrayList<>();
            folderAndFileList.addAll(foldersList);
            if (pickFiles) {
                Collections.sort(filesList, comparatorAscending);
                folderAndFileList.addAll(filesList);
            }
            showList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showList() {
        try {
            FolderAdapter folderAdapter = new FolderAdapter(this, folderAndFileList);
            ListView listView = findViewById(R.id.fp_listView);
            listView.setAdapter(folderAdapter);
            listView.setOnItemClickListener((adapterView, view, i, j) -> listClick(i));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void listClick(int i) {
        if (!pickFiles || folderAndFileList.get(i).isFolder()) {
            location = location + File.separator + folderAndFileList.get(i).getName();
            loadLists(location);
            return;
        }
        receivedIntent.putExtra("data", location + File.separator + folderAndFileList.get(i).getName());
        setResult(-1, receivedIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack(null);
    }

    public void goBack(View view) {
        if (location == null || location.equals("") || location.equals(Constants.PATH_SEPERATOR)) {
            exit();
            return;
        }
        String substring = location.substring(0, location.lastIndexOf(47));
        location = substring;
        loadLists(substring);
    }


    public void exit() {
        setResult(0, receivedIntent);
        finish();
    }


    public void createNewFolder(String name) {
        try {
            new File(location + File.separator + name).mkdirs();
            loadLists(location);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error:" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void newFolderDialog(View view) {
        AlertDialog create = new AlertDialog.Builder(this).create();
        create.setTitle("Enter Folder Name");
        final EditText editText = new EditText(this);
        create.setView(editText);
        create.setButton(-1, "Create", (dialogInterface, i) -> createNewFolder(editText.getText().toString()));
        create.setButton(-2, "Cancel", (dialogInterface, i) -> {
        });
        create.show();
    }

    public void select(View view) {
        if (pickFiles) {
            Toast.makeText(this, "You have to select a file", Toast.LENGTH_SHORT).show();
            return;
        }
        if (receivedIntent != null) {
            receivedIntent.putExtra("data", location);
            setResult(-1, receivedIntent);
            finish();
        }
    }

    public void cancel(View view) {
        exit();
    }
}

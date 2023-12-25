package com.templatemela.smartpdfreader.folder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.templatemela.smartpdfreader.R;

import java.util.ArrayList;

public class FolderAdapter extends ArrayAdapter<FilePojo> {
    Activity context;
    ArrayList<FilePojo> dataList;

    public FolderAdapter(Activity activity, ArrayList<FilePojo> arrayList) {
        super(activity, R.layout.fp_filerow, arrayList);
        this.context = activity;
        this.dataList = arrayList;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View inflate = context.getLayoutInflater().inflate(R.layout.fp_filerow, viewGroup, false);
        ImageView imageView = inflate.findViewById(R.id.fp_iv_icon);
        TextView textView = inflate.findViewById(R.id.fp_tv_name);
        if (dataList.get(position).isFolder()) {
            imageView.setImageResource(R.drawable.fp_folder);
        } else {
            imageView.setImageResource(R.drawable.fp_file);
        }
        textView.setText(dataList.get(position).getName());
        return inflate;
    }
}


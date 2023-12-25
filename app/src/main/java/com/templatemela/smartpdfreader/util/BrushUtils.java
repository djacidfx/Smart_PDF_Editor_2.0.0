package com.templatemela.smartpdfreader.util;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.model.BrushItem;
import java.util.ArrayList;

public class BrushUtils {
    private BrushUtils() {
    }

    public ArrayList<BrushItem> getBrushItems() {
        ArrayList<BrushItem> arrayList = new ArrayList<>();
        arrayList.add(new BrushItem(R.color.mb_white));
        arrayList.add(new BrushItem(R.color.red));
        arrayList.add(new BrushItem(R.color.mb_blue));
        arrayList.add(new BrushItem(R.color.mb_green));
        arrayList.add(new BrushItem(R.color.dark_purple));
        arrayList.add(new BrushItem(R.color.text_color));
        arrayList.add(new BrushItem(R.color.light_gray));
        arrayList.add(new BrushItem(R.color.black));
        arrayList.add(new BrushItem(R.drawable.color_palette));
        return arrayList;
    }

    private static class SingletonHolder {
        static final BrushUtils INSTANCE = new BrushUtils();

        private SingletonHolder() {
        }
    }

    public static BrushUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }
}

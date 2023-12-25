package com.templatemela.smartpdfreader.util;

import android.content.Context;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.model.FilterItem;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import java.util.ArrayList;

public class ImageFilterUtils {

    private static class SingletonHolder {
        static final ImageFilterUtils INSTANCE = new ImageFilterUtils();

        private SingletonHolder() {
        }
    }

    public static ImageFilterUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public ArrayList<FilterItem> getFiltersList(Context context) {
        ArrayList<FilterItem> arrayList = new ArrayList<>();
        arrayList.add(new FilterItem(R.drawable.none, context.getString(R.string.filter_none), PhotoFilter.NONE));
        arrayList.add(new FilterItem(R.drawable.brush, context.getString(R.string.filter_brush), PhotoFilter.NONE));
        arrayList.add(new FilterItem(R.drawable.auto_fix, context.getString(R.string.filter_autofix), PhotoFilter.AUTO_FIX));
        arrayList.add(new FilterItem(R.drawable.black, context.getString(R.string.filter_grayscale), PhotoFilter.GRAY_SCALE));
        arrayList.add(new FilterItem(R.drawable.brightness, context.getString(R.string.filter_brightness), PhotoFilter.BRIGHTNESS));
        arrayList.add(new FilterItem(R.drawable.contrast, context.getString(R.string.filter_contrast), PhotoFilter.CONTRAST));
        arrayList.add(new FilterItem(R.drawable.cross_process, context.getString(R.string.filter_cross), PhotoFilter.CROSS_PROCESS));
        arrayList.add(new FilterItem(R.drawable.documentary, context.getString(R.string.filter_documentary), PhotoFilter.DOCUMENTARY));
        arrayList.add(new FilterItem(R.drawable.due_tone, context.getString(R.string.filter_duetone), PhotoFilter.DUE_TONE));
        arrayList.add(new FilterItem(R.drawable.fill_light, context.getString(R.string.filter_filllight), PhotoFilter.FILL_LIGHT));
        arrayList.add(new FilterItem(R.drawable.flip_vertical, context.getString(R.string.filter_filpver), PhotoFilter.FLIP_VERTICAL));
        arrayList.add(new FilterItem(R.drawable.flip_horizontal, context.getString(R.string.filter_fliphor), PhotoFilter.FLIP_HORIZONTAL));
        arrayList.add(new FilterItem(R.drawable.grain, context.getString(R.string.filter_grain), PhotoFilter.GRAIN));
        arrayList.add(new FilterItem(R.drawable.lomish, context.getString(R.string.filter_lomish), PhotoFilter.LOMISH));
        arrayList.add(new FilterItem(R.drawable.negative, context.getString(R.string.filter_negative), PhotoFilter.NEGATIVE));
        arrayList.add(new FilterItem(R.drawable.poster, context.getString(R.string.filter_poster), PhotoFilter.POSTERIZE));
        arrayList.add(new FilterItem(R.drawable.rotate, context.getString(R.string.filter_rotate), PhotoFilter.ROTATE));
        arrayList.add(new FilterItem(R.drawable.saturate, context.getString(R.string.filter_saturate), PhotoFilter.SATURATE));
        arrayList.add(new FilterItem(R.drawable.sepia, context.getString(R.string.filter_sepia), PhotoFilter.SEPIA));
        arrayList.add(new FilterItem(R.drawable.sharpen, context.getString(R.string.filter_sharpen), PhotoFilter.SHARPEN));
        arrayList.add(new FilterItem(R.drawable.temp, context.getString(R.string.filter_temp), PhotoFilter.TEMPERATURE));
        arrayList.add(new FilterItem(R.drawable.tint, context.getString(R.string.filter_tint), PhotoFilter.TINT));
        arrayList.add(new FilterItem(R.drawable.vignette, context.getString(R.string.filter_vig), PhotoFilter.VIGNETTE));
        return arrayList;
    }
}

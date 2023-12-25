package com.templatemela.smartpdfreader.util;

import android.content.Context;
import com.itextpdf.text.Font;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.model.EnhancementOptionsEntity;
import java.util.ArrayList;

public class AddTextEnhancementOptionsUtils {
    private AddTextEnhancementOptionsUtils() {
    }

    public ArrayList<EnhancementOptionsEntity> getEnhancementOptions(Context context, String title, Font.FontFamily fontFamily) {
        ArrayList<EnhancementOptionsEntity> arrayList = new ArrayList<>();
        arrayList.add(new EnhancementOptionsEntity(context.getResources().getDrawable(R.drawable.ic_text_size), title));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_extract_text, String.format(context.getString(R.string.default_font_family_text), fontFamily.name())));
        return arrayList;
    }

    private static class SingletonHolder {
        static final AddTextEnhancementOptionsUtils INSTANCE = new AddTextEnhancementOptionsUtils();

        private SingletonHolder() {
        }
    }


    public static AddTextEnhancementOptionsUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }
}

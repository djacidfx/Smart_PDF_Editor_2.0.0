package com.templatemela.smartpdfreader.util;

import android.content.Context;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.model.EnhancementOptionsEntity;
import java.util.ArrayList;

public class MergePdfEnhancementOptionsUtils {

    private static class SingletonHolder {
        static final MergePdfEnhancementOptionsUtils INSTANCE = new MergePdfEnhancementOptionsUtils();

        private SingletonHolder() {
        }
    }

    public static MergePdfEnhancementOptionsUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public ArrayList<EnhancementOptionsEntity> getEnhancementOptions(Context context) {
        ArrayList<EnhancementOptionsEntity> arrayList = new ArrayList<>();
        arrayList.add(new EnhancementOptionsEntity(context,  R.drawable.ic_add_password,  R.string.set_password));
        return arrayList;
    }
}

package com.templatemela.smartpdfreader.fragment.texttopdf;

import android.content.Context;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.interfaces.Enhancer;
import com.templatemela.smartpdfreader.model.EnhancementOptionsEntity;
import com.templatemela.smartpdfreader.preferences.TextToPdfPreferences;
import com.templatemela.smartpdfreader.util.PageSizeUtils;

public class PageSizeEnhancer implements Enhancer {
    private final EnhancementOptionsEntity mEnhancementOptionsEntity;
    private final PageSizeUtils mPageSizeUtils;

    PageSizeEnhancer(Context context) {
        mPageSizeUtils = new PageSizeUtils(context);
        mEnhancementOptionsEntity = new EnhancementOptionsEntity(context, R.drawable.ic_set_page_size, R.string.set_page_size_text);
        PageSizeUtils.mPageSize = new TextToPdfPreferences(context).getPageSize();
    }

    @Override
    public void enhance() {
        mPageSizeUtils.showPageSizeDialog(false);
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }
}

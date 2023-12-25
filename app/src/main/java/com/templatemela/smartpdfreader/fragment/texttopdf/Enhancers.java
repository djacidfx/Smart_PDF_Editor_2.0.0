package com.templatemela.smartpdfreader.fragment.texttopdf;

import android.app.Activity;

import com.templatemela.smartpdfreader.interfaces.Enhancer;
import com.templatemela.smartpdfreader.model.TextToPDFOptions;

public enum Enhancers {
    FONT_COLOR {

        public Enhancer getEnhancer(Activity activity, TextToPdfContract.View view, TextToPDFOptions.Builder builder) {
            return new FontColorEnhancer(activity, builder);
        }
    },
    FONT_FAMILY {

        public Enhancer getEnhancer(Activity activity, TextToPdfContract.View view, TextToPDFOptions.Builder builder) {
            return new FontFamilyEnhancer(activity, view, builder);
        }
    },
    FONT_SIZE {

        public Enhancer getEnhancer(Activity activity, TextToPdfContract.View view, TextToPDFOptions.Builder builder) {
            return new FontSizeEnhancer(activity, view, builder);
        }
    },
    PAGE_COLOR {

        public Enhancer getEnhancer(Activity activity, TextToPdfContract.View view, TextToPDFOptions.Builder builder) {
            return new PageColorEnhancer(activity, builder);
        }
    },
    PAGE_SIZE {

        public Enhancer getEnhancer(Activity activity, TextToPdfContract.View view, TextToPDFOptions.Builder builder) {
            return new PageSizeEnhancer(activity);
        }
    },
    PASSWORD {

        public Enhancer getEnhancer(Activity activity, TextToPdfContract.View view, TextToPDFOptions.Builder builder) {
            return new PasswordEnhancer(activity, view, builder);
        }
    };


    public abstract Enhancer getEnhancer(Activity activity, TextToPdfContract.View view, TextToPDFOptions.Builder builder);
}

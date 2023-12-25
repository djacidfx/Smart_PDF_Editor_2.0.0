package com.templatemela.smartpdfreader.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.templatemela.smartpdfreader.R;

public class MyCardView extends LinearLayout {
    ImageView icon = findViewById(R.id.option_image);
    TextView text;

    public MyCardView(Context context) {
        super(context);
        init();
    }

    public MyCardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet);
    }

    public MyCardView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet);
    }

    private void init() {
        inflate(getContext(), R.layout.item_view_enhancement_option, this);
        text = findViewById(R.id.option_name);
        icon = findViewById(R.id.option_image);
    }

    private void init(AttributeSet attributeSet) {
        inflate(getContext(), R.layout.item_view_enhancement_option, this);
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.MyCardView);
        text = findViewById(R.id.option_name);
        icon = findViewById(R.id.option_image);
        text.setText(obtainStyledAttributes.getString(R.styleable.MyCardView_option_text));
        icon.setImageDrawable(obtainStyledAttributes.getDrawable(R.styleable.MyCardView_option_icon));
//        TypedArray obtainStyledAttributes2 = getContext().obtainStyledAttributes(attributeSet, R.styleable.CTAppTheme);
//        icon.setColorFilter(obtainStyledAttributes2.getColor(R.styleable.CTAppTheme_iconTintOnCard, ContextCompat.getColor(getContext(), R.color.transparent)));
//        text.setTextColor(obtainStyledAttributes2.getColor(R.styleable.CTAppTheme_customDividerColor, ContextCompat.getColor(getContext(), R.color.black)));
        obtainStyledAttributes.recycle();
//        obtainStyledAttributes2.recycle();
    }


    public  void setOptionText(int stringId){
        text.setText(stringId);

    }
}

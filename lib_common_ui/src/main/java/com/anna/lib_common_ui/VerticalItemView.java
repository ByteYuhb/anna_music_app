package com.anna.lib_common_ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * 垂直icon+msg
 */
public class VerticalItemView extends RelativeLayout {

    private Context mContext;

    /*
     * 所有样式属性
     */
    private int mIconWidth;
    private int mIconHeight;
    private Drawable mIcon;

    private int mTipPaddingTop;
    private int mTipPaddingRight;
    private Drawable mTipBg;
    private int mTipTextColor;
    private float mTipTextSize;
    private String mTipText;

    private float mInfoTextSize;
    private int mInfoTextColor;
    private int mInfoTextMarginTop;
    private String mInfoText;

    /*
     * 所有View
     */
    private ImageView mIconView;
    private TextView mTipView;
    private TextView mInfoView;

    public VerticalItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.VerticalItem);
        mIconWidth = a.getLayoutDimension(R.styleable.VerticalItem_iconWidth, 35);
        mIconHeight = a.getLayoutDimension(R.styleable.VerticalItem_iconHeight, 35);
        mIcon = a.getDrawable(R.styleable.VerticalItem_icon);
        mTipPaddingTop = a.getLayoutDimension(R.styleable.VerticalItem_tipPaddingTop, 2);
        mTipPaddingRight = a.getLayoutDimension(R.styleable.VerticalItem_tipPaddingRight, 2);
        mTipBg = a.getDrawable(R.styleable.VerticalItem_tipBg);
        mTipTextColor = a.getColor(R.styleable.VerticalItem_tipTextColor, 0xffffff);
        mTipTextSize = a.getDimension(R.styleable.VerticalItem_tipTextSize, 12);
        mTipText = a.getString(R.styleable.VerticalItem_tipText);
        mInfoTextSize = a.getDimension(R.styleable.VerticalItem_infoTextSize, 12);
        mInfoTextColor = a.getColor(R.styleable.VerticalItem_infoTextColor, 0x333333);
        mInfoTextMarginTop = a.getLayoutDimension(R.styleable.VerticalItem_infoTextMarginTop, 10);
        mInfoText = a.getString(R.styleable.VerticalItem_infoText);
        a.recycle();

        //居中添加到布局中
        LayoutParams params =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_IN_PARENT);
        addView(createItemView(), params);
    }

    /**
     * 构建自己的组合view
     */
    private View createItemView() {
        RelativeLayout rootLayout = new RelativeLayout(mContext);
        mIconView = new ImageView(mContext);
        mIconView.setImageDrawable(mIcon);
        mIconView.setId(R.id.vertical_image_id);
        LayoutParams iconParams =
                new LayoutParams(mIconWidth, mIconHeight);
        iconParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rootLayout.addView(mIconView, iconParams);

        mInfoView = new TextView(mContext);
        mInfoView.setId(R.id.vertical_text_id);
        mInfoView.setTextColor(mInfoTextColor);
        mInfoView.getPaint().setTextSize(mInfoTextSize);
        mInfoView.setText(mInfoText);
        LayoutParams textParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textParams.setMargins(0, mInfoTextMarginTop, 0, 0);
        textParams.addRule(RelativeLayout.BELOW, R.id.vertical_image_id);
        textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rootLayout.addView(mInfoView, textParams);

        //mTipView = new TextView(mContext);
        //mTipView.setId(R.id.vertical_tip_id);
        //mTipView.setBackground(mTipBg);
        //mTipView.setText(mTipText);
        //mTipView.getPaint().setTextSize(mTipTextSize);
        //mTipView.setTextColor(mTipTextColor);
        //RelativeLayout.LayoutParams tipParams =
        //    new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //tipParams.addRule(RelativeLayout.RIGHT_OF, R.id.vertical_image_id);
        //tipParams.addRule(RelativeLayout.ABOVE, R.id.vertical_image_id);
        //tipParams.setMargins(0, mTipPaddingTop, mTipPaddingRight, 0);
        //rootLayout.addView(mTipView, tipParams);
        return rootLayout;
    }
}

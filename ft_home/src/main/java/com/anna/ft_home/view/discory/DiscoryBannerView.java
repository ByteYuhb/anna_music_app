package com.anna.ft_home.view.discory;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.anna.ft_home.R;
import com.anna.ft_home.model.discory.RecommandHeadValue;
import com.anna.ft_home.view.discory.adapter.BannerPagerAdapter;
import com.anna.lib_common_ui.banner.AutoScrollViewPager;
import com.anna.lib_common_ui.pager_indictor.CirclePageIndicator;



public class DiscoryBannerView extends RelativeLayout {
  private Context mContext;

  /**
   * UI
   */
  private AutoScrollViewPager mViewPager;
  private BannerPagerAdapter mAdapter;
  private CirclePageIndicator mPagerIndictor;
  /**
   * Data
   */
  private RecommandHeadValue mHeaderValue;

  public DiscoryBannerView(Context context, RecommandHeadValue headerValue) {
    this(context, null, headerValue);
  }

  public DiscoryBannerView(Context context, AttributeSet attrs, RecommandHeadValue headerValue) {
    super(context, attrs);
    mContext = context;
    mHeaderValue = headerValue;
    initView();
  }

  private void initView() {
    View rootView =
        LayoutInflater.from(mContext).inflate(R.layout.item_discory_head_banner_layout, this);
    mViewPager = rootView.findViewById(R.id.pager);
    mPagerIndictor = rootView.findViewById(R.id.pager_indictor_view);

    mAdapter = new BannerPagerAdapter(mContext, mHeaderValue.ads);
    mViewPager.setAdapter(mAdapter);
    mViewPager.startAutoScroll(3000);
    mViewPager.setInterval(3000);
    mPagerIndictor.setViewPager(mViewPager);
  }
}

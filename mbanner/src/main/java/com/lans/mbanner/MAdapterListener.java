package com.lans.mbanner;

import android.view.View;

/**
 * author:       lans
 * date:         2019/3/2511:50 AM
 * description:
 **/
public interface MAdapterListener {
    void imageListener(View view, String t);

    void bannerOnClickListener(int position);
}

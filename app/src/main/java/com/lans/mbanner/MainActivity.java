package com.lans.mbanner;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> urls;
    private MBanner mBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urls = new ArrayList<>();
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552996252766&di=88bf6bc605c993904c23401125253df5&imgtype=0&src=http%3A%2F%2Fpic.90sjimg.com%2Fback_pic%2Fqk%2Fback_origin_pic%2F00%2F03%2F11%2F6c2e8c26260ddd557291579e9012988a.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552996252766&di=0f1f062329aca2950a1e97049b6f7512&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01cde658a416eda801219c7773e7d8.png");
        //urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553581735290&di=f82e2da91d97dbc628fcb333dfd2c61d&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F010588583c063da801219c77b14fe8.jpg");
        mBanner = findViewById(R.id.mBanner);
        mBanner.setAdapterListener(new MAdapterListener() {
            @Override
            public void imageListener(View view, String o) {
                ImageView img = (ImageView) view;
                GlideApp.with(MainActivity.this)
                        .load(o)
                        .into(img);
            }

            @Override
            public void bannerOnClickListener(int position) {
                Snackbar.make(mBanner, "当前position为" + position, Snackbar.LENGTH_SHORT).show();
            }
        });
        mBanner.setListData(urls);
        mBanner.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBanner.releaseBanner();
    }
}

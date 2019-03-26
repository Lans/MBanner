package com.lans.mbanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * author:       lans
 * date:         2019/3/193:55 PM
 * description:
 **/
public class MBanner extends RelativeLayout {
    private static final String TAG = "MBanner";
    private Context mContext;
    private RecyclerView mRecyclerView;
    private LinearLayout mRoundViews;
    private MAdapter mAdapter;
    private MAdapterListener mAdapterListener;
    private LinearLayout.LayoutParams layoutParams;
    protected List<String> mList;
    // 指示器的位置 居中、左右
    private int mIndicatorGravity = 0;                  //指示器的位置
    private int mIndicatorStartEndMargins = 0;          //指示器左右Margins
    private int mIndicatorBottomMargins = 0;            //指示器底部Margins
    protected int mCurrentBannerPosition;               //当前banner的position
    protected int mDelayTime;                           //轮播时间
    protected boolean isAutoRun = true;                 //是否自动轮播
    protected boolean isShowIndicator = true;           //是否显示指示器
    protected int mIndicatorActive;                     //指示器选中的资源文件图片
    protected int mIndicatorInactive;                   //指示器未选中的资源文件图片
    protected int mPlaceholder;                         //默认的占位图
    protected WeakHandler mWeakHandler = new WeakHandler();
    protected Runnable runTask = new Runnable() {
        @Override
        public void run() {
            mCurrentBannerPosition++;
            if (mRecyclerView != null) {
                if (isAutoRun) {
                    mRecyclerView.smoothScrollToPosition(mCurrentBannerPosition);
                    mWeakHandler.postDelayed(this, mDelayTime);
                }
                moveIndicator(mCurrentBannerPosition);
            }
        }
    };

    public MBanner(Context context) {
        super(context);
    }

    public MBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context, attrs);
        init(context);

    }

    public MBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs);
        init(context);
    }

    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MBanner);
        mIndicatorGravity = typedArray.getInt(R.styleable.MBanner_mIndicatorGravity, 0);
        mDelayTime = typedArray.getInt(R.styleable.MBanner_mDelayTime, 3000);
        isShowIndicator = typedArray.getBoolean(R.styleable.MBanner_mShowIndicator, true);
        mIndicatorStartEndMargins = typedArray.getDimensionPixelSize(R.styleable.MBanner_mIndicatorStartEndMargins, 10);
        mIndicatorBottomMargins = typedArray.getDimensionPixelSize(R.styleable.MBanner_mIndicatorBottomMargins, 30);
        mIndicatorActive = typedArray.getInt(R.styleable.MBanner_mIndicatorActive, R.drawable.ic_round_white);
        mIndicatorInactive = typedArray.getInt(R.styleable.MBanner_mIndicatorInactive, R.drawable.ic_round);
        mPlaceholder = typedArray.getResourceId(R.styleable.MBanner_mPlaceholder, mPlaceholder);
        typedArray.recycle();
    }

    private void init(Context context) {
        this.mContext = context;
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mList = new ArrayList<>();
    }

    private void initBanner() {
        mAdapter = new MAdapter(mList, mContext);
        mAdapter.setmAdapterListener(mAdapterListener);
        mRecyclerView = new RecyclerView(mContext);
        mRecyclerView.setLayoutParams(layoutParams);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mAdapter);
        // 设置一次翻一项
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                // 当前滑动的项
                int target = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
                // 联动指示器
                moveIndicator(target);
                return target;
            }
        };
        pagerSnapHelper.attachToRecyclerView(mRecyclerView);
        addView(mRecyclerView);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                startPlay();
                break;
            case MotionEvent.ACTION_DOWN:
                stopPlay();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    //原点移动
    private void moveIndicator(int target) {
        mCurrentBannerPosition = target;
        Log.e(TAG, "当前的position是" + mCurrentBannerPosition % mList.size());
        int index = mCurrentBannerPosition % mList.size();
        if (mRoundViews != null && isShowIndicator) {
            for (int i = 0; i < mList.size(); i++) {
                mRoundViews.getChildAt(i).setBackgroundResource(R.drawable.ic_round);
            }
            View childAt = mRoundViews.getChildAt(index);
            childAt.setBackgroundResource(R.drawable.ic_round_white);
        }
    }

    /**
     * 设置数据源
     *
     * @param list
     * @return
     */
    public void setListData(List<String> list) {
        mList.clear();
        mList.addAll(list);
        if (mList.size() > 0) {
            initBanner();
            if (isShowIndicator && mList.size() > 1) {
                //显示下方的指示器
                setIndicatorLayout();
            }
        } else { //显示默认图
            ImageView placeholderImg = new ImageView(mContext);
            placeholderImg.setLayoutParams(layoutParams);
            placeholderImg.setImageResource(mPlaceholder);
            addView(placeholderImg);
        }
    }


    //设置指示器的位置
    private void setIndicatorLayout() {
        //大于1张图时，添加指示器按钮
        mRoundViews = new LinearLayout(getContext());
        mRoundViews.setOrientation(LinearLayout.HORIZONTAL);
        mRoundViews.setId(Integer.MAX_VALUE - 1000);
        mRoundViews.removeAllViews();


        LinearLayout.LayoutParams viewLayoutParams = new LinearLayout.LayoutParams(30, 30);
        viewLayoutParams.setMargins(mIndicatorStartEndMargins, 0, mIndicatorStartEndMargins, 0);
        for (int i = 0; i < mList.size(); i++) {
            View view = new View(getContext());
            view.setLayoutParams(viewLayoutParams);
            view.setPadding(20, 20, 20, 20);
            if (i == mCurrentBannerPosition) {
                view.setBackgroundResource(mIndicatorActive);
            } else {
                view.setBackgroundResource(mIndicatorInactive);
            }
            mRoundViews.addView(view);
        }

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        switch (mIndicatorGravity) {
            case 0:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, mRoundViews.getId());
                break;
            case 1:
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, mRoundViews.getId());
                break;
            case 2:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, mRoundViews.getId());
                break;
        }
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, mRoundViews.getId());
        layoutParams.setMargins(10, 10, 10, mIndicatorBottomMargins);
        addView(mRoundViews, layoutParams);
    }

    //停止自动滚动
    public void stopPlay() {
        if (mWeakHandler != null) {
            mWeakHandler.removeCallbacks(runTask);
            isAutoRun = false;
        }
    }

    //开始自动滚动
    public void startPlay() {
        if (mWeakHandler != null) {
            mWeakHandler.removeCallbacks(runTask);
            isAutoRun = true;
            mWeakHandler.postDelayed(runTask, mDelayTime);
        }
    }

    public void start() {
        if (mWeakHandler != null) {
            mWeakHandler.removeCallbacks(runTask);
            isAutoRun = true;
            mWeakHandler.postDelayed(runTask, mDelayTime);
        }
    }

    //设置监听
    public void setAdapterListener(MAdapterListener mAdapterListener) {
        this.mAdapterListener = mAdapterListener;
        if (mAdapter != null) {
            mAdapter.setmAdapterListener(mAdapterListener);
        }
    }

    //释放Handler
    public void releaseBanner() {
        if (mWeakHandler != null) {
            mWeakHandler.removeCallbacksAndMessages(null);
        }
    }
}

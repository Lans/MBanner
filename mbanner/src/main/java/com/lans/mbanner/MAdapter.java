package com.lans.mbanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;


/**
 * author:       lans
 * date:         2019/3/194:10 PM
 * description:
 **/
public class MAdapter extends RecyclerView.Adapter<MAdapter.MViewHolder> {
    private List<String> mList;
    private Context mContext;
    private MAdapterListener mAdapterListener;

    public MAdapter(List<String> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }


    public void setmAdapterListener(MAdapterListener mAdapterListener) {
        this.mAdapterListener = mAdapterListener;
    }

    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
        return new MViewHolder(imageView);
    }


    @Override
    public void onBindViewHolder(@NonNull final MViewHolder viewHolder, int i) {
        String t = mList.get(i % mList.size());
        if (mAdapterListener != null) {
            mAdapterListener.imageListener(viewHolder.imageView, t);
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapterListener.bannerOnClickListener(viewHolder.getAdapterPosition() % mList.size());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList == null || mList.size() < 2 ? 1 : Integer.MAX_VALUE;
    }

    static class MViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}
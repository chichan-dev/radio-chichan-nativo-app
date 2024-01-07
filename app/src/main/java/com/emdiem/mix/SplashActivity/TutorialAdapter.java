package com.emdiem.mix.SplashActivity;

import android.content.Context;
import android.media.MediaPlayer;

import androidx.viewpager.widget.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TutorialAdapter extends PagerAdapter {

    public ArrayList<Integer> data;
    public LayoutInflater layoutInflater;
    public Context context;
    public Map<Integer, VideoView> mVideoViewMap;



    public TutorialAdapter(Context context, ArrayList<Integer> data){
        this.data = data;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.mVideoViewMap = new HashMap<>();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){

        VideoView mViewView = new VideoView(context);

        if(position != 3) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);


            Log.d("file", ((new StringBuilder()).append("android.resource://").append(context.getPackageName()).append("/").append(data.get(position))).toString());
            mViewView.setLayoutParams(layoutParams);
            container.addView(mViewView, 0);

            if(position == 0) {
                mViewView.setVideoPath(((new StringBuilder()).append("android.resource://").append(context.getPackageName()).append("/").append(data.get(position))).toString());
                mViewView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mViewView.start();
                        mViewView.setTag(1);
                        mp.setLooping(true);
                    }
                });
            }

            mVideoViewMap.put(position, mViewView);

        }

        return mViewView;
    }

    @Override
    public int getCount(){
        return this.data.size();
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object view){
        container.removeView((View)view);
    }

}
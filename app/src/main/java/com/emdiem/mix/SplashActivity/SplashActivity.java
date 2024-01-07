// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.emdiem.mix.SplashActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;
import com.emdiem.mix.MainActivity.MainActivity;
import com.emdiem.mix.Models.City;
import com.github.skykai.stickercamera.R;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity
{

    private Context context;
    private List mCityList;
    private CityPagerAdapter mCityPagerAdapter;
    private ViewPager mViewPager;
    private VideoView videoView;
    private RelativeLayout mParentLayout;
    private CirclePageIndicator mCirclePageIndicator;
    private TutorialAdapter mTutorialAdapter;
    private ViewPager mTutorialPager;
    private ProgressDialog progress;
    private Button mTutorialCloseButton;

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);

        // Save
        SharedPreferences mSharedPrefs = getSharedPreferences(
                "com.emdiem.mix.SHARED_PREFERENCES", Context.MODE_PRIVATE);

        Boolean mInit = mSharedPrefs.getBoolean("init", false);

        if(mInit){
            Intent mIntent = new Intent(this, MainActivity.class);
            startActivity(mIntent);
            finish();
            return;
        }

        //
        hideSystemUI();

        setContentView(R.layout.activity_splash);
        context = this;
        videoView = (VideoView)findViewById(R.id.videoView);
        //videoView.setVideoURI(Uri.parse((new StringBuilder()).append("android.resource://").append(getPackageName()).append("/").append(R.raw.intro).toString()));
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mParentLayout = (RelativeLayout)findViewById(R.id.parentLayout);
        mTutorialPager = (ViewPager)findViewById(R.id.viewPager2);
        mTutorialCloseButton = (Button)findViewById(R.id.tutorialClose);
        mCirclePageIndicator = (CirclePageIndicator)findViewById(R.id.indicator);

        setup();
        listen();
    }


    // This snippet hides the system bars.
    private void hideSystemUI() {

        View decorView = getWindow().getDecorView();

        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        decorView.setSystemUiVisibility(

                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }


    public void setup()
    {
        mCityList = new ArrayList();

        mCityList.add(new City(0, R.drawable.baq_text));
        mCityList.add(new City(1, R.drawable.cali_text));
        mCityList.add(new City(2, R.drawable.neiva_text));
        mCityList.add(new City(3, R.drawable.sincelejo_text));
        mCityList.add(new City(4, R.drawable.valledupar_text));
        // mCityList.add(new City(1, R.drawable));

        mCityPagerAdapter = new CityPagerAdapter(this, mCityList);
        mViewPager.setAdapter(mCityPagerAdapter);
        mViewPager.setPageTransformer(false, new ZoomOutSlideTransformer());

        showTutorial();
    }

    public void showTutorial(){

        mCirclePageIndicator.setVisibility(View.VISIBLE);
        mTutorialPager.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        ArrayList<Integer> mTutorialSlides = new ArrayList<>();

        mTutorialSlides.add(R.raw.v01);
        mTutorialSlides.add(R.raw.v02);
        mTutorialSlides.add(R.raw.v02);

        mTutorialAdapter = new TutorialAdapter(this, mTutorialSlides);
        mTutorialPager.setAdapter(mTutorialAdapter);
        mTutorialAdapter.notifyDataSetChanged();
        mTutorialPager.setOffscreenPageLimit(5);
        mTutorialPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(VideoView mAvailVideoView :  mTutorialAdapter.mVideoViewMap.values()){
                    if(mAvailVideoView != null && mAvailVideoView.isPlaying()){
                        mAvailVideoView.stopPlayback();
                    }
                }

                if(position != 2) {

                        mTutorialAdapter.mVideoViewMap.get(position).setVideoPath(((new StringBuilder()).append("android.resource://").append(context.getPackageName()).append("/").append(mTutorialAdapter.data.get(position))).toString());
                        mTutorialAdapter.mVideoViewMap.get(position).setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                mTutorialAdapter.mVideoViewMap.get(position).start();
                                mTutorialAdapter.mVideoViewMap.get(position).setTag(1);
                                mediaPlayer.setLooping(true);
                            }
                        });


                }else{

                    // Save
                    SharedPreferences mSharedPrefs = getSharedPreferences(
                            "com.emdiem.mix.SHARED_PREFERENCES", Context.MODE_PRIVATE);

                    SharedPreferences.Editor mEditor = mSharedPrefs.edit();
                    mEditor.putInt("city", 0);
                    mEditor.putBoolean("init", true);
                    mEditor.apply();

                    Intent mIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mIntent);

                    finish();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mCirclePageIndicator.setViewPager(mTutorialPager);
        mCirclePageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(position == 6){
                    // Make close button appear
                    mTutorialCloseButton.setVisibility(View.VISIBLE);
                }else{
                    mTutorialCloseButton.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });

        mTutorialCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTutorialPager.setVisibility(View.INVISIBLE);
                mTutorialCloseButton.setVisibility(View.INVISIBLE);
                mCirclePageIndicator.setVisibility(View.INVISIBLE);
            }
        });

    }


    public void listen()
    {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        });
    }


    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        menuitem.getItemId();
        return super.onOptionsItemSelected(menuitem);
    }

    public void onResume()
    {
        super.onResume();
    }


}

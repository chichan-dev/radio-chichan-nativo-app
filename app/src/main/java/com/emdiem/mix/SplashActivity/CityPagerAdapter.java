// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.emdiem.mix.SplashActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.viewpager.widget.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;

import com.emdiem.mix.MainActivity.MainActivity;
import com.emdiem.mix.Models.City;
import com.github.skykai.stickercamera.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CityPagerAdapter extends PagerAdapter
{

    List mCityArrayList;
    Context mContext;

    public CityPagerAdapter(Context context, List list)
    {
        mCityArrayList = list;
        mContext = context;
    }

    public void destroyItem(ViewGroup viewgroup, int i, Object obj)
    {
        viewgroup.removeView((View)obj);
    }

    public int getCount()
    {
        return mCityArrayList.size();
    }

    public Object instantiateItem(ViewGroup viewGroup, int i)
    {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.city_item, null);

        final ImageButton mCityButton = (ImageButton)mView.findViewById(R.id.cityButton);

        final City city = (City)mCityArrayList.get(i);

        Picasso.with(mContext)
                .load(city.getCityDrawable())
                .into(mCityButton);

        mCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Click", "click");

                final Animation mAnimation = AnimationUtils.loadAnimation(mContext, R.anim.zoom_in);
                mAnimation.setInterpolator(new DecelerateInterpolator());
                mAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Animation mZoomOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.zoom_out);
                        mAnimation.setInterpolator(new DecelerateInterpolator());
                        mCityButton.startAnimation(mZoomOutAnimation);

                        mZoomOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                // Save
                                SharedPreferences mSharedPrefs = (mContext).getSharedPreferences(
                                        "com.emdiem.mix.SHARED_PREFERENCES", Context.MODE_PRIVATE);

                                SharedPreferences.Editor mEditor = mSharedPrefs.edit();
                                mEditor.putInt("city", city.getCityId());
                                mEditor.putBoolean("init", true);
                                mEditor.apply();

                                Intent mIntent = new Intent(mContext, MainActivity.class);
                                mContext.startActivity(mIntent);

                                // Finish
                                ((Activity)mContext).finish();

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                mCityButton.startAnimation(mAnimation);
            }
        });

        viewGroup.addView(mView, 0);

        return mView;
    }

    public boolean isViewFromObject(View view, Object obj)
    {
        return view == obj;
    }
}

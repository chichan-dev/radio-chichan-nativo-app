// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.emdiem.mix.Models;


public class City
{

    public Integer mCityDrawable;
    public Integer mCityId;

    public City(Integer integer, Integer integer1)
    {
        mCityId = integer;
        mCityDrawable = integer1;
    }

    public Integer getCityDrawable()
    {
        return mCityDrawable;
    }

    public Integer getCityId()
    {
        return mCityId;
    }

    public void setCityDrawable(Integer integer)
    {
        mCityDrawable = integer;
    }

    public void setCityId(Integer integer)
    {
        mCityId = integer;
    }
}

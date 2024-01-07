package com.emdiem.mix.Provider;

import java.util.HashMap;

public class StationProvider
{

    private HashMap mStationMap;

    public StationProvider()
    {
        mStationMap = new HashMap();
        mStationMap.put(Integer.valueOf(0), "https://playerservices.streamtheworld.com/api/livestream-redirect/RNA_BARRANQUILLA.mp3");
//        mStationMap.put(Integer.valueOf(1), "http://master.letio.com/getMD.aspx?gs=9150&streamType=iceDirectMP3&redir=1");
//        mStationMap.put(Integer.valueOf(1), "http://master.letio.com/getMD.aspx?gs=9150&streamType=iceDirectMP3&redir=1");
    }

    public String getStation(Integer integer)
    {
        return (String)mStationMap.get(integer);
    }
}
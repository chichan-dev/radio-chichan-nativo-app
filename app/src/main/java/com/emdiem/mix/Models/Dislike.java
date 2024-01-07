package com.emdiem.mix.Models;

import android.util.Log;

import com.orm.SugarRecord;
import com.parse.ParseCloud;

import java.util.HashMap;
import java.util.List;

public class Dislike extends SugarRecord{

    Boolean dislike;
    String songId;

    public Dislike()
    {
    }

    public Dislike(String s, Boolean boolean1)
    {
        songId = s;
        dislike = boolean1;
    }

    public static void addDislike(String songId, String postId)
    {
        Dislike dislike = new Dislike();
        dislike.dislike = true;
        dislike.songId = songId;
        long l = dislike.save();
        Log.d("Sugar:Dislike", (new StringBuilder()).append("Saved ").append(l).toString());

        // Create like params
        HashMap<String, String> mParams = new HashMap<>();
        mParams.put("songId", postId);

        // Send like in background
        ParseCloud.callFunctionInBackground("dislike", mParams);
    }

    public static Boolean hasDislike(String dislike)
    {
        List<Dislike> mDislikes = Dislike.find(Dislike.class, "song_id = ?", dislike);
        return mDislikes.size() > 0;
    }
}

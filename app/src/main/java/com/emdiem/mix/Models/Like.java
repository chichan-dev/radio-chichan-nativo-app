// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.emdiem.mix.Models;

import android.util.Log;

import com.orm.SugarRecord;
import com.parse.ParseCloud;

import java.util.HashMap;
import java.util.List;

public class Like extends SugarRecord
{

    Boolean liked;
    String postId;

    public Like()
    {
    }

    public Like(String s, Boolean boolean1)
    {
        postId = s;
        liked = boolean1;
    }

    public static void addLike(String postId)
    {
        Like like = new Like();
        like.liked = true;
        like.postId = postId;
        long l = like.save();

        Log.d("Sugar:Like", (new StringBuilder()).append("Saved ").append(l).toString());

        // Create like params
        HashMap<String, String> mParams = new HashMap<>();
        mParams.put("postId", postId);

        // Send like in background
        ParseCloud.callFunctionInBackground("like", mParams);
    }

    public static Boolean hasLike(String postId) {
        // List<Like> mLikes = Like.findWithQuery(Like.class, "post_id = ?", postId);
        List<Like> mLikes;
        try{
            mLikes = Like.findWithQuery(Like.class, "select * from like where post_id = ?", postId);
            //mLikes = Like.findWithQuery(Like.class, "post_id = ?", postId);
            return mLikes.size() > 0;
        }catch (Exception e){
            return false;
        }
    }
}



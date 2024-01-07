package com.emdiem.mix.Models;

import android.util.Log;

import com.orm.SugarRecord;

import java.util.List;

public class SurveyEntry extends SugarRecord {
    Boolean answered;
    String postId;

    public SurveyEntry() {
    }

    /**
     * @param s
     * @param
     */
    public SurveyEntry(String s, Boolean boolean1){
        postId = s;
        answered = boolean1;
    }

    /**
     *
     * @param postId
     */
    public static void addAnswered(String postId){
        SurveyEntry surveyEntry = new SurveyEntry();
        surveyEntry.answered = true;
        surveyEntry.postId = postId;
        long l = surveyEntry.save();
        Log.d("Sugar:SurveyEntry", (new StringBuilder()).append("Saved ").append(l).toString());
    }

    /**
     *
     * @param postId
     * @return
     */
    public static Boolean hasAnswered(String postId) {
        List<SurveyEntry> mEntries = SurveyEntry.find(SurveyEntry.class, "post_id = ?", postId);
        return mEntries.size() > 0;
    }
}

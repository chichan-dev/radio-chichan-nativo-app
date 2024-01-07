package com.emdiem.mix.Utils;

public class EditorItem {
    public String mTitle;
    public Integer mResourceId;

    public Integer mType;

    public Integer getType() {
        return mType;
    }

    public void setType(Integer mType) {
        this.mType = mType;
    }

    public EditorItem(String title, Integer resourceId, Integer type){
        mTitle = title;
        mResourceId = resourceId;
        mType = type;
    }

    public Integer getResourceId() {
        return mResourceId;
    }


    public String getTitle() {
        return mTitle;
    }


    public static final int TEXT = 0;
    public static final int COLOR = 1;
    public static final int POSITION = 2;
    public static final int FILTER = 3;
    public static final int AUDIOS = 4;
    public static final int FONT = 5;
}

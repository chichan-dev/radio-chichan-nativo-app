package com.emdiem.mix.Utils;

public class FontStyle {
    public String mName;
    public int mDrawable;
    public int mType;

    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public int getDrawable() {
        return mDrawable;
    }

    public void setDrawable(int mDrawable) {
        this.mDrawable = mDrawable;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }


    public FontStyle(String name, int drawable, int type){
        mName = name;
        mDrawable = drawable;
        mType = type;
    }


    public class Type{
        public static final int HELVETICA = 0;
        public static final int IMPACT = 1;
        public static final int COURIER = 2;
        public static final int GEORGIA = 3;
    }
}

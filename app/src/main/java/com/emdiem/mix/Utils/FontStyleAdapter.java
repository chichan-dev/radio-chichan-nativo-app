package com.emdiem.mix.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.skykai.stickercamera.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FontStyleAdapter extends BaseAdapter {
    List<FontStyle> mFontStyleList;
    Context mContext;

    private int selectFilter = 0;

    public void setSelectFilter(int selectFilter) {
        this.selectFilter = selectFilter;
    }

    public int getSelectFilter() {
        return selectFilter;
    }

    public FontStyleAdapter(Context context, List<FontStyle> items) {
        mContext = context;
        mFontStyleList = items;

    }

    @Override
    public int getCount() {
        return mFontStyleList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFontStyleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EffectHolder holder = null;

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_bottom_mix, null);
            holder = new EffectHolder();

            holder.image = (ImageView) convertView.findViewById(R.id.small_filter);
            holder.filterName = (TextView) convertView.findViewById(R.id.filter_name);

            convertView.setTag(holder);
        } else {
            holder = (EffectHolder) convertView.getTag();
        }

        final FontStyle mEditorItem = (FontStyle) getItem(position);

        holder.filterName.setText(mEditorItem.getName());

        Picasso.with(mContext)
                .load(mEditorItem.getDrawable())
                .into(holder.image);

        return convertView;
    }

    class EffectHolder {
        ImageView image;
        TextView  filterName;
    }
}
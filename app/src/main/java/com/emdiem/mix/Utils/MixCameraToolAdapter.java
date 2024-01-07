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

public class MixCameraToolAdapter extends BaseAdapter {
    List<EditorItem> mEditorItemList;
    Context mContext;

    private int selectFilter = 0;

    public void setSelectFilter(int selectFilter) {
        this.selectFilter = selectFilter;
    }

    public int getSelectFilter() {
        return selectFilter;
    }

    public MixCameraToolAdapter(Context context, List<EditorItem> items) {
        mContext = context;
        mEditorItemList = items;

    }

    @Override
    public int getCount() {
        return mEditorItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mEditorItemList.get(position);
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

        final EditorItem mEditorItem = (EditorItem) getItem(position);

        holder.filterName.setText(mEditorItem.getTitle());

        Picasso.with(mContext)
                .load(mEditorItem.getResourceId())
                .into(holder.image);

        return convertView;
    }

    class EffectHolder {
        ImageView image;
        TextView  filterName;
    }
}

package com.emdiem.mix.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.github.skykai.stickercamera.R;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MemeToolAdapter  extends BaseAdapter {
    private Context mContext;
    private List<ParseObject> values;
    public static GalleryHolder holder;

    public MemeToolAdapter(Context context, List<ParseObject> values) {
        this.mContext = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final GalleryHolder holder;
        int width = DistanceUtil.getCameraAlbumWidth();
        if (convertView == null) {

            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_meme, null);
            holder = new GalleryHolder();
            holder.sample = (ImageView) convertView.findViewById(R.id.small_filter);

            //ViewGroup mLayoutParams = holder.sample.getLayoutParams();

            // holder.sample.setLayoutParams(new ViewGroup.LayoutParams(width, width));

            // holder.sample.setLayoutParams(new AbsHListView.LayoutParams(width, width));

            convertView.setTag(holder);
        } else {
            holder = (GalleryHolder) convertView.getTag();
        }

        final ParseObject mObject = (ParseObject) getItem(position);
        ParseFile mParseFile = mObject.getParseFile("image");

        if (mParseFile != null)
            Picasso.with(mContext).load(mParseFile.getUrl()).into(holder.sample);


        return convertView;
    }

    class GalleryHolder {
        ImageView sample;
    }
}

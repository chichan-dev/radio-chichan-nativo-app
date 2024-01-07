package com.emdiem.mix.NewsActivity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.skykai.stickercamera.R;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsItemRecyclerAdapter extends RecyclerView.Adapter {

    public Context mContext;
    public List mPostList;

    public NewsItemRecyclerAdapter(Context context, List<ParseObject> postList){
        mContext = context;
        mPostList = postList;
    }

    public static class NewsItemViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView mCover;
        public TextView mTitleText;

        public NewsItemViewHolder(View view)
        {
            super(view);
            mCover = (ImageView)view.findViewById(R.id.cover);
            mTitleText = (TextView)view.findViewById(R.id.titleText);
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewgroup, int i) {
        return new NewsItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.news_item, viewgroup, false));
    }

    /**
     * Binds the view holder
     * @param viewHolder
     * @param i
     */
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i)
    {
        onBindViewHolder((NewsItemViewHolder)viewHolder, i);
    }

    /**
     * View Holder
     * @param holder
     * @param i
     */
    public void onBindViewHolder(final NewsItemViewHolder holder, int i) {

        final ParseObject mParentObject = (ParseObject)mPostList.get(i);
        final ParseObject mInnerObject = mParentObject.getParseObject("newsItem");
        ParseFile mCover = mParentObject.getParseFile("cover");

        if(mCover != null)
            Picasso.with(mContext)
                    .load(mCover.getUrl())
                    .into(holder.mCover);

        holder.mTitleText.setText(mInnerObject.getString("title"));

        holder.mCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((Activity)mContext).finish();

                Intent mIntent = new Intent(mContext, NewsActivity.class);
                mIntent.putExtra("postId", mParentObject.getObjectId());
                mContext.startActivity(mIntent);

            }
        });

    }

    public int getItemCount() {
        return mPostList.size();
    }

}

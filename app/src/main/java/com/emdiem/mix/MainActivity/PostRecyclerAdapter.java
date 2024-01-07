package com.emdiem.mix.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.emdiem.mix.Models.Dislike;
import com.emdiem.mix.Models.Like;
import com.emdiem.mix.NewsActivity.NewsActivity;
import com.emdiem.mix.Service.PlaybackService;
import com.github.skykai.stickercamera.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import xyz.hanks.library.bang.SmallBangView;

public class PostRecyclerAdapter extends RecyclerView.Adapter {

    public Context mContext;
    public List mPostList;
    private SmallBangView mSmallBang;
    public static  SongCardViewHolder  mOngoingSongHolder;


    public ParseObject getInnerParseObject(int i)
    {
        String s = null;

        Log.d("ParseObject", mPostList.get(i).toString());

        switch (getItemViewType(i)){

            case 1:
                s = "contest";
                break;

            case 0:
                s = "survey";
                break;

            case 2:
                s = "newsItem";
                break;

            case 3:
                s = "song";
                break;

        }

        return ((ParseObject)mPostList.get(i)).getParseObject(s);


    }

    public PostRecyclerAdapter(Context context, List list)
    {
        mContext = context;
        mPostList = list;/*
        mSmallBang = SmallBang.attach2Window((Activity) mContext);
        mSmallBang.setColors(new int[] {
                0xffff0000, 0xffff0000
         });*/
    }

    public static class ContestCardViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView mCover;
        public TextView mDateText;
        public ImageView mHeart;
        public LinearLayout mHeartContainer;
        public TextView mTitleText;

        public ContestCardViewHolder(View view)
        {
            super(view);
            mCover = (ImageView)view.findViewById(R.id.cover);
            mTitleText = (TextView)view.findViewById(R.id.titleText);
            mDateText = (TextView)view.findViewById(R.id.dateText);
            mHeartContainer = (LinearLayout)view.findViewById(R.id.heartContainer);
            mHeart = (ImageView)view.findViewById(R.id.heart);
        }
    }

    public static class NewsCardViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView mCover;
        public TextView mDateText;
        public ImageView mHeart;
        public LinearLayout mHeartContainer;
        public TextView mSubject;
        public TextView mTitleText;

        public NewsCardViewHolder(View view)
        {
            super(view);
            mCover = (ImageView)view.findViewById(R.id.cover);
            mTitleText = (TextView)view.findViewById(R.id.titleText);
            mDateText = (TextView)view.findViewById(R.id.dateText);
            mSubject = (TextView)view.findViewById(R.id.subjectText);
            mHeartContainer = (LinearLayout)view.findViewById(R.id.heartContainer);
            mHeart = (ImageView)view.findViewById(R.id.heart);
        }
    }

    public static class SongCardViewHolder extends RecyclerView.ViewHolder
    {

        public TextView mArtistText;
        public ImageView mCover;
        public ImageView mPhoto;
        public SeekBar mSeekBar;
        public TextView mTitleText;
        public ImageButton mPlayPauseButton;
        public ImageButton mThumbsUp;
        public ImageButton mThumbsDown;

        public SongCardViewHolder(View view)
        {
            super(view);
            mCover = (ImageView)view.findViewById(R.id.cover);
            mTitleText = (TextView)view.findViewById(R.id.titleText);
            mArtistText = (TextView)view.findViewById(R.id.artist);
            mPhoto = (ImageView)view.findViewById(R.id.photo);
            mSeekBar = (SeekBar)view.findViewById(R.id.seekbar);
            mPlayPauseButton = (ImageButton)view.findViewById(R.id.playPause);
            mThumbsUp = (ImageButton)view.findViewById(R.id.thumbsUp);
            mThumbsDown = (ImageButton)view.findViewById(R.id.thumbsDown);
        }
    }

    public static class SurveyCardViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView mCover;
        public TextView mDateText;
        public ImageView mHeart;
        public LinearLayout mHeartContainer;
        public TextView mTitleText;

        public SurveyCardViewHolder(View view)
        {
            super(view);
            mCover = (ImageView)view.findViewById(R.id.cover);
            mTitleText = (TextView)view.findViewById(R.id.titleText);
            mDateText = (TextView)view.findViewById(R.id.dateText);
            mHeartContainer = (LinearLayout)view.findViewById(R.id.heartContainer);
            mHeart = (ImageView)view.findViewById(R.id.heart);
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
    {
        switch (i)
        {
            default:
                return null;

            case 1:
                return new ContestCardViewHolder(LayoutInflater.from(mContext).inflate(R.layout.contest_card, viewgroup, false));

            case 0:
                return new SurveyCardViewHolder(LayoutInflater.from(mContext).inflate(R.layout.survey_card, viewgroup, false));

            case 2:
                return new NewsCardViewHolder(LayoutInflater.from(mContext).inflate(R.layout.news_card, viewgroup, false));

            case 3:
                return new SongCardViewHolder(LayoutInflater.from(mContext).inflate(R.layout.song_card, viewgroup, false));
        }
    }


    public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int i)
    {
        switch (getItemViewType(i))
        {
            default:
                return;

            case 1:
                onBindContestCardViewHolder((ContestCardViewHolder)viewholder, i);
                return;

            case 0:
                onBindSurveyCardViewHolder((SurveyCardViewHolder)viewholder, i);
                return;

            case 2:
                onBindNewsCardViewHolder((NewsCardViewHolder)viewholder, i);
                return;

            case 3:
                onBindSongCardViewHolder((SongCardViewHolder)viewholder, i);
                break;
        }
    }

    public void onBindContestCardViewHolder(final ContestCardViewHolder holder, int i) {

        final ParseObject mParentObject = (ParseObject)mPostList.get(i);
        final ParseObject mInnerObject = getInnerParseObject(i);
        ParseFile mParseFile = ((ParseObject)mPostList.get(i)).getParseFile("cover");

        if (mParseFile != null)
            Picasso.with(mContext).load(mParseFile.getUrl()).into(holder.mCover);

        mInnerObject.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy - MM - dd");
                    mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                    holder.mTitleText.setText(object.getString("title"));
                    holder.mDateText.setText(mSimpleDateFormat.format(object.getCreatedAt()));

            }
        });


        if (Like.hasLike(mParentObject.getObjectId())) {
            holder.mHeart.setImageResource(R.drawable.ic_favorite_24dp);
        } else {
            holder.mHeart.setImageResource(R.drawable.ic_favorite_outline_24dp);
        }

        holder.mCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContestFormDialogFragment(mParentObject.getObjectId());
            }
        });


        holder.mHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Like.hasLike(mParentObject.getObjectId())) {

                    Like.addLike(mParentObject.getObjectId());
/*
                    mSmallBang.bang(holder.mHeart, new SmallBangListener() {
                        public void onAnimationEnd() {

                        }

                        public void onAnimationStart() {
                            holder.mHeart.setImageResource(R.drawable.ic_favorite_24dp);
                        }
                    });*/
                }
            }
        });

    }


    /**
     * Contest form dialog fragment
     * @param postId
     */
    private void showContestFormDialogFragment(String postId) {
        ContestFormDialogFragment mContestFormDialogFragment = ContestFormDialogFragment.newInstance(postId);
        mContestFormDialogFragment.show(((AppCompatActivity) mContext).getFragmentManager(), "");
    }

    /**
     * Survey form dialog fragment
     * @param postId
     */
    private void showSurveyFormDialogFragment(String postId) {
        SurveyFormDialogFragment mSurveyFormDialogFragment = SurveyFormDialogFragment.newInstance(postId);
        mSurveyFormDialogFragment.show(((AppCompatActivity) mContext).getFragmentManager(), "");
    }


    public void onBindSurveyCardViewHolder(final SurveyCardViewHolder holder, int i) {

        final ParseObject mParentObject = (ParseObject)mPostList.get(i);
        final ParseObject mInnerObject = getInnerParseObject(i);
        ParseFile mParseFile = ((ParseObject)mPostList.get(i)).getParseFile("cover");

        if (mParseFile != null)
            Picasso.with(mContext).load(mParseFile.getUrl()).into(holder.mCover);

        mInnerObject.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy - MM - dd");
                mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                holder.mTitleText.setText(object.getString("title"));
                holder.mDateText.setText(mSimpleDateFormat.format(object.getCreatedAt()));

            }
        });


        if (Like.hasLike(mParentObject.getObjectId())) {
            holder.mHeart.setImageResource(R.drawable.ic_favorite_24dp);
        } else {
            holder.mHeart.setImageResource(R.drawable.ic_favorite_outline_24dp);
        }

        holder.mCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSurveyFormDialogFragment(mParentObject.getObjectId());
            }
        });


        holder.mHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Like.hasLike(mParentObject.getObjectId())) {

                    Like.addLike(mParentObject.getObjectId());
/*
                    mSmallBang.bang(holder.mHeart, new SmallBangListener() {
                        public void onAnimationEnd() {

                        }

                        public void onAnimationStart() {
                            holder.mHeart.setImageResource(R.drawable.ic_favorite_24dp);
                        }
                    });
                    */
                }
            }
        });

    }


    public void onBindNewsCardViewHolder(final NewsCardViewHolder holder, int i) {

        final ParseObject mParentObject = (ParseObject)mPostList.get(i);
        final ParseObject mInnerObject = getInnerParseObject(i);
        ParseFile mParseFile = ((ParseObject)mPostList.get(i)).getParseFile("cover");

        if (mParseFile != null)
            Picasso.with(mContext).load(mParseFile.getUrl()).into(holder.mCover);

        mInnerObject.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy - MM - dd");
                mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                holder.mTitleText.setText(object.getString("title"));
                holder.mDateText.setText(mSimpleDateFormat.format(object.getCreatedAt()));
                holder.mSubject.setText(object.getString("subject"));

            }
        });


        if (Like.hasLike(mParentObject.getObjectId())) {
            holder.mHeart.setImageResource(R.drawable.ic_favorite_24dp);
        } else {
            holder.mHeart.setImageResource(R.drawable.ic_favorite_outline_24dp);
        }


        holder.mCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mIntent = new Intent(mContext, NewsActivity.class);
                mIntent.putExtra("postId", mParentObject.getObjectId());
                mContext.startActivity(mIntent);

            }
        });


        holder.mHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Like.hasLike(mParentObject.getObjectId())) {
                    holder.mHeart.setImageResource(R.drawable.ic_favorite_24dp);
                    Like.addLike(mParentObject.getObjectId());

/*
                    mSmallBang.bang(holder.mHeart, new SmallBangListener() {

                        public void onAnimationEnd() {

                        }

                        public void onAnimationStart() {
                            holder.mHeart.setImageResource(R.drawable.ic_favorite_24dp);
                        }

                    });
                    */
                }
            }
        });

    }

    /**
     *
     * @param holder
     * @param i
     */
    public void onBindSongCardViewHolder(final SongCardViewHolder holder, int i) {

        final ParseObject mParentObject = (ParseObject)mPostList.get(i);


        final ParseObject mParseObject = getInnerParseObject(i);


        holder.mSeekBar.setThumb(null);
        holder.mSeekBar.setProgress(0);

        ParseFile parsefile = ((ParseObject)mPostList.get(i)).getParseFile("cover");


        if (parsefile != null)
            Picasso.with(mContext).load(parsefile.getUrl()).into(holder.mCover);


        if (Like.hasLike(mParentObject.getObjectId())) {
            holder.mThumbsUp.setImageResource(R.drawable.ic_manito_arribax2);
        } else {
            holder.mThumbsUp.setImageResource(R.drawable.ic_manito_arriba_outlinex2);
        }


        if (Dislike.hasDislike(mParentObject.getObjectId())) {
            holder.mThumbsDown.setImageResource(R.drawable.ic_manito_abajox2);
        } else {
            holder.mThumbsDown.setImageResource(R.drawable.ic_manito_abajo_outline);
        }

        mParseObject.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                holder.mTitleText.setText(object.getString("title"));
                ParseFile mPhoto = object.getParseFile("photo");

                if (mParseObject != null && mPhoto != null)
                    Picasso.with(mContext)
                            .load(mPhoto.getUrl())
                            .transform(new CropCircleTransformation())
                            .into(holder.mPhoto);

                holder.mArtistText.setText(object.getString("artist"));

            }
        });


        holder.mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mOngoingSongHolder = holder;
                ParseFile mSong = mParseObject.getParseFile("song");

                Log.d("Song", mSong.getUrl());
                Intent intent = new Intent(mContext, PlaybackService.class);

                if (holder.mPlayPauseButton.getTag() == "p") {

                    intent.setAction("force-stop");
                    mContext.startService(intent);

                    holder.mPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_24dp);
                    holder.mPlayPauseButton.setTag("s");
                    ((MainActivity)mContext).setPlayButtonStatus(false);


                } else {

                    intent.setAction("play-track");
                    intent.putExtra("track", mSong.getUrl());
                    intent.putExtra("handler", new Messenger(new PlaybackServiceHandler()));


                    mContext.startService(intent);

                    // Then fetch the progress
                    new Timer().scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {

                            intent.setAction("get-progress");
                            mContext.startService(intent);

                        }
                    }, 0, 1000);



                    holder.mPlayPauseButton.setImageResource(R.drawable.ic_pause_24dp);
                    holder.mPlayPauseButton.setTag("p");
                }


            }
        });


        holder.mThumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Like.hasLike(mParentObject.getObjectId())) {

                    Like.addLike(mParentObject.getObjectId());
/*
                    mSmallBang.bang(holder.mThumbsUp, new SmallBangListener() {

                        public void onAnimationEnd() {

                        }

                        public void onAnimationStart() {
                            holder.mThumbsUp.setImageResource(R.drawable.ic_manito_arribax2);
                        }

                    });
                    */
                }




            }
        });


        holder.mThumbsDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Dislike.hasDislike(mParentObject.getObjectId())) {

                    Dislike.addDislike(mParentObject.getObjectId(), mParseObject.getObjectId());
/*
                    mSmallBang.bang(holder.mThumbsDown, new SmallBangListener() {

                        public void onAnimationEnd() {

                        }

                        public void onAnimationStart() {
                             holder.mThumbsDown.setImageResource(R.drawable.ic_manito_abajox2);
                        }

                    });
                    */
                }


            }
        });



    }

    public int getItemViewType(int i) {
        ParseObject parseobject = (ParseObject)mPostList.get(i);
        if (parseobject.has("survey") && parseobject.get("survey") != null) {
            return 0;
        }
        if (parseobject.has("contest") && parseobject.get("contest") != null) {
            return 1;
        }
        if (parseobject.has("newsItem") && parseobject.get("newsItem") != null) {
            return 2;
        }
        return !parseobject.has("song") || parseobject.get("song") == null ? 4 : 3;
    }


    public int getItemCount() {
        return mPostList.size();
    }

    private static class PlaybackServiceHandler extends Handler
    {

        public void handleMessage(Message message)
        {
            Bundle mData = message.getData();

            Boolean justPlaying = mData.getBoolean("justPlaying");
            Boolean isPlaying = mData.getBoolean("playing");
            int progress =  mData.getInt("progress");


            Log.d("ONGOGING", isPlaying + "");
            Log.d("ONGOGING", progress + "");

            if(mOngoingSongHolder != null){
                mOngoingSongHolder.mSeekBar.setProgress(progress);
            }


            /**if(!isPlaying){
                mOngoingSongHolder.mPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_24dp);
                mOngoingSongHolder.mPlayPauseButton.setTag("s");
            }**/


            /**if(mActivity != null){
                // Activity is running
                mActivity.isPlaying = isPlaying;
                mActivity.setPlayButtonStatus(justPlaying || isPlaying);

            }**/
        }

    }

}

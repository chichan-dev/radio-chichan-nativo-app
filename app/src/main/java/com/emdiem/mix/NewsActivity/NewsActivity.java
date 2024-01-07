package com.emdiem.mix.NewsActivity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.skykai.stickercamera.R;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sliding.MultiShrinkScroller;
import com.sliding.SlidingActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class NewsActivity extends SlidingActivity {

    public String mPostObjectId;
    public ParseObject mPostObject;
    public ParseObject mInnerObject;
    public Target mCoverLoadTarget;
    public Context mContext;
    public ImageView mCover;
    public RelativeLayout mCoverContainer;
    public ImageView mRectangleView;
    public TextView mTitleText;
    public TextView mSubtitleText;
    public TextView mBodyText;
    public TextView mDateText;
    public FloatingActionButton mShareButton;
    public ImageView mBackButton;
    public RecyclerView mRelatedArticles;
    public NewsItemRecyclerAdapter mRecyclerAdapter;
    public String mVideoUrl;
    public WebView mWebView;


    @Override
    public void init(Bundle savedInstanceState) {
        setTitle("Activity Title");

        mPostObjectId = getIntent().getExtras().getString("postId");
        mContext = this;

        setPrimaryColors( getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark) );
        setContent(R.layout.content_news);

        enableFullscreen();
        disableHeader();


        mCover = (ImageView)findViewById(R.id.cover);
        mCoverContainer = (RelativeLayout)findViewById(R.id.coverContainer);
        mRectangleView = (ImageView)findViewById(R.id.rectangleView);

        mTitleText = (TextView)findViewById(R.id.titleText);
        mSubtitleText = (TextView)findViewById(R.id.subtitleText);
        mDateText = (TextView)findViewById(R.id.dateText);
        mBodyText = (TextView)findViewById(R.id.bodyText);
        mBackButton = (ImageView)findViewById(R.id.backButton);

        mRelatedArticles = (RecyclerView)findViewById(R.id.relatedArticles);
        mWebView = (WebView)findViewById(R.id.webView);
        mShareButton = (FloatingActionButton)findViewById(R.id.fab);

        load();
        listen();

    }

    public void load(){

        ParseQuery<ParseObject> mParseQuery = new ParseQuery<>("Post");
        mParseQuery.include("newsItem");
        mParseQuery.whereEqualTo("objectId", mPostObjectId);

        mParseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null) {
                    mPostObject = object;
                    mInnerObject  = object.getParseObject("newsItem");
                    setup();
                }
            }
        });

    }

    public void listen(){

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("View", "Share");
                share();
            }
        });
    }

    @Override
    protected void configureScroller(MultiShrinkScroller scroller) {
        super.configureScroller(scroller);
        scroller.setIntermediateHeaderHeightRatio(1);
    }

    public void share(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mInnerObject.getString("shortDescription") + " \n\r ¡Descarga Mix desde la Play Store o App Store! ¿Qué estás esperando?" );
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Compartir vía..."));
    }

    public void setup(){
        mTitleText.setText(mInnerObject.getString("title"));
        mSubtitleText.setText(mInnerObject.getString("shortDescription"));

        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy - MM - dd");
        mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        mDateText.setText(mSimpleDateFormat.format(mInnerObject.getCreatedAt()));
        mBodyText.setText(mInnerObject.getString("description"));

        mVideoUrl = mInnerObject.getString("videoUrl");

        mCoverLoadTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                // setImage(bitmap)
                mCover.setImageBitmap(bitmap);
                /**mCoverContainer.setRotation(15);
                mCover.setRotation(-15);
                mCover.setImageBitmap(bitmap);**/

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(mContext)
                .load(R.drawable.rectangle)
                .fit()
                .into(mRectangleView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ParseFile mCoverFile = mPostObject.getParseFile("cover");

                Picasso.with(mContext)
                        .load(mCoverFile.getUrl())
                        .into(mCoverLoadTarget);

            }
        }, 500);

        // Setup things using innerParseObject
        setTitle(mInnerObject.getString("title"));

        Map<String, String> mParams = new HashMap<>();

        mParams.put("postId", mPostObjectId);

        ParseCloud.callFunctionInBackground("getSimilar", mParams, new FunctionCallback<Object>() {
            @Override
            public void done(Object object, ParseException e) {
                mRecyclerAdapter = new NewsItemRecyclerAdapter(mContext, (List<ParseObject>)object);

                LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

                mRelatedArticles.setLayoutManager(mLinearLayoutManager);
                mRelatedArticles.setAdapter(mRecyclerAdapter);

            }
        });


        if (mVideoUrl != null) {
            Log.d("VideoUrl", mVideoUrl);
            WebSettings settings = mWebView.getSettings();
            settings.setDefaultTextEncodingName("utf-8");
            //mWebView.loadData("<html><head> <style type=\"text/css\"> body { background-color: transparent; color: white; } </style> </head><body style=\"margin:0\"> <embed id=\"yt\" src=\"http://www.youtube.com/embed/" + mVideoUrl + "\" type=\"application/x-shockwave-flash\"  width=\"100\" height=\"100\"></embed> </body></html>", "text/html", null);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.loadData("<iframe style=\"width: 100%; height: 100%\" src=\"https://www.youtube.com/embed/" + mVideoUrl + "\" frameborder=\"0\" allowfullscreen></iframe>", "text/html", null);

        }else{
            ViewGroup.LayoutParams mLayoutParams = mWebView.getLayoutParams();
            mLayoutParams.height = 0;
        }

    }

    @Override
    public void onStop(){
        super.onStop();
        if(mWebView != null){
            mWebView.loadData("", "text/html", null);
        }
    }


}

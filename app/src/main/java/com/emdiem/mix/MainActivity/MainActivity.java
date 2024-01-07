package com.emdiem.mix.MainActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.emdiem.mix.Service.PlaybackService;
import com.emdiem.mix.Utils.GifMovieView;
import com.github.skykai.stickercamera.R;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.List;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class MainActivity extends AppCompatActivity {


    private Context mContext;
    private GifMovieView mGifMovieView;
    private TextView mMarqueeText;
    private ImageButton mPlayButton;
    private PostRecyclerAdapter mPostRecyclerAdapter;
    private Handler mRadioMessageHandler;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;

    public FloatingActionButton mFab;

    public static MainActivity mActivity;
    public boolean isPlaying;

    public static final int PERMISSION_REQUEST_CAMERA = 11;
    public static final int PERMISSION_REQUEST_STORAGE_READ = 12;
    public static final int PERMISSION_REQUEST_STORAGE_WRITE = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mActivity = this;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        mGifMovieView = (GifMovieView)findViewById(R.id.gifMovieView);
        mPlayButton = (ImageButton)findViewById(R.id.playButton);
        mMarqueeText = (TextView)findViewById(R.id.marquee);
        mFab = (FloatingActionButton)findViewById(R.id.fab);

        setup();
        load();
        listen();
    }

    public void setup() {

        // Because we can't run a service without a handler, baby
        Intent intent = new Intent(this, PlaybackService.class);
        intent.setAction("attach");
        intent.putExtra("handler", new Messenger(new PlaybackServiceHandler()));
        startService(intent);


        mGifMovieView.setMovieResource(R.drawable.eqx);
        mGifMovieView.setPaused(true);

        mPlayButton.setTag("s");
//        requestCameraPermission();
    }


    /**
     * Requests camera permission
     * Explanation not
     */

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("Permission", "CAM PERMISSION GRANTED");

                } else {

                    Log.d("Permission", "CAM PERMISSION DENIED");
                    mFab.setVisibility(View.INVISIBLE);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

            case PERMISSION_REQUEST_STORAGE_WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {


                    Log.d("Permission", "WRITE PERMISSION GRANTED");

                } else {

                    Log.d("Permission", "WRITE PERMISSION DENIED");
                    mFab.setVisibility(View.INVISIBLE);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

            case PERMISSION_REQUEST_STORAGE_READ: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {


                    Log.d("Permission", "READ PERMISSION GRANTED");

                } else {

                    Log.d("Permission", "READ PERMISSION DENIED");
                    mFab.setVisibility(View.INVISIBLE);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void listen() {
        mPlayButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, PlaybackService.class);

                if (mPlayButton.getTag() == "p") {
                    mIntent.setAction("force-stop");
                    mPlayButton.setTag("s");
                    mGifMovieView.setPaused(true);
                    mPlayButton.setImageResource(R.drawable.ic_play_arrow_60dp);
                }else{
                    mIntent.setAction("play");
                    mIntent.putExtra("stationId", 0);
                    mPlayButton.setTag("p");
                    mGifMovieView.setPaused(false);
                    mPlayButton.setImageResource(R.drawable.ic_stop_blue_24dp);
                }

                startService(mIntent);
            }

        });

//        mFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent mIntent = new Intent(MainActivity.this, MyCameraActivity.class);
//                startActivity(mIntent);
//
//            }
//        });
    }

    public void load()
    {
        ParseQuery<ParseObject> mParseQuery = new ParseQuery<>("Marquee");
        mParseQuery.whereEqualTo("active", true);

        SharedPreferences prefs = this.getSharedPreferences("com.emdiem.mix.SHARED_PREFERENCES", Context.MODE_PRIVATE);
        int city = prefs.getInt("city", 0);
        mParseQuery.whereEqualTo("city", city);

        mParseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseobject, ParseException parseexception) {
                if(parseexception == null) {
                    System.out.println("ParseObject: " + parseobject);
                    if(parseobject != null) {
                        System.out.println("Text: " + parseobject.getString("text"));
                        mMarqueeText.setText(parseobject.getString("text"));
                        mMarqueeText.setSelected(true);
                    } else {
                        System.out.println("ParseObject is null");
                    }
                } else {
                    System.out.println("ParseException: " + parseexception.getMessage());
                }
            }
        });

        HashMap<String, Object> mParams = new HashMap<>();

        mParams.put("installation", ParseInstallation.getCurrentInstallation().getObjectId());
        mParams.put("city", 0);

        ParseCloud.callFunctionInBackground("getPosts", mParams, new FunctionCallback<Object>() {
            @Override
            public void done(Object obj1, ParseException e) {
                if (e != null) {
                    Log.d("Exception", e.getMessage());
                    return;
                }

                mPostRecyclerAdapter = new PostRecyclerAdapter(mContext, ((List) (obj1)));
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, 1, false));
                mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

//                mRecyclerView.setAdapter(mPostRecyclerAdapter);

                AlphaInAnimationAdapter mAlphaAdapter = new AlphaInAnimationAdapter(mPostRecyclerAdapter, 0f);
                mAlphaAdapter.setFirstOnly(false);
                ScaleInAnimationAdapter mScaleAnimationAdapter = new ScaleInAnimationAdapter(mAlphaAdapter);
                mScaleAnimationAdapter.setFirstOnly(false);
                mScaleAnimationAdapter.setInterpolator(new OvershootInterpolator());

                mRecyclerView.setAdapter(mScaleAnimationAdapter);

//                mRecyclerView.setAdapter(mPostRecyclerAdapter);
                mPostRecyclerAdapter.notifyDataSetChanged();
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setItemViewCacheSize(20);
            }
        });
    }

    public void setPlayButtonStatus(boolean p){
        if(p) {
            mPlayButton.setImageResource(R.drawable.ic_stop_blue_24dp);
            mGifMovieView.setPaused(false);
            mPlayButton.setTag("p");
        }else {
            mPlayButton.setImageResource(R.drawable.ic_play_arrow_60dp);
            mGifMovieView.setPaused(true);
            mPlayButton.setTag("s");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class PlaybackServiceHandler extends Handler
    {

        public void handleMessage(Message message)
        {
            Bundle mData = message.getData();

            Boolean justPlaying = mData.getBoolean("justPlaying");
            Boolean isPlaying = mData.getBoolean("playing");

            if(mActivity != null){
                // Activity is running
                mActivity.isPlaying = isPlaying;
                mActivity.setPlayButtonStatus(justPlaying || isPlaying);

            }
        }

    }
}

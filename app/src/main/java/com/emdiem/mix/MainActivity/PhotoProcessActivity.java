package com.emdiem.mix.MainActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.customview.LabelSelector;
import com.customview.LabelView;
import com.customview.MyHighlightView;
import com.customview.MyImageViewDrawableOverlay;
import com.emdiem.mix.App;
import com.emdiem.mix.Utils.AppConstants;
import com.emdiem.mix.Utils.AudioToolAdapter;
import com.emdiem.mix.Utils.CourierTextView;
import com.emdiem.mix.Utils.EditorItem;
import com.emdiem.mix.Utils.EffectUtil;
import com.emdiem.mix.Utils.FileUtils;
import com.emdiem.mix.Utils.FontStyle;
import com.emdiem.mix.Utils.FontStyleAdapter;
import com.emdiem.mix.Utils.GeorgiaTextView;
import com.emdiem.mix.Utils.HelveticaTextView;
import com.emdiem.mix.Utils.ImageUtils;
import com.emdiem.mix.Utils.MemeLabelDialogFragment;
import com.emdiem.mix.Utils.MemeTextView;
import com.emdiem.mix.Utils.MixCameraToolAdapter;
import com.emdiem.mix.Utils.TagItem;
import com.emdiem.mix.Utils.TimeUtils;
import com.github.skykai.stickercamera.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jcodec.common.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.widget.HListView;

public class PhotoProcessActivity extends AppCompatActivity {

    //滤镜图片
//    @BindView(R.id.gpuimage)
    ImageView mGPUImageView;
    //绘图区域
//    @BindView(R.id.drawing_view_container)
    ViewGroup drawArea;
    //底部按钮
    /**@InjectView(R.id.sticker_btn)
    TextView stickerBtn;
     @InjectView(R.id.filter_btn)
     TextView filterBtn;
     @InjectView(R.id.text_btn)
     TextView labelBtn;**/
    //工具区
//    @BindView(R.id.list_tools)
    HListView bottomToolBar;
//    @BindView(R.id.toolbar_area)
    ViewGroup toolArea;
    private MyImageViewDrawableOverlay mImageView;
    private LabelSelector labelSelector;
    private TextView currentBtn;
    private Bitmap currentBitmap;
    private Bitmap smallImageBackgroud;
    private LabelView emptyLabelView;
    private List<LabelView> labels = new ArrayList<>();
    private View commonLabelArea;
    private MediaPlayer mMediaPlayer;
    public ProgressDialog mProgressDialog;
    public ParseFile mSelectedAudio;

    public MemeTextView mTopText;
    public MemeTextView mBottomText;

    public HelveticaTextView mTopHelveText;
    public HelveticaTextView mBottomHelveText;


    public GeorgiaTextView mTopGeorgiaTextView;
    public GeorgiaTextView mBottomGeorgiaTextView;


    public CourierTextView mTopCourierTextView;
    public CourierTextView mBottomCourierTextView;


    public ImageButton mShareButton, mEdit;
    public static PhotoProcessActivity mActivity;

//    @BindView(R.id.watermark)
    ImageView mWaterMark;


    public int currentToolbar = 0;
    public Target mWatermarkLoadTarget;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_process);


        ButterKnife.bind(this);
        // EffectUtil.clear();
        drawArea = findViewById(R.id.drawing_view_container);
        mGPUImageView = findViewById(R.id.gpuimage);
        bottomToolBar = findViewById(R.id.list_tools);
        toolArea = findViewById(R.id.toolbar_area);
        mWaterMark = findViewById(R.id.watermark);
        initView();
        initEvent();



        mActivity = this;

        //  mLoadingView = (RelativeLayout)findViewById(R.id.loadingView);
        initMainToolbar();
        ImageUtils.asyncLoadImage(this, getIntent().getData(), new ImageUtils.LoadImageCallback() {
            @Override
            public void callback(Bitmap result) {

                currentBitmap = result;
                mGPUImageView.setImageBitmap(currentBitmap);

            }
        });

        Picasso.with(PhotoProcessActivity.this)
                .load(getIntent().getData())
                .into(mGPUImageView);


        ImageUtils.asyncLoadSmallImage(this, getIntent().getData(), new ImageUtils.LoadImageCallback() {
            @Override
            public void callback(Bitmap result) {
                smallImageBackgroud = result;
            }
        });



        androidx.appcompat.app.ActionBar mSupportActionBar = getSupportActionBar();

        if(mSupportActionBar != null){
            mSupportActionBar.setTitle("");
            mSupportActionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mSupportActionBar.setDisplayHomeAsUpEnabled(true);


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                mSupportActionBar.setHomeAsUpIndicator(R.drawable.ic_back_24dp);
            }




        }

        mTopText = (MemeTextView)findViewById(R.id.topText);
        mBottomText = (MemeTextView)findViewById(R.id.bottomText);

        mTopHelveText = (HelveticaTextView)findViewById(R.id.topHelveText);
        mBottomHelveText = (HelveticaTextView)findViewById(R.id.bottomHelveText);

        mTopGeorgiaTextView = (GeorgiaTextView)findViewById(R.id.topGeorgiaText);
        mBottomGeorgiaTextView = (GeorgiaTextView)findViewById(R.id.bottomGeorgiaText);

        mTopCourierTextView = (CourierTextView)findViewById(R.id.topCourierText);
        mBottomCourierTextView = (CourierTextView)findViewById(R.id.bottomCourierText);

        mShareButton = (ImageButton)findViewById(R.id.shareButton);
        mEdit = findViewById(R.id.editText);


        ViewTreeObserver observer= mShareButton.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Log.d("ToolAreaHeight", toolArea.getHeight() + "px");

                Display display = getWindowManager().getDefaultDisplay();

                Point size = new Point();
                display.getSize(size);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(size.x - (size.x / 6), drawArea.getHeight() - (mShareButton.getHeight() / 2), 0, 0);

                mShareButton.setLayoutParams(lp);


                Log.d("Button", "Setting button position");

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


            }
        }, 100);


        mWatermarkLoadTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                mWaterMark.setImageBitmap(bitmap); // Load
                captureView(R.id.drawing_view_container, "mix.png"); // Take
                mWaterMark.setImageBitmap(null); // Unload

                if(mSelectedAudio != null) {

                    /*Intent intent = new Intent(PhotoProcessActivity.this, VideoCreationService.class);
                    intent.setAction("make");

                    intent.putExtra("image", Environment.getExternalStorageDirectory().toString() + "/mix.png");
                    intent.putExtra("audio", mSelectedAudio.getUrl());

                    intent.putExtra("handler", new Messenger(new VideoCreationServiceHandler()));

                    Log.d("Image", Environment.getExternalStorageDirectory().toString() + "/mix.png");
                    Log.d("Audio", mSelectedAudio.getUrl());

                    startService(intent);*/

                }else{
                    // Share, only image
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    Uri screenshotUri = Uri.parse(Environment.getExternalStorageDirectory().toString() + "/mix.png");

                    sharingIntent.setType("image/png");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                    startActivity(Intent.createChooser(sharingIntent, "Compartir"));

                }

                if(mProgressDialog != null)
                    mProgressDialog.dismiss();


            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLabelsDialog();
            }
        });

        drawArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLabelsDialog();
            }
        });

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mMediaPlayer != null && mMediaPlayer.isPlaying())
                    mMediaPlayer.stop();


                mProgressDialog = ProgressDialog.show(PhotoProcessActivity.this, "Generando", "Porfavor espere...");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                Picasso.with(PhotoProcessActivity.this)
                        .load(R.drawable.water)
                        .resize(740, 740)
                        .into(mWatermarkLoadTarget);

            }
        });


        drawArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


    }

    private void initMainToolbar(){
        currentToolbar = 0;
        MixCameraToolAdapter mMixCameraToolAdapter = new MixCameraToolAdapter(this, EffectUtil.itemList);

        bottomToolBar.setAdapter(mMixCameraToolAdapter);
        bottomToolBar.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> arg0,
                                    View arg1, int arg2, long arg3) {

                EditorItem mEditorItem = (EditorItem) mMixCameraToolAdapter.getItem(arg2);



                switch (mEditorItem.getType()) {
                    case EditorItem.TEXT:
                        initFontStyleToolbar();

                        /**showLabelsDialog();**/

                        break;
                    case EditorItem.COLOR:
                        break;
                    case EditorItem.FILTER:
                        break;
                    case EditorItem.AUDIOS:
                        initAudioToolbar();
                        break;
                }


                /**labelSelector.hide();
                 Addon sticker = EffectUtil.addonList.get(arg2);
                 EffectUtil.addStickerImage(mImageView, PhotoProcessActivity.this, sticker,
                 new EffectUtil.StickerCallback() {
                @Override public void onRemoveSticker(Addon sticker) {
                labelSelector.hide();
                }
                });**/

            }
        });
    }

    private void initAudioToolbar(){
        currentToolbar = 1;

        mProgressDialog = ProgressDialog.show(this, "Descargando datos", "Porfavor espere...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        ParseQuery<ParseObject> mQuery = new ParseQuery<>("Audio");

        SharedPreferences prefs = this.getSharedPreferences("com.emdiem.mix.SHARED_PREFERENCES", Context.MODE_PRIVATE);
        int city = prefs.getInt("city", 0);

        mQuery.whereEqualTo("city", city);

        // mLoadingView.setVisibility(View.VISIBLE);
        mQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {


                // mLoadingView.setVisibility(View.INVISIBLE);

                if (mProgressDialog != null)
                    mProgressDialog.dismiss();


                if (e == null && objects != null) {

                    AudioToolAdapter mAudioToolAdapter = new AudioToolAdapter(PhotoProcessActivity.this, objects);

                    bottomToolBar.setAdapter(mAudioToolAdapter);
                    bottomToolBar.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> arg0,
                                                View arg1, int arg2, long arg3) {

                            ParseObject mAudio = (ParseObject) mAudioToolAdapter.getItem(arg2);
                            ParseFile mAudioFile = mAudio.getParseFile("audio");


                            if (mAudioFile != null) {

                                if (mMediaPlayer != null && mMediaPlayer.isPlaying())
                                    mMediaPlayer.stop();


                                mMediaPlayer = new MediaPlayer();

                                mSelectedAudio = mAudioFile;

                                mProgressDialog = ProgressDialog.show(PhotoProcessActivity.this, "Cargando audio", "Un momento...");

                                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


                                try {
                                    mMediaPlayer.setDataSource(PhotoProcessActivity.this, Uri.parse(mAudioFile.getUrl()));
                                    mMediaPlayer.prepareAsync();
                                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mediaPlayer) {
                                            //  mLoadingView.setVisibility(View.INVISIBLE);
                                            if (mProgressDialog != null)
                                                mProgressDialog.dismiss();

                                            mMediaPlayer.start();
                                        }
                                    });
                                } catch (IOException exception) {
                                    Log.d("IOException", exception.getMessage());
                                }
                            }

                        }
                    });


                }

            }
        });


    }

    private void initFontStyleToolbar(){
        currentToolbar = 2;
        FontStyleAdapter mMixCameraToolAdapter = new FontStyleAdapter(this, EffectUtil.fontStyleList);

        bottomToolBar.setAdapter(mMixCameraToolAdapter);
        bottomToolBar.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> arg0,
                                    View arg1, int arg2, long arg3) {

                FontStyle mEditorItem = (FontStyle) mMixCameraToolAdapter.getItem(arg2);


                Log.d("FontStyle", mEditorItem.getName());

                mTopText.setVisibility(View.INVISIBLE);
                mBottomText.setVisibility(View.INVISIBLE);

                mTopCourierTextView.setVisibility(View.INVISIBLE);
                mBottomCourierTextView.setVisibility(View.INVISIBLE);

                mTopGeorgiaTextView.setVisibility(View.INVISIBLE);
                mBottomGeorgiaTextView.setVisibility(View.INVISIBLE);

                mTopHelveText.setVisibility(View.INVISIBLE);
                mBottomHelveText.setVisibility(View.INVISIBLE);

                switch (mEditorItem.getType()) {
                    case FontStyle.Type.COURIER:
                        mTopText.setVisibility(View.VISIBLE);
                        mBottomText.setVisibility(View.VISIBLE);
                        break;
                    case FontStyle.Type.GEORGIA:
                        mTopGeorgiaTextView.setVisibility(View.VISIBLE);
                        mBottomGeorgiaTextView.setVisibility(View.VISIBLE);
                        break;
                    case FontStyle.Type.HELVETICA:
                        mTopHelveText.setVisibility(View.VISIBLE);
                        mBottomHelveText.setVisibility(View.VISIBLE);
                        //initAudioToolbar();
                        break;
                }

                initMainToolbar();

                /**labelSelector.hide();
                 Addon sticker = EffectUtil.addonList.get(arg2);
                 EffectUtil.addStickerImage(mImageView, PhotoProcessActivity.this, sticker,
                 new EffectUtil.StickerCallback() {
                @Override public void onRemoveSticker(Addon sticker) {
                labelSelector.hide();
                }
                });**/

            }
        });
        //setCurrentBtn(stickerBtn);
    }

    private void showLabelsDialog() {

        MemeLabelDialogFragment mMemeLabelDialogFragment = MemeLabelDialogFragment.newInstance(mTopText.getText().toString(), mBottomText.getText().toString());
        mMemeLabelDialogFragment.show(getFragmentManager(), "diag");

        mMemeLabelDialogFragment.setOnDismissListener(new MemeLabelDialogFragment.OnDismissListener() {
            @Override
            public void onDismiss() {

                mTopText.setText(mMemeLabelDialogFragment.getTopText());
                mBottomText.setText(mMemeLabelDialogFragment.getBottomText());

                mTopGeorgiaTextView.setText(mMemeLabelDialogFragment.getTopText());
                mBottomGeorgiaTextView.setText(mMemeLabelDialogFragment.getBottomText());

                mTopCourierTextView.setText(mMemeLabelDialogFragment.getTopText());
                mBottomCourierTextView.setText(mMemeLabelDialogFragment.getBottomText());

                mTopHelveText.setText(mMemeLabelDialogFragment.getTopText());
                mBottomHelveText.setText(mMemeLabelDialogFragment.getBottomText());

            }
        });

    }
    private void initView() {
        //添加贴纸水印的画布
        View overlay = LayoutInflater.from(PhotoProcessActivity.this).inflate(
                R.layout.view_drawable_overlay, null);
        mImageView = (MyImageViewDrawableOverlay) overlay.findViewById(R.id.drawable_overlay);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(App.getApp().getScreenWidth(),
                App.getApp().getScreenWidth());
        mImageView.setLayoutParams(params);
        overlay.setLayoutParams(params);
        drawArea.addView(overlay);
        //添加标签选择器
        RelativeLayout.LayoutParams rparams = new RelativeLayout.LayoutParams(App.getApp().getScreenWidth(), App.getApp().getScreenWidth());
        labelSelector = new LabelSelector(this);
        labelSelector.setLayoutParams(rparams);
        drawArea.addView(labelSelector);
        labelSelector.hide();

        //初始化滤镜图片
        mGPUImageView.setLayoutParams(rparams);


        //初始化空白标签
        emptyLabelView = new LabelView(this);
        emptyLabelView.setEmpty();
        EffectUtil.addLabelEditable(mImageView, drawArea, emptyLabelView,
                mImageView.getWidth() / 2, mImageView.getWidth() / 2);
        emptyLabelView.setVisibility(View.INVISIBLE);

        //初始化推荐标签栏
        commonLabelArea = LayoutInflater.from(PhotoProcessActivity.this).inflate(
                R.layout.view_label_bottom,null);
        commonLabelArea.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        toolArea.addView(commonLabelArea);
        commonLabelArea.setVisibility(View.GONE);
    }

    private void initEvent() {

        // TODO STICKER / FILTERS / LABEL LISTENERS
        /**stickerBtn.setOnClickListener(v ->{
         if (!setCurrentBtn(stickerBtn)) {
         return;
         }
         bottomToolBar.setVisibility(View.VISIBLE);
         labelSelector.hide();
         emptyLabelView.setVisibility(View.GONE);
         commonLabelArea.setVisibility(View.GONE);
         initStickerToolBar();
         });

         filterBtn.setOnClickListener(v -> {
         if (!setCurrentBtn(filterBtn)) {
         return;
         }
         bottomToolBar.setVisibility(View.VISIBLE);
         labelSelector.hide();
         emptyLabelView.setVisibility(View.INVISIBLE);
         commonLabelArea.setVisibility(View.GONE);
         initFilterToolBar();
         });
         labelBtn.setOnClickListener(v -> {
         if (!setCurrentBtn(labelBtn)) {
         return;
         }
         bottomToolBar.setVisibility(View.GONE);
         labelSelector.showToTop();
         commonLabelArea.setVisibility(View.VISIBLE);

         });**/

        labelSelector.setTxtClicked(v -> {
            //EditTextActivity.openTextEdit(PhotoProcessActivity.this, "", 8, AppConstants.ACTION_EDIT_LABEL);
        });

        labelSelector.setAddrClicked(v -> {
            //EditTextActivity.openTextEdit(PhotoProcessActivity.this,"",8, AppConstants.ACTION_EDIT_LABEL_POI);

        });

        mImageView.setOnDrawableEventListener(wpEditListener);

        mImageView.setSingleTapListener(()->{

            /** emptyLabelView.updateLocation((int) mImageView.getmLastMotionScrollX(),
             (int) mImageView.getmLastMotionScrollY());
             emptyLabelView.setVisibility(View.VISIBLE);

             labelSelector.showToTop();
             drawArea.postInvalidate();**/



        });

        /**labelSelector.setOnClickListener(v -> {
         labelSelector.hide();
         emptyLabelView.updateLocation((int) labelSelector.getmLastTouchX(),
         (int) labelSelector.getmLastTouchY());
         emptyLabelView.setVisibility(View.VISIBLE);
         });**/

        /** titleBar.setRightBtnOnclickListener(v -> {
         savePicture();
         });**/

    }

    /**private void savePicture(){
     final Bitmap newBitmap = Bitmap.createBitmap(mImageView.getWidth(), mImageView.getHeight(),
     Bitmap.Config.ARGB_8888);
     Canvas cv = new Canvas(newBitmap);
     RectF dst = new RectF(0, 0, mImageView.getWidth(), mImageView.getHeight());
     try {
     cv.drawBitmap(mGPUImageView.capture(), null, dst, null);
     } catch (InterruptedException e) {
     e.printStackTrace();
     cv.drawBitmap(currentBitmap, null, dst, null);
     }

     EffectUtil.applyOnSave(cv, mImageView);



     new SavePicToFileTask().execute(newBitmap);
     }**/

    private class SavePicToFileTask extends AsyncTask<Bitmap,Void,String> {
        Bitmap bitmap;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(PhotoProcessActivity.this, "Guardando...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            String fileName = null;
            try {
                bitmap = params[0];

                String picName = TimeUtils.dtFormat(new Date(), "yyyyMMddHHmmss");
                fileName = ImageUtils.saveToFile(FileUtils.getInst().getPhotoSavedPath() + "/"+ picName, false, bitmap);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(PhotoProcessActivity.this, "Error, porfavor reinica el teléfono e intenta de nuevo.", Toast.LENGTH_SHORT).show();
            }
            return fileName;
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            if (StringUtils.isEmpty(fileName)) {
                return;
            }

            List<TagItem> tagInfoList = new ArrayList<TagItem>();
            for (LabelView label : labels) {
                tagInfoList.add(label.getTagInfo());
            }

            //将图片信息通过EventBus发送到MainActivity
        }
    }


    public void tagClick(View v){
        TextView textView = (TextView)v;
        TagItem tagItem = new TagItem(AppConstants.POST_TYPE_TAG,textView.getText().toString());
    }

    private MyImageViewDrawableOverlay.OnDrawableEventListener wpEditListener = new MyImageViewDrawableOverlay.OnDrawableEventListener() {
        @Override
        public void onMove(MyHighlightView view) {
        }

        @Override
        public void onFocusChange(MyHighlightView newFocus, MyHighlightView oldFocus) {
        }

        @Override
        public void onDown(MyHighlightView view) {

        }

        @Override
        public void onClick(MyHighlightView view) {
            labelSelector.hide();
        }

        @Override
        public void onClick(final LabelView label) {
            if (label.equals(emptyLabelView)) {
                return;
            }
        }
    };

    private boolean setCurrentBtn(TextView btn) {
        if (currentBtn == null) {
            currentBtn = btn;
        } else if (currentBtn.equals(btn)) {
            return false;
        } else {
            currentBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        Drawable myImage = getResources().getDrawable(R.drawable.select_icon);
        btn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, myImage);
        currentBtn = btn;
        return true;
    }





    /**
     * Dialog
     */


    @Override
    public void onStop(){
        super.onStop();
        if(mMediaPlayer != null && mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
    }


    /**
     *
     * @param viewId
     * @param filename
     */
    public void captureView(int viewId, String filename){
        //Find the view we are after
        View    view = (View) findViewById(viewId);
        //Create a Bitmap with the same dimensions
        Bitmap image = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(),
                Bitmap.Config.RGB_565);
        //Draw the view inside the Bitmap
        view.draw(new Canvas(image));

        //Store to sdcard
        try {
            String path = Environment.getExternalStorageDirectory().toString();
            File myFile = new File(path, filename);
            FileOutputStream out = new FileOutputStream(myFile);

            image.compress(Bitmap.CompressFormat.PNG, 90, out); //Output
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying())
            mMediaPlayer.stop();

        if(currentToolbar == 0){
            finish();
            return;
        }
        initMainToolbar();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        labelSelector.hide();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static class VideoCreationServiceHandler extends Handler
    {

        public void handleMessage(Message message)
        {
            Bundle mData = message.getData();


            if(mActivity != null){

                Log.d("PlaybackServiceHandler", mData.toString());
                if(mActivity.mProgressDialog != null)
                    mActivity.mProgressDialog.dismiss();


                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = Uri.parse(Environment.getExternalStorageDirectory().toString() + "/mix/video.mp4");

                sharingIntent.setType("video/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                mActivity.startActivity(Intent.createChooser(sharingIntent, "Compartir"));

            }
        }

    }

}

package com.emdiem.mix.MainActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.customview.LabelSelector;
import com.customview.LabelView;
import com.customview.MyHighlightView;
import com.customview.MyImageViewDrawableOverlay;
import com.emdiem.mix.App;
import com.emdiem.mix.Utils.AppConstants;
import com.emdiem.mix.Utils.CameraManager;
import com.emdiem.mix.Utils.EffectUtil;
import com.emdiem.mix.Utils.FileUtils;
import com.emdiem.mix.Utils.ImageUtils;
import com.emdiem.mix.Utils.MemeToolAdapter;
import com.emdiem.mix.Utils.PhotoItem;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.widget.HListView;

public class MemeSelectActivity extends AppCompatActivity {

//    @BindView(R.id.gpuimage)
    ViewGroup mGPUImage;

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

    //当前选择底部按钮
    private TextView currentBtn;
    //当前图片
    private Bitmap currentBitmap;
    //用于预览的小图片
    private Bitmap smallImageBackgroud;
    //小白点标签
    private LabelView emptyLabelView;

    private List<LabelView> labels = new ArrayList<>();

    //标签区域
    private View commonLabelArea;

    private Target mMemeLoadTarget;

    public ImageButton mShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_process);
        ButterKnife.bind(this);
        EffectUtil.clear();
        drawArea = findViewById(R.id.drawing_view_container);
        mGPUImageView = findViewById(R.id.gpuimage);
        bottomToolBar = findViewById(R.id.list_tools);
        toolArea = findViewById(R.id.toolbar_area);
        initView();
        initEvent();

        // TODO this to be run at the start of the process
        // initStickerToolBar();
        initMainToolbar();


        mShareButton = (ImageButton)findViewById(R.id.shareButton);
        mShareButton.setVisibility(View.INVISIBLE);

        /**ImageUtils.asyncLoadImage(this, getIntent().getData(), new ImageUtils.LoadImageCallback() {
        @Override
        public void callback(Bitmap result) {
        currentBitmap = result;
        mGPUImageView.setImage(currentBitmap);
        }
        });

         ImageUtils.asyncLoadSmallImage(this, getIntent().getData(), new ImageUtils.LoadImageCallback() {
        @Override
        public void callback(Bitmap result) {
        smallImageBackgroud = result;
        }
        });

         setOnBackButtonClickListener(new OnBackButtonClickInterceptListener() {
        @Override
        public boolean onIntercept() {
        initMainToolbar();
        return false;
        }
        });**/

        mMemeLoadTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                currentBitmap = bitmap;
                mGPUImageView.setImageBitmap(currentBitmap);

                try {
                    FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/meme.png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                    out.flush();
                    out.close();


                    File mFile = new File(Environment.getExternalStorageDirectory().toString() + "/meme.png");

                    CameraManager.getInst().processPhotoItem(MemeSelectActivity.this,
                            new PhotoItem(mFile.getAbsolutePath(), System.currentTimeMillis()));

                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

    }
    private void initView() {
        //添加贴纸水印的画布
        View overlay = LayoutInflater.from(MemeSelectActivity.this).inflate(
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
        mGPUImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


        //初始化空白标签
        emptyLabelView = new LabelView(this);
        emptyLabelView.setEmpty();
        EffectUtil.addLabelEditable(mImageView, drawArea, emptyLabelView,
                mImageView.getWidth() / 2, mImageView.getWidth() / 2);
        emptyLabelView.setVisibility(View.INVISIBLE);

        //初始化推荐标签栏
        commonLabelArea = LayoutInflater.from(MemeSelectActivity.this).inflate(
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


        /**labelSelector.setTxtClicked(v -> {
         EditTextActivity.openTextEdit(MemeSelectActivity.this, "", 8, AppConstants.ACTION_EDIT_LABEL);
         });

         labelSelector.setAddrClicked(v -> {
         EditTextActivity.openTextEdit(MemeSelectActivity.this,"",8, AppConstants.ACTION_EDIT_LABEL_POI);

         });

         mImageView.setOnDrawableEventListener(wpEditListener);

         mImageView.setSingleTapListener(()->{

         emptyLabelView.updateLocation((int) mImageView.getmLastMotionScrollX(),
         (int) mImageView.getmLastMotionScrollY());
         emptyLabelView.setVisibility(View.VISIBLE);

         labelSelector.showToTop();
         drawArea.postInvalidate();

         });

         labelSelector.setOnClickListener(v -> {
         labelSelector.hide();
         emptyLabelView.updateLocation((int) labelSelector.getmLastTouchX(),
         (int) labelSelector.getmLastTouchY());
         emptyLabelView.setVisibility(View.VISIBLE);
         });**/

        /**titleBar.setRightBtnOnclickListener(v -> {
         //savePicture();
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
            Toast.makeText(MemeSelectActivity.this, "Guardando...", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(MemeSelectActivity.this, "porfavor reinica el teléfono e intenta de nuevo.", Toast.LENGTH_SHORT).show();
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
            /*FeedItem feedItem = new FeedItem(tagInfoList,fileName);
            EventBus.getDefault().post(feedItem);
            CameraManager.getInst().close();
        */
        }
    }


    public void tagClick(View v){
        TextView textView = (TextView)v;
        TagItem tagItem = new TagItem(AppConstants.POST_TYPE_TAG,textView.getText().toString());
        addLabel(tagItem);
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


    private void initMainToolbar(){

        ParseQuery<ParseObject> mQuery = new ParseQuery<>("Meme");
        mQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {


                if (e == null && objects != null) {

                    MemeToolAdapter mMemeToolAdapter = new MemeToolAdapter(MemeSelectActivity.this, objects);

                    bottomToolBar.setAdapter(mMemeToolAdapter);
                    bottomToolBar.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> arg0,
                                                View arg1, int arg2, long arg3) {

                            ParseObject mMeme = (ParseObject)mMemeToolAdapter.getItem(arg2);

                            ParseFile mImage = mMeme.getParseFile("image");

                            if(mImage != null)
                                Picasso.with(MemeSelectActivity.this)
                                        .load(mImage.getUrl())
                                        .into(mMemeLoadTarget);




                        }
                    });


                }

            }
        });


    }

    private void addLabel(TagItem tagItem) {
        labelSelector.hide();
        emptyLabelView.setVisibility(View.INVISIBLE);
        if (labels.size() >= 5) {
        } else {
            int left = emptyLabelView.getLeft();
            int top = emptyLabelView.getTop();
            if (labels.size() == 0 && left == 0 && top == 0) {
                left = mImageView.getWidth() / 2 - 10;
                top = mImageView.getWidth() / 2;
            }
            LabelView label = new LabelView(MemeSelectActivity.this);
            label.init(tagItem);
            EffectUtil.addLabelEditable(mImageView, drawArea, label, left, top);
            labels.add(label);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        labelSelector.hide();
        super.onActivityResult(requestCode, resultCode, data);
        if (AppConstants.ACTION_EDIT_LABEL== requestCode && data != null) {
            String text = data.getStringExtra(AppConstants.PARAM_EDIT_TEXT);
            if(((text != null) && (text.trim().length() > 0))){
                TagItem tagItem = new TagItem(AppConstants.POST_TYPE_TAG,text);
                addLabel(tagItem);
            }
        }else if(AppConstants.ACTION_EDIT_LABEL_POI == requestCode && data != null){
            String text = data.getStringExtra(AppConstants.PARAM_EDIT_TEXT);
            if(((text != null) && (text.trim().length() > 0))){
                TagItem tagItem = new TagItem(AppConstants.POST_TYPE_POI,text);
                addLabel(tagItem);
            }
        }
    }

}

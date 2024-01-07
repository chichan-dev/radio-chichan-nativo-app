package com.emdiem.mix.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.customview.LabelView;
import com.customview.MyHighlightView;
import com.customview.MyImageViewDrawableOverlay;
import com.customview.drawable.StickerDrawable;
import com.emdiem.mix.App;
import com.github.skykai.stickercamera.R;
import com.imagezoom.ImageViewTouch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EffectUtil {
    public static List<Addon> addonList = new ArrayList<Addon>();
    private static List<MyHighlightView> hightlistViews = new CopyOnWriteArrayList<>();
    public static List<EditorItem> itemList = new ArrayList<>();
    public static List<FontStyle> fontStyleList = new ArrayList<>();

    static {
        itemList.add(new EditorItem("Texto", R.drawable.ic_text, EditorItem.TEXT));
        itemList.add(new EditorItem("Audios", R.drawable.ic_audio, EditorItem.AUDIOS));
    }


    static {
        addonList.add(new Addon(R.drawable.sticker1));
        addonList.add(new Addon(R.drawable.sticker2));
        addonList.add(new Addon(R.drawable.sticker3));
        addonList.add(new Addon(R.drawable.sticker4));
        addonList.add(new Addon(R.drawable.sticker5));
        addonList.add(new Addon(R.drawable.sticker6));
        addonList.add(new Addon(R.drawable.sticker7));
        addonList.add(new Addon(R.drawable.sticker8));
    }

    static {
        fontStyleList.add(new FontStyle("HELVETICA", R.drawable.ic_text_helveticx2, FontStyle.Type.HELVETICA));
        fontStyleList.add(new FontStyle("COURIER", R.drawable.ic_courier, FontStyle.Type.COURIER));
        fontStyleList.add(new FontStyle("GEORGIA", R.drawable.ic_georgia, FontStyle.Type.GEORGIA));
    }


    public static void clear() {
        hightlistViews.clear();
    }

    public static interface StickerCallback {
        public void onRemoveSticker(Addon sticker);
    }

    public static void addLabelEditable(MyImageViewDrawableOverlay overlay, ViewGroup container,
                                        LabelView label, int left, int top) {
        addLabel(container, label, left, top);
        addLabel2Overlay(overlay, label);
    }

    private static void addLabel(ViewGroup container, LabelView label, int left, int top) {
        label.addTo(container, left, top);
    }

    public static void removeLabelEditable(MyImageViewDrawableOverlay overlay, ViewGroup container,
                                           LabelView label) {
        container.removeView(label);
        overlay.removeLabel(label);
    }

    public static int getStandDis(float realDis, float baseWidth) {
        float imageWidth = baseWidth <= 0 ? App.getApp().getScreenWidth() : baseWidth;
        float radio = AppConstants.DEFAULT_PIXEL / imageWidth;
        return (int) (radio * realDis);
    }

    public static int getRealDis(float standardDis, float baseWidth) {
        float imageWidth = baseWidth <= 0 ? App.getApp().getScreenWidth() : baseWidth;
        float radio = imageWidth / AppConstants.DEFAULT_PIXEL;
        return (int) (radio * standardDis);
    }

    private static void addLabel2Overlay(final MyImageViewDrawableOverlay overlay,
                                         final LabelView label) {
        overlay.addLabel(label);
        label.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 手指按下时
                        overlay.setCurrentLabel(label, event.getRawX(), event.getRawY());
                        return false;
                    default:
                        return false;
                }
            }
        });
    }


    /**
     * Returns a bitmap from the view
     * @param view
     * @return
     */
    public static Bitmap getBitmapFromView(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return bitmap;
    }



    public static void applyOnSave(Canvas mCanvas, ImageViewTouch processImage) {
        for (MyHighlightView view : hightlistViews) {
            applyOnSave(mCanvas, processImage, view);
        }
    }

    private static void applyOnSave(Canvas mCanvas, ImageViewTouch processImage, MyHighlightView view) {

        if (view != null && view.getContent() instanceof StickerDrawable) {

            final StickerDrawable stickerDrawable = ((StickerDrawable) view.getContent());
            RectF cropRect = view.getCropRectF();
            Rect rect = new Rect((int) cropRect.left, (int) cropRect.top, (int) cropRect.right,
                    (int) cropRect.bottom);

            Matrix rotateMatrix = view.getCropRotationMatrix();
            Matrix matrix = new Matrix(processImage.getImageMatrix());
            if (!matrix.invert(matrix)) {
            }
            int saveCount = mCanvas.save();
            mCanvas.concat(rotateMatrix);

            stickerDrawable.setDropShadow(false);
            view.getContent().setBounds(rect);
            view.getContent().draw(mCanvas);
            mCanvas.restoreToCount(saveCount);
        }
    }
}

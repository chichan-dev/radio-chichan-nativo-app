package com.emdiem.mix.Utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.emdiem.mix.MainActivity.PhotoProcessActivity;

import java.util.Stack;

public class CameraManager {
    private static CameraManager mInstance;
    private Stack<Activity> cameras = new Stack<Activity>();

    public static CameraManager getInst() {
        if (mInstance == null) {
            synchronized (CameraManager.class) {
                if (mInstance == null)
                    mInstance = new CameraManager();
            }
        }
        return mInstance;
    }


    public void processPhotoItem(Activity activity, PhotoItem photo) {


        Uri uri = photo.getImageUri().startsWith("file:") ? Uri.parse(photo
                .getImageUri()) : Uri.parse("file://" + photo.getImageUri());
        //if (ImageUtils.isSquare(photo.getImageUri())) {
        Intent newIntent = new Intent(activity, PhotoProcessActivity.class);
        newIntent.setData(uri);
        activity.startActivity(newIntent);
        /**} else {
         Intent i = new Intent(activity, CropPhotoActivity.class);
         i.setData(uri);
         //TODO稍后添加
         activity.startActivityForResult(i, AppConstants.REQUEST_CROP);
         }**/




    }

    public void close() {
        for (Activity act : cameras) {
            try {
                act.finish();
            } catch (Exception e) {

            }
        }
        cameras.clear();
    }

    public void addActivity(Activity act) {
        cameras.add(act);
    }

    public void removeActivity(Activity act) {
        cameras.remove(act);
    }

}

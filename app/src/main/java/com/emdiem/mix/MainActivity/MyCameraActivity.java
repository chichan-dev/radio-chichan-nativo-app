package com.emdiem.mix.MainActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.emdiem.mix.Utils.CircleTransform;
import com.github.skykai.stickercamera.R;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Facing;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyCameraActivity extends AppCompatActivity {

    static final int CAPTURE_STATE = 0;
    static final int SHARE_STATE = 1;
    static final int RESULT_LOAD_IMAGE = 3;
    static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    CameraView cameraView;
    ImageView cancel, gallery, spin, preview, meme;
    File dir;
    Button shutter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);
        dir = getDiskCacheDir(this);
        cameraView =  findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery_button);
        spin = findViewById(R.id.spin_button);
        preview = findViewById(R.id.preview);
        shutter = findViewById(R.id.shutter_button);
        cancel = findViewById(R.id.cancel_button);
        meme = findViewById(R.id.next);
        putLastTakenPhoto();
        cancel.bringToFront();

        meme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyCameraActivity.this, MemeSelectActivity.class));
            }
        });

        shutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.takePicture();
                //cameraView.captureSnapshot();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeState(CAPTURE_STATE);
            }
        });

        spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraView.getFacing() == Facing.BACK) cameraView.setFacing(Facing.FRONT);
                else cameraView.setFacing(Facing.BACK);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchGalleryIntent();
            }
        });


        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                CameraUtils.decodeBitmap(result.getData(), new BitmapCallback() {
                    @Override
                    public void onBitmapReady(@Nullable Bitmap bitmap) {
                        Intent newIntent = new Intent(MyCameraActivity.this, PhotoProcessActivity.class);
                        File destination1 = createDirectoryAndSaveFile(bitmap, System.currentTimeMillis() + ".jpg");
                        newIntent.setData(Uri.fromFile(destination1));
                        startActivity(newIntent);
                    }
                });
            }
        });
    }

    private File createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/My Images");
        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/MYfolder Images/");
            wallpaperDirectory.mkdirs();
        }
        File file = new File(new File("/sdcard/Myfolder Images/"), fileName);
  /*  if (file.exists()) {
        file.delete();
    }*/
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private void dispatchGalleryIntent(){

        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1) {

            File f = new File(dir.getAbsolutePath() + "/myphoto.jpg");
            if (requestCode == RESULT_LOAD_IMAGE && null != data) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Intent newIntent = new Intent(MyCameraActivity.this, PhotoProcessActivity.class);
                newIntent.setData(Uri.fromFile(new File(picturePath)));
                startActivity(newIntent);
                //

                /*Uri selectedUri = data.getData();
                String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.MIME_TYPE };

                Cursor cursor = getActivity().getContentResolver().query(selectedUri, columns, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int pathColumnIndex     = cursor.getColumnIndex( columns[0] );
                    int mimeTypeColumnIndex = cursor.getColumnIndex( columns[1] );

                    String contentPath = cursor.getString(pathColumnIndex);
                    String mimeType    = cursor.getString(mimeTypeColumnIndex);
                    cursor.close();
                    File f1 = new File(contentPath);
                    if(mimeType.startsWith("image")) {
                        dispatchEditIntent(contentPath);
                    }
                }*/
            }
            else if (requestCode == 20){

                if(data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {

                        String fileToSend = dir.getAbsolutePath() + "/finalphoto1.jpg";
                        File mediaFile = new File(fileToSend);
                        Bitmap imageBitmap = BitmapFactory.decodeFile(mediaFile.getPath());
                        imageHandler(imageBitmap,fileToSend);
                        changeState(SHARE_STATE);
                    }
                    else
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("Ha ocurrido un error con la imagen. Por favor, intente más tarde.");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
                else
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Ha ocurrido un error con la imagen. Por favor, intente más tarde.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                }
            }
        }
    }

    public void imageHandler(Bitmap imageBitmap, String fileToSend){

        Bitmap finalBitmap;
        int degrees = 0;
        ExifInterface ei;
        try {
            ei = new ExifInterface(fileToSend);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:

                    degrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degrees = 270;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (degrees == 0) {

            finalBitmap = imageBitmap;

        } else {

            finalBitmap = rotateImage(imageBitmap, degrees);
        }
        preview.setImageBitmap(finalBitmap);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }
        catch (OutOfMemoryError out){
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }
    }

    private void putLastTakenPhoto() {
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        final Cursor cursor = getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

// Put it in the image view
        if (cursor.moveToFirst()) {
            String imageLocation = cursor.getString(1);
            File imageFile = new File(imageLocation);
            if (imageFile.exists()) {
                Picasso.with(this).load(imageFile).transform(new CircleTransform()).into(gallery);
            }
        }
    }

    public static File getDiskCacheDir(Context c) {
        File dir = c.getExternalCacheDir();

        if (dir == null)
            dir = c.getCacheDir();

        return dir;
    }

    public void changeState(int state) {
        if (state == SHARE_STATE) {
            cancel.setVisibility(View.VISIBLE);
            preview.setVisibility(View.VISIBLE);
            //shareButton.setVisibility(View.VISIBLE);
            cameraView.setVisibility(View.INVISIBLE);
            gallery.setVisibility(View.INVISIBLE);
            shutter.setVisibility(View.INVISIBLE);
            spin.setVisibility(View.INVISIBLE);

        } else {
            cancel.setVisibility(View.INVISIBLE);
            cameraView.setVisibility(View.VISIBLE);
            preview.setVisibility(View.INVISIBLE);
            shutter.setVisibility(View.VISIBLE);
            gallery.setVisibility(View.VISIBLE);
            //shareButton.setVisibility(View.INVISIBLE);
            spin.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraView.close();
        //cameraView.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }
}

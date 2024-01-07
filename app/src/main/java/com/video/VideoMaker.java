package com.video;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;

import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class VideoMaker extends AsyncTask<JSONObject, Boolean, Boolean> {

    private Context _context;
    private Bitmap _bitmap;
    public OnVideoCreationFinishedListener mFinishedListener;

    public OnVideoCreationFinishedListener getFinishedListener() {
        return mFinishedListener;
    }

    public void setFinishedListener(OnVideoCreationFinishedListener mFinishedListener) {
        this.mFinishedListener = mFinishedListener;
    }

    public VideoMaker(Context context, Bitmap bitmap){

        this._context = context;
        this._bitmap = bitmap;
    }


    @Override
    protected void onPreExecute(){

    }

    @Override
    protected Boolean doInBackground(JSONObject... voice){

        // Make dirs
        (new File(Environment.getExternalStorageDirectory().getPath()+"/mix/")).mkdirs();

         try {
         SequenceEncoder encoder = new SequenceEncoder(new File("/sdcard/mix/out.mp4"));

         for (int i = 0; i <= 2; i++) {
            try {
                encoder.encodeNativeFrame(this.fromBitmap(_bitmap));
            }catch (IOException e){
                Log.d("IOException", e.getLocalizedMessage());
            }
         }
         try {
            encoder.finish();
         }catch (IOException e){
            Log.d("IOException", e.getLocalizedMessage());
         }
         }catch (IOException e){
             Log.d("IOException", e.getLocalizedMessage());
         }

        // SAVE AUDIO TO SDCARD

        try {
            InputStream in = new URL(voice[0].getString("url")).openStream();

            FileOutputStream out = new FileOutputStream("/sdcard/mix/tmp.aac");

            byte[] buff = new byte[1024];
            int read = 0;

            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {

                in.close();
                out.close();

           }

            FFmpegFrameGrabber audio_grabber = new FFmpegFrameGrabber("/sdcard/mix/tmp.aac");
            FFmpegFrameGrabber video_grabber = new FFmpegFrameGrabber("/sdcard/mix/out.mp4");

            video_grabber.setFormat("mp4");
            audio_grabber.setFormat("mp3");

            try {

                audio_grabber.start();
                video_grabber.start();

                Log.d("AudioFormat", audio_grabber.getFormat());
                Log.d("AudioFormat", video_grabber.getFormat());

                FFmpegFrameRecorder video_recorder = new FFmpegFrameRecorder("/sdcard/mix/video.mp4",
                        video_grabber.getImageWidth(), video_grabber.getImageHeight(), audio_grabber.getAudioChannels());

                video_recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
                video_recorder.setFrameRate(24);
                video_recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
                video_recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);

                video_recorder.start();

                Log.d("getFrameNumber", audio_grabber.getFrameNumber() + "");
                Log.d("getFrameRate", audio_grabber.getFrameRate() + "");
                Log.d("getLengthInFrames", audio_grabber.getLengthInFrames() + "");
                Log.d("getLengthInTime", audio_grabber.getLengthInTime() + "");
                Log.d("getTimeout", audio_grabber.getTimeout() + "");
                Log.d("getDelayedTime", audio_grabber.getDelayedTime() + "");

                try {

                    for(int i = 0; i <=  (  ( (audio_grabber.getLengthInTime() / 1000000) + 5) *  24) ; i++) {
                        video_recorder.record(audio_grabber.grabFrame());
                        video_recorder.record(video_grabber.grabFrame());

                    }

                }catch (FFmpegFrameRecorder.Exception e){
                    Log.d("FFmpegFrameRecorder", e.getLocalizedMessage());
                }

                audio_grabber.stop();
                video_recorder.stop();

            }catch (Exception e){
                Log.d("THREAD", e.getLocalizedMessage());
            }

        }catch (IOException e){
            Log.d("IOException", e.getLocalizedMessage());
        }catch(JSONException e){
           Log.d("JSONException", e.getLocalizedMessage());
        }

        return true;

    }

    // To convert from Bitmap to Picture (jcodec native structure)

    public Picture fromBitmap(Bitmap src) {
        Picture dst = Picture.create((int)src.getWidth(), (int)src.getHeight(), ColorSpace.RGB);
        fromBitmap(src, dst);
        return dst;
    }

    public void fromBitmap(Bitmap src, Picture dst) {
        int[] dstData = dst.getPlaneData(0);
        int[] packed = new int[src.getWidth() * src.getHeight()];

        src.getPixels(packed, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());

        for (int i = 0, srcOff = 0, dstOff = 0; i < src.getHeight(); i++) {
            for (int j = 0; j < src.getWidth(); j++, srcOff++, dstOff += 3) {
                int rgb = packed[srcOff];
                dstData[dstOff]     = (rgb >> 16) & 0xff;
                dstData[dstOff + 1] = (rgb >> 8) & 0xff;
                dstData[dstOff + 2] = rgb & 0xff;
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean result){
        // Delete temporal files
        (new File("/sdcard/mix/tmp.aac")).delete();
        (new File("/sdcard/mix/out.mp4")).delete();

        if(getFinishedListener() != null){
            getFinishedListener().onFinished();
        }
    }


    public interface OnVideoCreationFinishedListener{
        void onFinished();
    }

}

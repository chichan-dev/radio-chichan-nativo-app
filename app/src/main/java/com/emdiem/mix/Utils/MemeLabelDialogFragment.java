package com.emdiem.mix.Utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.github.skykai.stickercamera.R;

public class MemeLabelDialogFragment  extends DialogFragment {
    public EditText mTopText;
    public EditText mBottomText;

    public Button mOkButton;

    public OnDismissListener mOnDismissListener;

    public MemeLabelDialogFragment(){

    }
    public OnDismissListener getOnDismissListener() {
        return mOnDismissListener;
    }

    public void setOnDismissListener(OnDismissListener mOnDismissListener) {
        this.mOnDismissListener = mOnDismissListener;
    }


    public static MemeLabelDialogFragment newInstance(String t1, String t2){
        MemeLabelDialogFragment mSurveyFormDialogFragment = new MemeLabelDialogFragment();

        Bundle mArgs = new Bundle();
        mArgs.putString("t1", t1);
        mArgs.putString("t2", t2);
        mSurveyFormDialogFragment.setArguments(mArgs);


        return mSurveyFormDialogFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("onViewCreated", "true");

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_labels, null);
        alertDialogBuilder.setView(view);

        mTopText = (EditText)view.findViewById(R.id.topEdit);
        mBottomText = (EditText)view.findViewById(R.id.bottomEdit);
        mOkButton = (Button)view.findViewById(R.id.readyButton);

        setup();
        listen();

        Dialog mDialog =  alertDialogBuilder.create();

        Resources r = getResources();
        int width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, r.getDisplayMetrics()));
        int height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics()));

        mDialog.getWindow().setLayout(width, getResources().getDimensionPixelSize(R.dimen.height_300px));


        return mDialog;

    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static int convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(dp);
    }



    public void setup(){
        mTopText.setText(getArguments().getString("t1"));
        mBottomText.setText(getArguments().getString("t2"));
    }

    public void listen(){
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(getOnDismissListener() != null){
                    getOnDismissListener().onDismiss();
                }
            }
        });
    }

    public String getTopText(){
        return mTopText.getText().toString().toUpperCase();
    }

    public String getBottomText(){
        return mBottomText.getText().toString().toUpperCase();
    }


    public interface OnDismissListener{
        void onDismiss();
    }
}

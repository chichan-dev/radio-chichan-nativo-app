package com.emdiem.mix.MainActivity;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.skykai.stickercamera.R;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SurveyAnswerAdapter extends ArrayAdapter<ParseObject> {

    Context mContext;

    public SurveyAnswerAdapter(Context context, List<ParseObject> users) {
        super(context, 0, users);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        ParseObject mParseObject = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_answer, parent, false);
        }

        RelativeLayout mAnswerContainer = (RelativeLayout)convertView.findViewById(R.id.answerContainer);
        ImageView mAnswerBackground = (ImageView)convertView.findViewById(R.id.answerBackground);
        Button mButton = (Button)convertView.findViewById(R.id.answerButton);


        Picasso.with(mContext)
                .load(R.drawable.survey_box)
                .into(mAnswerBackground);

        mButton.setText(mParseObject.getString("answer"));


        return convertView;
    }
}

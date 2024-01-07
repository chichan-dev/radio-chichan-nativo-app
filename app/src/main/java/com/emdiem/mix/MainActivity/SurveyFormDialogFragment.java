package com.emdiem.mix.MainActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.emdiem.mix.Models.SurveyEntry;
import com.github.skykai.stickercamera.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class SurveyFormDialogFragment extends DialogFragment {

    public String mPostId;
    public ListView mListView;
    public TextView mTitle;
    public ParseObject mSurveyItem;

    public SurveyFormDialogFragment(){

    }

    public static SurveyFormDialogFragment newInstance(String postId){
        SurveyFormDialogFragment mSurveyFormDialogFragment = new SurveyFormDialogFragment();

        Bundle mArgs = new Bundle();
        mSurveyFormDialogFragment.setArguments(mArgs);

        mArgs.putString("postId", postId);


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

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_survey, null);
        alertDialogBuilder.setView(view);



        mPostId = getArguments().getString("postId");
        mListView = (ListView)view.findViewById(R.id.answerList);
        mTitle = (TextView)view.findViewById(R.id.title);


        setup();
        listen();

        return alertDialogBuilder.create();
    }


    public void setup(){

        // Send
        ParseQuery<ParseObject> mQuery = new ParseQuery<>("Post");
        mQuery.whereEqualTo("objectId", mPostId);
        mQuery.include("survey");

        // Get
        mQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject post, ParseException e) {
                if (e == null && post != null) {

                    mSurveyItem = post.getParseObject("survey");

                    // Set title
                    mTitle.setText(mSurveyItem.getString("title"));

                        ParseQuery<ParseObject> mAnswerQuery = new ParseQuery<>("SurveyAnswer");
                        mAnswerQuery.whereEqualTo("survey", mSurveyItem);

                        mAnswerQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {

                                if(mListView != null && e == null && getActivity() != null) {
                                    SurveyAnswerAdapter mSurveyAnswerAdapter = new SurveyAnswerAdapter(getActivity(), objects);
                                    mListView.setAdapter(mSurveyAnswerAdapter);
                                    mSurveyAnswerAdapter.notifyDataSetChanged();
                                }

                            }
                        });
                }
            }
        });

    }

    public void listen(){

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ParseObject mSelectedAnswer = (ParseObject) mListView.getAdapter().getItem(i);

                ParseObject mSurveyEntry = new ParseObject("SurveyEntry");

                mSurveyEntry.put("survey", mSurveyItem);
                mSurveyEntry.put("answer", mSelectedAnswer);


                if(!SurveyEntry.hasAnswered(mPostId)) {
                    mSurveyEntry.saveInBackground();
                    SurveyEntry.addAnswered(mPostId);
                    Log.d("Survey", "Saving...");
                }else{
                    Log.d("Survey", "Not saving survey...");
                }


                // Todo only once
                dismiss();

            }
        });
    }


}

package com.emdiem.mix.MainActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.skykai.stickercamera.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class ContestFormDialogFragment extends DialogFragment{

    public String mPostId;
    public ImageButton mSendButton;

    public Spinner mDocTypeSpinner;
    public EditText mAgeEditText;

    public TextView mDocumentNumber;
    public TextView mPhone;
    public TextView mNeighborhood;
    public TextView mWhatsapp;
    public TextView mEmail;

    public TextView mName;

    public ContestFormDialogFragment(){

    }

    public static ContestFormDialogFragment newInstance(String postId){
        ContestFormDialogFragment mContestFormDialogFragment = new ContestFormDialogFragment();

        Bundle mArgs = new Bundle();
        mContestFormDialogFragment.setArguments(mArgs);

        mArgs.putString("postId", postId);

        return mContestFormDialogFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("onViewCreated", "true");

    }

    public void setup(){
        /**ArrayList<String> mAges = new ArrayList<>();

        for(int i = 12; i <= 70; i++)
            mAges.add(Integer.valueOf(i).toString());

        ArrayAdapter<String> mAgesAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mAges);
        mAgesAdapter.notifyDataSetChanged();

        mAgeSpinner.setAdapter(mAgesAdapter);**/
    }

    public void listen(){
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String mDocumentText = mDocumentNumber.getText().toString().trim();
                final String mPhoneText = mPhone.getText().toString().trim();
                final String mNeighborhoodText = mNeighborhood.getText().toString().trim();
                final String mWhatsAppText = mWhatsapp.getText().toString().trim();
                final String mEmailText = mEmail.getText().toString().trim();
                final String mNameText = mName.getText().toString().trim();

                if(mDocumentText.equals("") || mPhoneText.equals("") || mNeighborhoodText.equals("") || mWhatsAppText.equals("") || mEmailText.equals("") || mNameText.equals("")) {

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Error")
                            .setMessage("La información ingresada está incompleta o no es válida")
                            .show();

                    return;
                }

                // Dismiss this dialog
                getDialog().dismiss();

                // Send
                ParseQuery<ParseObject> mQuery = new ParseQuery<>("Post");
                mQuery.whereEqualTo("objectId", mPostId);
                mQuery.include("contest");


                int mParsedAge = Integer.parseInt(mAgeEditText.getText().toString());
                if(mParsedAge < 12 || mParsedAge > 90 ){

                    new AlertDialog.Builder(getActivity())
                            .setTitle("¡Gracias!")
                            .setMessage("Verifíca la edad ingresada")
                            .show();


                    return;
                }



                mQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject post, ParseException e) {
                        if (e == null && post != null) {

                            ParseObject mParseObject = new ParseObject("ContestEntry");

                            mParseObject.put("contest", post.get("contest"));
                            mParseObject.put("name", mNameText);
                            mParseObject.put("phone", mPhoneText );
                            // mParseObject.put("documentType", ) TODO set document type values
                            mParseObject.put("document", mDocumentText);
                            mParseObject.put("neighborhood", mNeighborhoodText);
                            mParseObject.put("age", Integer.parseInt(mAgeEditText.getText().toString()));
                            mParseObject.put("email", mEmailText);
                            mParseObject.put("whatsapp", mWhatsAppText);

                            mParseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e != null){
                                        Log.d("ParseException", e.getMessage());
                                    }
                                }
                            });

                            // mParseObject.put("");
                        }
                    }
                });


                new AlertDialog.Builder(getActivity())
                        .setTitle("¡Gracias!")
                        .setMessage("Su formulario ha sido enviado, lo llamaremos para confirmar la información.")
                        .show();
            }
        });
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_contest, null);
        alertDialogBuilder.setView(view);

        mPostId = getArguments().getString("postId");

        mSendButton = (ImageButton)view.findViewById(R.id.sendButton);
        mAgeEditText = (EditText)view.findViewById(R.id.age);
        mDocTypeSpinner = (Spinner)view.findViewById(R.id.docType);

        mDocumentNumber = (TextView)view.findViewById(R.id.document);
        mPhone = (TextView)view.findViewById(R.id.phone);
        mNeighborhood = (TextView)view.findViewById(R.id.neighborhood);
        mWhatsapp = (TextView)view.findViewById(R.id.whatsapp);
        mEmail = (TextView)view.findViewById(R.id.email);
        mName = (TextView)view.findViewById(R.id.name);

        setup();
        listen();

        return alertDialogBuilder.create();
    }


}

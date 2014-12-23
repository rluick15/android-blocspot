package com.bloc.blocspot.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.ui.activities.BlocSpotActivity;
import com.bloc.blocspot.utils.Constants;

/**
 * This fragment lets the user enter in a new note and updates the database accordingly
 */
public class EditNoteFragment extends DialogFragment {

    private String mOldNote;
    private String mId;
    private Context mContext;
    private OnNoteUpdateListener mListener;
    private EditText mNewNote;

    public EditNoteFragment() {} // Required empty public constructor

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Constants.EDIT_NOTE_TEXT, mNewNote.getText().toString());
    }

    public EditNoteFragment(String id, Context context, String note) {
        this.mId = id;
        this.mContext = context;
        this.mOldNote = note;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_note, container, false);
        getDialog().setTitle(getString(R.string.title_edit_note));
        getDialog().setCanceledOnTouchOutside(true);
        if (savedInstanceState != null) {
            mOldNote = savedInstanceState.getString(Constants.EDIT_NOTE_TEXT);
        }

        mNewNote = (EditText) rootView.findViewById(R.id.editNoteText);
        mNewNote.setText(mOldNote);

        //set cancel Button
        Button cancelButton = (Button) rootView.findViewById(R.id.editNoteCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        //set update button
        Button updateNoteButton = (Button) rootView.findViewById(R.id.editNoteButton);
        updateNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedNote = mNewNote.getText().toString();
                ((BlocSpotActivity) mContext).updateNoteDb(mId, updatedNote);
                dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnNoteUpdateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnNoteUpdateListener {
        public void updateNoteDb(String name, String note);
    }

}

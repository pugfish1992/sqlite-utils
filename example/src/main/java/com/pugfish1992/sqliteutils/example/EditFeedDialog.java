package com.pugfish1992.sqliteutils.example;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.View;

/**
 * Created by daichi on 10/28/17.
 */

public class EditFeedDialog extends DialogFragment {

    public interface OnFinishEditingListener {
        void onFinishEditing(@Nullable Feed oldFeed, @NonNull Feed newFeed);
    }

    private static final String ARG_FEED_ID = "EditFeedDialog:feedId";
    private static final String ARG_FEED_TITLE = "EditFeedDialog:feedTitle";
    private static final String ARG_FEED_SUB_TITLE = "EditFeedDialog:feedSubTitle";

    private OnFinishEditingListener mListener;
    @Nullable private Feed mOldFeed;

    public static EditFeedDialog newInstance(Feed feed) {
        EditFeedDialog fragment = new EditFeedDialog();
        if (feed != null) {
            Bundle args = new Bundle();
            args.putLong(ARG_FEED_ID, feed.id);
            args.putString(ARG_FEED_TITLE, feed.title);
            args.putString(ARG_FEED_SUB_TITLE, feed.subTitle);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOldFeed = new Feed();
            mOldFeed.id = getArguments().getLong(ARG_FEED_ID);
            mOldFeed.title = getArguments().getString(ARG_FEED_TITLE);
            mOldFeed.subTitle = getArguments().getString(ARG_FEED_SUB_TITLE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.component_edit_feed_dialog, null);

        final TextInputEditText titleEditor = view.findViewById(R.id.edit_title);
        final TextInputEditText subTitleEditor = view.findViewById(R.id.edit_sub_title);
        if (mOldFeed != null) {
            titleEditor.setText(mOldFeed.title);
            subTitleEditor.setText(mOldFeed.subTitle);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Edit Feed")
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Feed feed = new Feed();
                        if (mOldFeed != null) {
                            feed.id = mOldFeed.id;
                            feed.title = mOldFeed.title;
                            feed.subTitle = mOldFeed.subTitle;
                        }

                        String text = feed.title = titleEditor.getText().toString();
                        feed.title = (text.length() != 0) ? text : feed.title;
                        text = subTitleEditor.getText().toString();
                        feed.subTitle = (text.length() != 0) ? text : feed.subTitle;

                        mListener.onFinishEditing(mOldFeed, feed);
                    }
                })
                .setNegativeButton("CANCEL", null)
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFinishEditingListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DialogFragment.OnFinishEditingListener");
        }
    }
}

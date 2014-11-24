/*
* Copyright 2014 Vasya Drobushkov
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.krossovochkin.kwitter.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.krossovochkin.kwitter.R;
import com.krossovochkin.kwitter.listeners.TwitterActionListener;
import com.krossovochkin.kwitter.toolbox.Constants;
import com.krossovochkin.kwitter.toolbox.KeyboardHelper;
import com.melnykov.fab.FloatingActionButton;

import twitter4j.Status;
import twitter4j.UserMentionEntity;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 20.02.14.
 */
public class SendTweetFragment extends Fragment {

    public static final String TAG = SendTweetFragment.class.getSimpleName();

    private TwitterActionListener twitterActionListener;

    public static SendTweetFragment newInstance() {
        return new SendTweetFragment();
    }

    public static SendTweetFragment newInstance(Status statusToReply) {
        SendTweetFragment sendTweetFragment = new SendTweetFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.STATUS_KEY, statusToReply);

        sendTweetFragment.setArguments(bundle);
        return sendTweetFragment;
    }

    public SendTweetFragment() {
        // instantiate only with newInstance() method
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_tweet, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof TwitterActionListener) {
            twitterActionListener = (TwitterActionListener) activity;
        } else {
            throw new ClassCastException("SendTweetFragment should be attached to activity " +
                    "that implements TwitterActionListener interface");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final EditText tweetEditText = (EditText) getView().findViewById(R.id.tweet_edit_text);
        final FloatingActionButton sendTweetButton = (FloatingActionButton) getView().findViewById(R.id.send_tweet_button);
        final TextView symbolCounterTextView = (TextView) getView().findViewById(R.id.char_counter_text_view);

        tweetEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (tweetEditText.getText() != null) {
                    int charsLeft = getResources().getInteger(R.integer.max_tweet_char_length) - tweetEditText.getText().length();
                    symbolCounterTextView.setText(String.valueOf(charsLeft));

                    if (charsLeft < 0) {
                        sendTweetButton.setEnabled(false);
                    } else {
                        sendTweetButton.setEnabled(true);
                    }
                }
            }
        });

        if (isReply()) {
            Status status = (Status) getArguments().getSerializable(Constants.STATUS_KEY);
            StringBuilder mentions = new StringBuilder();

            mentions.append("@");
            mentions.append(status.getUser().getScreenName());
            mentions.append(" ");

            for (UserMentionEntity mentionEntity : status.getUserMentionEntities()) {
                mentions.append("@");
                mentions.append(mentionEntity.getScreenName());
                mentions.append(" ");
            }
            tweetEditText.setText(mentions.toString());
        }

        sendTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (twitterActionListener != null) {
                    if (tweetEditText.getText() != null) {

                        if(isReply()) {
                            Status status = (Status) getArguments().getSerializable(Constants.STATUS_KEY);
                            twitterActionListener.sendReplyRequest(status, tweetEditText.getText().toString());
                        } else {
                            twitterActionListener.sendTweetRequest(tweetEditText.getText().toString());
                        }

                        getFragmentManager().popBackStack();
                    } else {
                        Log.e(TAG, "tweetEditText has no text");
                    }
                } else {
                    Log.e(TAG, "twitterActionListener from SendTweetFragment is null, did the fragment" +
                            "is attached to activity?");
                }
            }
        });

        KeyboardHelper.showSoftKeyboard(getActivity(), tweetEditText);
    }

    @Override
    public void onStop() {
        final View editText = getView().findViewById(R.id.tweet_edit_text);
        KeyboardHelper.hideSoftKeyboard(getActivity(), editText);

        super.onStop();
    }

    private boolean isReply() {
        return getArguments() != null && getArguments().getSerializable(Constants.STATUS_KEY) != null;
    }
}

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

package com.krossovochkin.kwitter.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.krossovochkin.kwitter.R;
import com.krossovochkin.kwitter.listeners.TweetActionListener;
import com.squareup.picasso.Picasso;

import twitter4j.ResponseList;
import twitter4j.Status;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 20.02.14.
 */
public class TimelineAdapter extends ArrayAdapter<Status> {

    public static final String TAG = TimelineAdapter.class.getSimpleName();

    private TweetActionListener tweetActionListener;

    public TimelineAdapter(Context context, ResponseList<Status> statuses, TweetActionListener tweetActionListener) {
        super(context, R.layout.list_view_item_timeline, statuses);

        this.tweetActionListener = tweetActionListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        final View view = layoutInflater.inflate(R.layout.list_view_item_timeline, parent, false);

        if (view != null) {
            final TextView statusTextView = (TextView) view.findViewById(R.id.status_text_view);
            String text = getItem(position).getText();
            int startPosition = text.indexOf('@');
            if (startPosition < 0) {
                statusTextView.setText(text);
            } else {
                SpannableString spannableString = new SpannableString(getItem(position).getText());
                do {
                int doubleDotPosition = text.indexOf(':', startPosition);
                int spacePosition = text.indexOf(' ', startPosition);
                int endPosition = (doubleDotPosition < 0 || spacePosition < doubleDotPosition) ? spacePosition : doubleDotPosition;
                spannableString.setSpan(new ForegroundColorSpan(Color.RED), startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                startPosition = text.indexOf('@', endPosition);
                } while (startPosition > 0);
                statusTextView.setText(spannableString);
            }

            final ImageView profileImage = (ImageView) view.findViewById(R.id.user_profile_image);
            Picasso.with(getContext()).load(getItem(position).getUser().getBiggerProfileImageURLHttps())
                    .into(profileImage);

            final Button replyButton = (Button) view.findViewById(R.id.reply_button);
            replyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tweetActionListener != null) {
                        tweetActionListener.sendReplyRequest(getItem(position));
                    } else {
                        Log.e(TAG, "tweetActionListener in TimelineAdapter is null. Did you set it in a proper way?");
                    }
                }
            });

            final Button retweetButton = (Button) view.findViewById(R.id.retweet_button);
            retweetButton.setBackgroundResource(getItem(position).isRetweetedByMe() ? R.drawable.retweet_on : R.drawable.retweet);
            retweetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tweetActionListener != null) {
                        tweetActionListener.sendRetweetRequest(retweetButton, getItem(position));
                    } else {
                        Log.e(TAG, "tweetActionListener in TimelineAdapter is null. Did you set it in a proper way?");
                    }
                }
            });

            final Button favoriteButton = (Button) view.findViewById(R.id.favorite_button);
            favoriteButton.setBackgroundResource(getItem(position).isFavorited() ? R.drawable.favorite_on : R.drawable.favorite);
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tweetActionListener != null) {
                        tweetActionListener.sendFavoriteRequest(favoriteButton, getItem(position));
                    } else {
                        Log.e(TAG, "tweetActionListener in TimelineAdapter is null. Did you set it in a proper way?");
                    }
                }
            });
        } else {
            Log.e(TAG, "getView failed, view is null");
        }

        return view;
    }
}

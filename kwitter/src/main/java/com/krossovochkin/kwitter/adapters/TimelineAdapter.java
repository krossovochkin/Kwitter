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
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.krossovochkin.kwitter.R;
import com.krossovochkin.kwitter.listeners.TweetActionListener;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 20.02.14.
 */
public class TimelineAdapter extends RecyclerView.Adapter {

    public static final String TAG = TimelineAdapter.class.getSimpleName();

    private List<Status> mData = new ArrayList<>();

    private TweetActionListener mListener = null;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_view_item_timeline, viewGroup, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        Status status = mData.get(position);
        holder.setText(status.getText());
        holder.setImage(status.getUser().getBiggerProfileImageURLHttps());
        holder.setUserName(status.getUser().getScreenName());
        holder.setText(mData.get(position).getText());
        holder.initButtons(mListener, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public int update(List<Status> statuses) {
        int scrollToPosition = 0;
        if (mData == null || mData.isEmpty()) {
            mData = statuses;
            scrollToPosition = 0;
        } else {
            int index = statuses.indexOf(mData.get(0));
            if (index == -1) {
                mData.addAll(0, statuses);
                scrollToPosition = 0;
            } else {
                mData.addAll(0, statuses.subList(0, index));
                scrollToPosition = index;
            }
        }
        this.notifyDataSetChanged();
        return scrollToPosition;
    }

    public Status getItem(int position) {
        return mData.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mStatusText;
        private TextView mUserName;
        private ImageView mProfileImage;
        private ImageButton mReplyButton;
        private ImageButton mRetweetButton;
        private ImageButton mFavoriteButton;

        public ViewHolder(View itemView) {
            super(itemView);

            mStatusText = (TextView) itemView.findViewById(R.id.status_text_view);
            mUserName = (TextView) itemView.findViewById(R.id.user_name);
            mProfileImage = (ImageView) itemView.findViewById(R.id.user_profile_image);
            mReplyButton = (ImageButton) itemView.findViewById(R.id.reply);
            mRetweetButton = (ImageButton) itemView.findViewById(R.id.retweet);
            mFavoriteButton = (ImageButton) itemView.findViewById(R.id.favorite);
        }

        public void setText(String text) {
            int startPosition = text.indexOf('@');
            if (startPosition < 0) {
                mStatusText.setText(text);
            } else {
                SpannableString spannableString = new SpannableString(text);
                do {
                    int doubleDotPosition = text.indexOf(':', startPosition);
                    int spacePosition = text.indexOf(' ', startPosition);
                    int endPosition = (doubleDotPosition < 0 || spacePosition < doubleDotPosition) ? spacePosition : doubleDotPosition;
                    if (endPosition == -1) {
                        endPosition = spannableString.length();
                    }
                    spannableString.setSpan(new ForegroundColorSpan(mStatusText.getContext().getResources().getColor(R.color.colorSecondaryDark)), startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    startPosition = text.indexOf('@', endPosition);
                } while (startPosition > 0);
                mStatusText.setText(spannableString);
            }
        }

        public void setUserName(String userName) {
            mUserName.setText(userName);
        }

        public void setImage(String url) {
            Picasso.with(mProfileImage.getContext()).load(url).into(mProfileImage);
        }

        public void initButtons(final TweetActionListener listener, final int position) {
            if (listener == null) {
                return;
            }
            mReplyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.sendReplyRequest(position);
                }
            });
            mRetweetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.sendRetweetRequest(position);
                }
            });
            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.sendFavoriteRequest(position);
                }
            });
        }
    }

    public TimelineAdapter(TweetActionListener listener) {
        super();

        mListener = listener;
    }
}

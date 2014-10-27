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
import android.widget.ImageView;
import android.widget.TextView;

import com.krossovochkin.kwitter.R;
import com.krossovochkin.kwitter.listeners.TweetActionListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 20.02.14.
 */
public class TimelineAdapter extends RecyclerView.Adapter {

    public static final String TAG = TimelineAdapter.class.getSimpleName();

    private List<Status> mData = new ArrayList<Status>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_view_item_timeline, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        Status status = mData.get(position);
        holder.setText(status.getText());
        holder.setImage(status.getUser().getBiggerProfileImageURLHttps());

        ((ViewHolder) viewHolder).setText(mData.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addAll(ResponseList<Status> statuses) {
        mData.addAll(statuses);
    }

    public Status getItem(int position) {
        return mData.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setText(String text) {
            final TextView statusTextView = (TextView) mView.findViewById(R.id.status_text_view);
            int startPosition = text.indexOf('@');
            if (startPosition < 0) {
                statusTextView.setText(text);
            } else {
                SpannableString spannableString = new SpannableString(text);
                do {
                    int doubleDotPosition = text.indexOf(':', startPosition);
                    int spacePosition = text.indexOf(' ', startPosition);
                    int endPosition = (doubleDotPosition < 0 || spacePosition < doubleDotPosition) ? spacePosition : doubleDotPosition;
                    if (endPosition == -1) {
                        endPosition = spannableString.length();
                    }
                    spannableString.setSpan(new ForegroundColorSpan(mView.getContext().getResources().getColor(R.color.colorSecondaryDark)), startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    startPosition = text.indexOf('@', endPosition);
                } while (startPosition > 0);
                statusTextView.setText(spannableString);
            }
        }

        public void setImage(String url) {
            final ImageView profileImage = (ImageView) mView.findViewById(R.id.user_profile_image);
            Picasso.with(mView.getContext()).load(url).into(profileImage);
        }
    }

    public TimelineAdapter() {
        super();
    }
}

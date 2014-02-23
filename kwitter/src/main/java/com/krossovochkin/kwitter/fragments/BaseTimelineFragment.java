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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.krossovochkin.kwitter.R;
import com.krossovochkin.kwitter.adapters.TimelineAdapter;
import com.krossovochkin.kwitter.animations.ExpandAnimation;
import com.krossovochkin.kwitter.listeners.FavoriteListener;
import com.krossovochkin.kwitter.listeners.GetTimelineListener;
import com.krossovochkin.kwitter.listeners.RetweetListener;
import com.krossovochkin.kwitter.listeners.TweetActionListener;
import com.krossovochkin.kwitter.tasks.FavoriteAsyncTask;
import com.krossovochkin.kwitter.tasks.GetTimelineAsyncTask;
import com.krossovochkin.kwitter.tasks.RetweetAsyncTask;
import com.krossovochkin.kwitter.toolbox.Constants;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 23.02.14.
 */
public abstract class BaseTimelineFragment extends Fragment implements GetTimelineListener, TweetActionListener,
        RetweetListener, FavoriteListener {

    protected Twitter twitter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initTwitter();
        sendGetTimelineRequest();
    }

    protected abstract void sendGetTimelineRequest();

    private void initTwitter() {
        if(getArguments() != null) {
            twitter = (Twitter) getArguments().getSerializable(Constants.TWITTER_KEY);
        }
    }

    @Override
    public void onGetTimelineSuccess(ResponseList<Status> statuses) {
        initListView(statuses);
    }

    @Override
    public void onGetTimelineError(TwitterException exception) {
        Toast.makeText(getActivity(), R.string.error_loading_timeline, Toast.LENGTH_SHORT).show();
    }

    private void initListView(ResponseList<Status> statuses) {
        ListView listView = (ListView) getView().findViewById(R.id.list_view);
        listView.setAdapter(new TimelineAdapter(getActivity(), statuses, this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                View toolbar = view.findViewById(R.id.toolbar);

                // Creating the expand animation for the item
                ExpandAnimation expandAnimation = new ExpandAnimation(toolbar, 500);

                // Start the animation on the toolbar
                toolbar.startAnimation(expandAnimation);
            }
        });
    }

    @Override
    public void sendReplyRequest(Status statusToReply) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.fade_out, R.anim.fade_in, R.anim.slide_down)
                .addToBackStack(SendTweetFragment.TAG)
                .add(R.id.content_frame, SendTweetFragment.newInstance(statusToReply))
                .commit();
    }

    @Override
    public void sendRetweetRequest(Button retweetButton, Status statusToRetweet) {
        new RetweetAsyncTask(twitter, retweetButton, statusToRetweet.getId(), this).execute();
    }

    @Override
    public void sendFavoriteRequest(Button favoriteButton, Status statusToFavorite) {
        new FavoriteAsyncTask(twitter, favoriteButton, statusToFavorite.getId(), this).execute();
    }

    @Override
    public void onRetweetSuccess(Button retweetButton) {
        retweetButton.setBackgroundResource(R.drawable.retweet_on);
    }

    @Override
    public void onRetweetError() {
        Toast.makeText(getActivity(), R.string.error_retweet, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavoriteSuccess(Button favoriteButton) {
        favoriteButton.setBackgroundResource(R.drawable.favorite_on);
    }

    @Override
    public void onFavoriteError() {
        Toast.makeText(getActivity(), R.string.error_favorite, Toast.LENGTH_SHORT).show();
    }
}

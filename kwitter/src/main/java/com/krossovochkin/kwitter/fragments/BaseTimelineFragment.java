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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.krossovochkin.kwitter.R;
import com.krossovochkin.kwitter.adapters.TimelineAdapter;
import com.krossovochkin.kwitter.listeners.FavoriteListener;
import com.krossovochkin.kwitter.listeners.GetTimelineListener;
import com.krossovochkin.kwitter.listeners.RetweetListener;
import com.krossovochkin.kwitter.listeners.TweetActionListener;
import com.krossovochkin.kwitter.tasks.FavoriteAsyncTask;
import com.krossovochkin.kwitter.tasks.RetweetAsyncTask;
import com.krossovochkin.kwitter.toolbox.Constants;
import com.krossovochkin.kwitter.toolbox.FileManager;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 23.02.14.
 */
public abstract class BaseTimelineFragment extends Fragment implements GetTimelineListener, TweetActionListener,
        RetweetListener, FavoriteListener, SwipeRefreshLayout.OnRefreshListener {

    protected Twitter mTwitter = null;

    private SwipeRefreshLayout mSwipeRefreshLayout = null;

    private RecyclerView mRecyclerView;
    private TimelineAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTwitter();
        initListView();

        List<Status> statuses = FileManager.loadStatuses(getActivity(), getStatusesFolder());
        if (statuses == null || statuses.isEmpty()) {
            sendGetTimelineRequest(getActivity());
        } else {
            mAdapter.update(statuses);
        }
    }

    protected abstract void sendGetTimelineRequest(Context context);

    private void initTwitter() {
        if(getArguments() != null) {
            mTwitter = (Twitter) getArguments().getSerializable(Constants.TWITTER_KEY);
        }
    }

    @Override
    public void onGetTimelineSuccess(ResponseList<Status> statuses) {
        refreshListView(statuses);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onGetTimelineError(TwitterException exception) {
        Toast.makeText(getActivity(), R.string.error_loading_timeline, Toast.LENGTH_SHORT).show();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void initListView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.colorSecondaryDark),
                getResources().getColor(R.color.colorSecondary),
                getResources().getColor(R.color.colorSecondaryDark),
                getResources().getColor(R.color.colorSecondary));

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new TimelineAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        if (mFab != null) {
            mFab.attachToRecyclerView(mRecyclerView);
        }
    }

    private void refreshListView(ResponseList<Status> statuses) {
        int position = mAdapter.update(statuses);
        mLayoutManager.scrollToPositionWithOffset(position, 0);
    }

    @Override
    public void onRefresh() {
        if (getActivity() != null) {
            sendGetTimelineRequest(getActivity());
        } else {
            // TODO: do something
        }
    }

    @Override
    public void sendReplyRequest(int statusToReplyIndex) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack(SendTweetFragment.TAG)
                .replace(R.id.content_frame, SendTweetFragment.newInstance(mAdapter.getItem(statusToReplyIndex)))
                .commit();
    }

    @Override
    public void sendRetweetRequest(int statusToRetweetIndex) {
        new RetweetAsyncTask(mTwitter, mAdapter.getItem(statusToRetweetIndex).getId(), this).execute();
    }

    @Override
    public void sendFavoriteRequest(int statusToFavoriteIndex) {
        new FavoriteAsyncTask(mTwitter, mAdapter.getItem(statusToFavoriteIndex).getId(), this).execute();
    }

    @Override
    public void onRetweetSuccess() {
        Toast.makeText(getActivity(), getString(R.string.success_retweet), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRetweetError() {
        Toast.makeText(getActivity(), R.string.error_retweet, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavoriteSuccess() {
        Toast.makeText(getActivity(), getString(R.string.success_favorite), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavoriteError() {
        Toast.makeText(getActivity(), R.string.error_favorite, Toast.LENGTH_SHORT).show();
    }

    private FloatingActionButton mFab = null;

    private String getStatusesFolder() {
        if (this instanceof TimelineFragment) {
            return FileManager.FOLDER_NAME_HOME_TIMELINE;
        } else if (this instanceof MentionsFragment) {
            return FileManager.FOLDER_NAME_MENTIONS_TIMELINE;
        } else {
            return null;
        }
    }
}

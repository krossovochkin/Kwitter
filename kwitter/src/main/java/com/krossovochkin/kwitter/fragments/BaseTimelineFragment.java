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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.melnykov.fab.FloatingActionButton;

import java.util.Collections;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 23.02.14.
 */
public abstract class BaseTimelineFragment extends Fragment implements GetTimelineListener, TweetActionListener,
        RetweetListener, FavoriteListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int NO_ITEM = -1;

    protected Twitter twitter = null;
    protected boolean mIsInContextualMode = false;
    protected int mCurrentActionItemIndex = NO_ITEM;
    private ActionMode mCurrentActionMode = null;

    private SwipeRefreshLayout mSwipeRefreshLayout = null;

    private RecyclerView mRecyclerView;
    private TimelineAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private class ActionBarCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            if (actionMode.getMenuInflater() != null) {
                actionMode.getMenuInflater().inflate(R.menu.actions, menu);
                return true;
            }
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.reply:
                    sendReplyRequest(mCurrentActionItemIndex);
                    break;
                case R.id.retweet:
                    sendRetweetRequest(mCurrentActionItemIndex);
                    break;
                case R.id.favorite:
                    sendFavoriteRequest(mCurrentActionItemIndex);
                    break;
                default:
                    break;
            }

            if (mIsInContextualMode) {
                if (mCurrentActionMode != null) {
                    mCurrentActionMode.finish();
                    mCurrentActionMode = null;
                }

                mIsInContextualMode = false;
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            if (mCurrentActionItemIndex != NO_ITEM) {
                //TODO: uncomment
//                mListView.setItemChecked(mCurrentActionItemIndex, false);
                mCurrentActionItemIndex = NO_ITEM;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initTwitter();
        initListView();
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
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_green_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark);

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new TimelineAdapter();
        mRecyclerView.setAdapter(mAdapter);

        if (mFab != null) {
            mFab.attachToRecyclerView(mRecyclerView);
        }

//        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
//        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
//                mCurrentActionMode = getActivity().startActionMode(new ActionBarCallback());
//                mIsInContextualMode = true;
//                mListView.setItemChecked(position, true);
//                mCurrentActionItemIndex = position;
//                return true;
//            }
//        });
    }

    private void refreshListView(ResponseList<Status> statuses) {
        int position = mAdapter.update(statuses);
        mLayoutManager.scrollToPositionWithOffset(position, 0);
    }

    @Override
    public void onRefresh() {
        sendGetTimelineRequest();
    }

    @Override
    public void sendReplyRequest(int statusToReplyIndex) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.fade_out, R.anim.fade_in, R.anim.slide_down)
                .addToBackStack(SendTweetFragment.TAG)
                .add(R.id.content_frame, SendTweetFragment.newInstance(mAdapter.getItem(statusToReplyIndex)))
                .commit();
    }

    @Override
    public void sendRetweetRequest(int statusToRetweetIndex) {

        new RetweetAsyncTask(twitter, mAdapter.getItem(statusToRetweetIndex).getId(), this).execute();
    }

    @Override
    public void sendFavoriteRequest(int statusToFavoriteIndex) {
        new FavoriteAsyncTask(twitter, mAdapter.getItem(statusToFavoriteIndex).getId(), this).execute();
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
}

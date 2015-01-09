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
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.krossovochkin.kwitter.R;
import com.krossovochkin.kwitter.adapters.ViewPagerAdapter;
import com.krossovochkin.kwitter.toolbox.Constants;
import com.melnykov.fab.FloatingActionButton;

import twitter4j.Twitter;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 20.02.14.
 */
public class MainFragment extends android.support.v4.app.Fragment {

    public static final String TAG = MainFragment.class.getSimpleName();

    private Twitter mTwitter;
    private ViewPager mViewPager;

    public static MainFragment newInstance(Twitter twitter) {
        MainFragment fragment = new MainFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.TWITTER_KEY, twitter);
        fragment.setArguments(bundle);

        return fragment;
    }

    public MainFragment() {
        // instantiate only with newInstance() method
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTwitter();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity(), mTwitter, getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(viewPagerAdapter);

        PagerSlidingTabStrip titleIndicator = (PagerSlidingTabStrip) getView().findViewById(R.id.titles);
        titleIndicator.setViewPager(mViewPager);
    }

    private void initTwitter() {
        if(getArguments() != null) {
            mTwitter = (Twitter) getArguments().getSerializable(Constants.TWITTER_KEY);
        } else {
            throw new IllegalArgumentException("initTwitter in MainFragment failed, no such serializable in arguments");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        final FloatingActionButton sendTweetButton = (FloatingActionButton) view.findViewById(R.id.fab);
        sendTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSendTweetFragment();
            }
        });

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        return view;
    }

    private void showSendTweetFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack(SendTweetFragment.TAG)
                .replace(R.id.content_frame, SendTweetFragment.newInstance(), SendTweetFragment.TAG)
                .commit();
    }
}

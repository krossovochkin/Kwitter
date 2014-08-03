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

import com.krossovochkin.kwitter.tasks.GetTimelineAsyncTask;
import com.krossovochkin.kwitter.toolbox.Constants;

import twitter4j.Twitter;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 20.02.14.
 */
public class TimelineFragment extends BaseTimelineFragment {

    public static TimelineFragment newInstance(Twitter twitter) {
        TimelineFragment fragment = new TimelineFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.TWITTER_KEY, twitter);
        fragment.setArguments(bundle);

        return fragment;
    }

    private TimelineFragment() {
        // instantiate only with newInstance() method
    }

    protected void sendGetTimelineRequest() {
        new GetTimelineAsyncTask(twitter, this).execute();
    }
}

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

package com.krossovochkin.kwitter.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.krossovochkin.kwitter.listeners.GetTimelineListener;
import com.krossovochkin.kwitter.toolbox.FileManager;

import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 20.02.14.
 */
public class GetMentionsAsyncTask extends AsyncTask<Object, Boolean, Boolean> {

    public static final String TAG = GetMentionsAsyncTask.class.getSimpleName();

    private Context mContext;
    private Twitter twitter;
    private GetTimelineListener getTimelineListener;
    private ResponseList<twitter4j.Status> statuses;
    private TwitterException exception;

    public GetMentionsAsyncTask(Context context, Twitter twitter, GetTimelineListener getTimelineListener) {
        super();

        mContext = context;
        this.twitter = twitter;
        this.getTimelineListener = getTimelineListener;
    }

    @Override
    protected Boolean doInBackground(Object... objects) {
        return getTimeline();
    }

    private boolean getTimeline() {
        try {
            this.statuses = twitter.getMentionsTimeline();
            FileManager.saveStatuses(mContext, statuses, FileManager.FOLDER_NAME_MENTIONS_TIMELINE);
            return true;
        } catch (TwitterException e) {
            this.exception = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean isSuccessful) {
        super.onPostExecute(isSuccessful);

        if(isCancelled()) {
            return;
        }

        if(getTimelineListener != null) {
            if(isSuccessful) {
                getTimelineListener.onGetTimelineSuccess(statuses);
            } else {
                getTimelineListener.onGetTimelineError(exception);
            }
        }
    }
}

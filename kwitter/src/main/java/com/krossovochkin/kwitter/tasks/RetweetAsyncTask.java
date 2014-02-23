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

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import com.krossovochkin.kwitter.listeners.RetweetListener;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 23.02.14.
 */
public class RetweetAsyncTask extends AsyncTask<Object, Boolean, Boolean> {

    public static final String TAG = RetweetAsyncTask.class.getSimpleName();

    private Twitter twitter;
    private long tweetId;
    private RetweetListener retweetListener;
    private Button retweetButton;

    public RetweetAsyncTask(Twitter twitter, Button retweetButton, long tweetId, RetweetListener retweetListener) {
        this.twitter = twitter;
        this.retweetListener = retweetListener;
        this.tweetId = tweetId;
        this.retweetButton = retweetButton;
    }

    @Override
    protected Boolean doInBackground(Object... objects) {
        if(twitter != null) {
            try {
                twitter.retweetStatus(tweetId);
                return true;
            } catch (TwitterException e) {
                Log.e(TAG, "Retweet failed: " + e.getMessage());
                return false;
            }
        } else {
            Log.e(TAG, "Retweet failed: twitter is null. Did you set it in a proper way?");
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean isSuccessful) {
        super.onPostExecute(isSuccessful);

        if(isCancelled()) {
            return;
        }

        if(retweetListener != null) {
            if(isSuccessful) {
                retweetListener.onRetweetSuccess(retweetButton);
            } else {
                retweetListener.onRetweetError();
            }
        }
    }
}

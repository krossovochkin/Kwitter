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

import com.krossovochkin.kwitter.listeners.SendTweetListener;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 20.02.14.
 */
public class SendTweetAsyncTask extends AsyncTask<Object, Boolean, Boolean> {

    public static final String TAG = SendTweetAsyncTask.class.getSimpleName();

    private Twitter twitter;
    private SendTweetListener sendTweetListener;
    private String tweet;

    public SendTweetAsyncTask(Twitter twitter, String tweet, SendTweetListener sendTweetListener) {
        super();
        this.twitter = twitter;
        this.sendTweetListener = sendTweetListener;
        this.tweet = tweet;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        sendTweetListener.onPreSendTweet();
    }

    @Override
    protected Boolean doInBackground(Object... objects) {
        return sendTweet(twitter, tweet);
    }

    @Override
    protected void onPostExecute(Boolean isTweetSent) {
        super.onPostExecute(isTweetSent);

        if(isCancelled()) {
            return;
        }

        sendTweetListener.onPostSendTweet(isTweetSent);
    }

    private boolean sendTweet(final Twitter twitter, final String tweet) {
        try {
            twitter.updateStatus(tweet);
            return true;
        } catch (TwitterException te) {
            Log.e(TAG, "Failed send tweet: " + te.getMessage());
            return false;
        }
    }
}
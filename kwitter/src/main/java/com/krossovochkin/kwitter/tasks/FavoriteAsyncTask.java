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

import com.krossovochkin.kwitter.listeners.FavoriteListener;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 23.02.14.
 */
public class FavoriteAsyncTask extends AsyncTask<Object, Boolean, Boolean> {

    public static final String TAG = FavoriteAsyncTask.class.getSimpleName();

    private Twitter twitter;
    private long tweetId;
    private FavoriteListener favoriteListener;

    public FavoriteAsyncTask(Twitter twitter, long tweetId, FavoriteListener favoriteListener) {
        this.twitter = twitter;
        this.favoriteListener = favoriteListener;
        this.tweetId = tweetId;
    }

    @Override
    protected Boolean doInBackground(Object... objects) {
        if(twitter != null) {
            try {
                twitter.createFavorite(tweetId);
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

        if(favoriteListener != null) {
            if(isSuccessful) {
                favoriteListener.onFavoriteSuccess();
            } else {
                favoriteListener.onFavoriteError();
            }
        }
    }
}

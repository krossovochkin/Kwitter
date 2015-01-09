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

package com.krossovochkin.kwitter.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.krossovochkin.kwitter.BuildConfig;
import com.krossovochkin.kwitter.R;
import com.krossovochkin.kwitter.fragments.MainFragment;
import com.krossovochkin.kwitter.listeners.SendTweetListener;
import com.krossovochkin.kwitter.listeners.TwitterActionListener;
import com.krossovochkin.kwitter.tasks.SendReplyAsyncTask;
import com.krossovochkin.kwitter.tasks.SendTweetAsyncTask;
import com.krossovochkin.kwitter.toolbox.Settings;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends ActionBarActivity implements TwitterActionListener, SendTweetListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int NOTIFICATION_ID_SENDING_TWEET = 17;
    public static final int NOTIFICATION_ID_ERROR_SENDING_TWEET = 18;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private Twitter mTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        showMainFragment();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mToggle.syncState();
    }

    private void showMainFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, MainFragment.newInstance(mTwitter), MainFragment.TAG)
                .commit();
    }

    @Override
    public void sendTweetRequest(String tweet) {
        new SendTweetAsyncTask(mTwitter, tweet, this).execute();
    }

    @Override
    public void sendReplyRequest(Status statusToReply, String tweet) {
        new SendReplyAsyncTask(mTwitter, tweet, statusToReply.getId(), this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            showSettingsActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettingsActivity() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void init() {
        initToolbar();
        initTwitter();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        mDrawerLayout.setDrawerListener(mToggle);
        setSupportActionBar(mToolbar);
    }

    private void initTwitter() {
        boolean authDataExists = Settings.getBoolean(this, Settings.AUTH_DATA_EXISTS, false);

        if (authDataExists) {
            ConfigurationBuilder cb = new ConfigurationBuilder();

            cb.setDebugEnabled(BuildConfig.DEBUG)
                    .setOAuthConsumerKey(getString(R.string.twitter_consumer_key))
                    .setOAuthConsumerSecret(getString(R.string.twitter_consumer_secret))
                    .setOAuthAccessToken(Settings.getString(this, Settings.ACCESS_TOKEN_KEY))
                    .setOAuthAccessTokenSecret(Settings.getString(this, Settings.ACCESS_TOKEN_SECRET_KEY))
                    .setJSONStoreEnabled(true);


            TwitterFactory tf = new TwitterFactory(cb.build());
            mTwitter = tf.getInstance();
        } else {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        }
    }

    @Override
    public void onPreSendTweet() {
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.sending_tweet))
                .setSmallIcon(R.drawable.ic_launcher)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_SENDING_TWEET, notification);
    }

    @Override
    public void onPostSendTweet(boolean isTweetSent) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID_SENDING_TWEET);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if(!isTweetSent) {


            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.error_sendgin_tweet))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .build();
            notificationManager.notify(NOTIFICATION_ID_SENDING_TWEET, notification);
        }
    }
}

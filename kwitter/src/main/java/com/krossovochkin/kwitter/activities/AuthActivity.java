package com.krossovochkin.kwitter.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dd.CircularProgressButton;
import com.krossovochkin.kwitter.BuildConfig;
import com.krossovochkin.kwitter.R;
import com.krossovochkin.kwitter.toolbox.Settings;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 03.08.14.
 */
public class AuthActivity extends Activity {

    private static final String TAG = AuthActivity.class.getSimpleName();

    // Twitter oauth urls
    private static final String URL_TWITTER_AUTH = "auth_url";
    private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    private static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

    private Twitter mTwitter;
    private RequestToken mRequestToken;
    private AccessToken mAccessToken;

    private WebView mWebView;
    private CircularProgressButton mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(getString(R.string.oauth_scheme))) {
                    Uri uri = Uri.parse(url);
                    String oauthVerifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
                    new TwitterGetAccessTokenTask(oauthVerifier).execute();
                    return true;
                }
                return false;
            }
        });

        initTwitter();

        mLoginButton = (CircularProgressButton) findViewById(R.id.btn_login);
        mLoginButton.setIndeterminateProgressMode(true);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginButton.setProgress(1); // start indeterminate
                new TwitterAuthenticateTask().execute();
            }
        });
    }

    private void initTwitter() {
        ConfigurationBuilder cb = new ConfigurationBuilder();

        cb.setDebugEnabled(BuildConfig.DEBUG)
                .setOAuthConsumerKey(getString(R.string.twitter_consumer_key))
                .setOAuthConsumerSecret(getString(R.string.twitter_consumer_secret));

        TwitterFactory tf = new TwitterFactory(cb.build());
        mTwitter = tf.getInstance();
    }

    private class TwitterAuthenticateTask extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                mRequestToken = mTwitter.getOAuthRequestToken(
                        getString(R.string.oauth_scheme) + "://" + getString(R.string.oauth_host));
                return true;
            } catch (TwitterException e) {
                Log.e(TAG, e.getMessage(), e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            if (isSuccess) {
                mWebView.setVisibility(View.VISIBLE);
                mLoginButton.setVisibility(View.GONE);
                mWebView.loadUrl(mRequestToken.getAuthenticationURL());
            } else {
                Log.e(TAG, TwitterAuthenticateTask.class.getCanonicalName() + " fails");
            }
        }
    }

    private class TwitterGetAccessTokenTask extends AsyncTask<String, String, Boolean> {

        private String mOAuthVerifier;

        private TwitterGetAccessTokenTask(String mOAuthVerifier) {
            this.mOAuthVerifier = mOAuthVerifier;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                mAccessToken = mTwitter.getOAuthAccessToken(mRequestToken, mOAuthVerifier);
                Settings.saveAuthData(AuthActivity.this, mAccessToken.getToken(), mAccessToken.getTokenSecret());
                return true;
            } catch (TwitterException e) {
                Log.e(TAG, TwitterGetAccessTokenTask.class.getCanonicalName() + " fails, " + e.getMessage(), e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            if (isSuccess) {
                startActivity(new Intent(AuthActivity.this, MainActivity.class));
                AuthActivity.this.finish();
                Log.e(TAG, "success: " + mAccessToken.getToken() + ", " + mAccessToken.getTokenSecret());
            } else {
                Log.e(TAG, "fail");
            }
        }
    }
}

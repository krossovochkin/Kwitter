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

package com.krossovochkin.kwitter.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.krossovochkin.kwitter.R;
import com.krossovochkin.kwitter.fragments.MentionsFragment;
import com.krossovochkin.kwitter.fragments.TimelineFragment;
import com.krossovochkin.kwitter.fragments.ViewPagerFragment;

import java.util.Locale;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 20.02.14.
 */
public class ViewPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    private Context context;
    private Twitter twitter;

    public ViewPagerAdapter(Context context, Twitter twitter, FragmentManager fm) {
        super(fm);

        this.context = context;
        this.twitter = twitter;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return TimelineFragment.newInstance(twitter);
            case 1:
                return MentionsFragment.newInstance(twitter);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return context.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return context.getString(R.string.title_section2).toUpperCase(l);
        }
        return null;
    }
}
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

package com.krossovochkin.kwitter.listeners;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 23.02.14.
 */
public interface TweetActionListener {

    public void sendReplyRequest(int statusToReplyIndex);

    public void sendRetweetRequest(int statusToRetweetIndex);

    public void sendFavoriteRequest(int statusToFavoriteIndex);

}

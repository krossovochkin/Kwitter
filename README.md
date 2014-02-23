# Kwitter – simple twitter client for android 4.0 and above #

# Overview #

Client possibilities:
 - reading last 20 tweets in timeline
 - reading last 20 tweets in mentions timeline
 - send tweet (without images)
 - reply to the tweet
 - retweet tweet
 - favorite tweet
 
# How to use #
To use this client, first you should register your application on [Twitter dev site](https://apps.twitter.com/)
And get there 4 keys:
 - consumer key
 - consumer secret
 - access token
 - access token secret
Then you should copy these keys into ../kwitter/src/main/res/values/strings.xml
instead of:

`<string name="twitter_consumer_key">""</string>`<br>
`<string name="twitter_consumer_secret">""</string>`<br>
`<string name="twitter_access_token">""</string>`<br>
`<string name="twitter_access_token_secret">""</string>`<br>

Then build the project and you're done!
 
# TODO: #
New features:
 - oauth implementation inside application
 - clean color sceme
 - update timelines (by pull-to-refresh (like actionbar-pulltorefresh with smoothProgressBar))
 - load older tweet in timelines
 - unretweet/unfavorite tweet
 - user profile (with follow/unfollow logic)
 - direct messages
 - tablet optimizations
 - screen with details about tweet (with attached images)
 - make urls in tweet clickable
Issues:
 - cancel asynctasks on exit/stop
 - close expanded list view item on scroll
 - close expanded list view item, when another list view item was expanded
 
# Thanks for #
In this project libraries were used:
 - [Twitter4j](https://github.com/yusuke/twitter4j) (working with Twitter API)
 - [ViewPagerIndicator](https://github.com/JakeWharton/Android-ViewPagerIndicator) (Underline indicator for view pager)
 - [Picasso](https://github.com/square/picasso) (loading by url and caching images)
 - [ListView item expand animation](https://github.com/Udinic/SmallExamples/tree/master/ExpandAnimationExample)
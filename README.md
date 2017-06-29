DEPRECATED
=========

# Kwitter – social client with material like design #

# Overview #

Client possibilities:
 - reading last 20 tweets in timeline
 - reading last 20 tweets in mentions timeline
 - update last 20 tweets in timelines
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
 
# Thanks for #
In this project libraries were used:
 - [Twitter4j](https://github.com/yusuke/twitter4j) (working with Twitter API)
 - [Picasso](https://github.com/square/picasso) (loading by url and caching images)
 - [Circular Progress Button](https://github.com/dmytrodanylyk/circular-progress-button) (login button)
 - [Pager Sliding Tab Strip](https://github.com/jpardogo/PagerSlidingTabStrip) (tabs)
 - [Floating Action Button](https://github.com/makovkastar/FloatingActionButton) (tweet button)
 - [About Libraries](https://github.com/mikepenz/AboutLibraries) (open-source libs description)

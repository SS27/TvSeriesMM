package com.spstanchev.tvseries.common;

import android.net.Uri;

/**
 * Created by Stefan on 2/8/2015.
 */
public interface Constants {

    //Fragments Titles
    public static final String FRAGMENT_TITLE_MY_SHOWS = "MY SHOWS";
    public static final String FRAGMENT_TITLE_UNWATCHED = "UNWATCHED SHOWS";

    //JSON URLs
    public static final String JSON_SHOW_URL = "http://api.tvmaze.com/shows/";
    public static final String JSON_QUERY_SHOWS_URL = "http://api.tvmaze.com/search/shows?q=";

    //JSON TAGS for SHOWS
    public static final String TAG_SHOW = "show";
    public static final String TAG_ID = "id";
    public static final String TAG_URL = "url";
    public static final String TAG_NAME = "name";
    public static final String TAG_TYPE = "type";
    public static final String TAG_STATUS = "status";
    public static final String TAG_RUNTIME = "runtime";
    public static final String TAG_PREMIERED = "premiered";
    public static final String TAG_RATING = "rating";
    public static final String TAG_AVERAGE = "average";
    public static final String TAG_NETWORK = "network";
    public static final String TAG_COUNTRY = "country";
    public static final String TAG_CODE = "code";
    public static final String TAG_TIMEZONE = "timezone";
    public static final String TAG_IMAGE = "image";
    public static final String TAG_MEDIUM = "medium";
    public static final String TAG_ORIGINAL = "original";
    public static final String TAG_SUMMARY = "summary";
    public static final String TAG_LINKS = "_links";
    public static final String TAG_SELF = "self";
    public static final String TAG_EPISODES = "episodes";
    public static final String TAG_CAST = "cast";
    public static final String TAG_PREVIOUS_EPISODE = "previousepisode";
    public static final String TAG_NEXT_EPISODE = "nextepisode";
    public static final String TAG_HREF = "href";

    //JSON TAGS for EPISODES
    public static final String TAG_SEASON = "season";
    public static final String TAG_NUMBER = "number";
    public static final String TAG_AIRDATE = "airdate";
    public static final String TAG_AIRTIME = "airtime";
    public static final String TAG_AIRSTAMP = "airstamp";
    public static final String TAG_SHOW_ID = "showid";
    public static final String TAG_WATCHED = "watched";

    //JSON TAGS for CAST
    public static final String TAG_PERSON = "person";
    public static final String TAG_CHARACTER = "character";

    // Table Names and Content URIs
    public static final String TABLE_SHOWS = "shows";
    public static final String TABLE_EPISODES = "episodes";
    public static final String TABLE_CAST = "cast";
    public static final String AUTHORITY = "com.spstanchev.tvseries.providers.ShowProvider";
    public static final String SHOW_URL = "content://" + AUTHORITY + "/" + TABLE_SHOWS;
    public static final Uri SHOW_CONTENT_URI = Uri.parse(SHOW_URL);
    public static final String EPISODES_URL = "content://" + AUTHORITY + "/" + TABLE_EPISODES;
    public static final Uri EPISODES_CONTENT_URI = Uri.parse(EPISODES_URL);
    public static final String EPISODES_SHOW_ID_URL = "content://" + AUTHORITY + "/" + TABLE_EPISODES + TAG_SHOW_ID;
    public static final String CAST_URL = "content://" + AUTHORITY + "/" + TABLE_CAST;
    public static final Uri CAST_CONTENT_URI = Uri.parse(CAST_URL);
    public static final String CAST_SHOW_ID_URL = "content://" + AUTHORITY + "/" + TABLE_CAST + TAG_SHOW_ID;

    //Suggested shows ids
    //public static final int[] suggestedShowsIds = {335, 13, 2, 67, 58, 6, 177, 111, 63, 62, 210, 73, 133, 31, 88};

}

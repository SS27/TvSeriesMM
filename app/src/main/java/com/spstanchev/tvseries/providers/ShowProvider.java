package com.spstanchev.tvseries.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.spstanchev.tvseries.common.Constants;
import com.spstanchev.tvseries.models.Country;
import com.spstanchev.tvseries.models.Episode;
import com.spstanchev.tvseries.models.Image;
import com.spstanchev.tvseries.models.Links;
import com.spstanchev.tvseries.models.Network;
import com.spstanchev.tvseries.models.Show;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Stefan on 2/15/2015.
 */
public class ShowProvider extends ContentProvider {

    private DBHelper dbHelper;
    private SQLiteDatabase database;

    // projection map for a query
    private static HashMap<String, String> showsProjectionMap;
    private static HashMap<String, String> episodesProjectionMap;
    private static HashMap<String, String> castProjectionMap;

    private static final int SHOWS = 1;
    private static final int SHOW_ID = 2;
    private static final int EPISODES = 3;
    private static final int EPISODE_ID = 4;
    private static final int EPISODE_SHOW_ID = 5;
    private static final int CAST = 6;
    private static final int CAST_ID = 7;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Constants.AUTHORITY, Constants.TABLE_SHOWS, SHOWS);
        uriMatcher.addURI(Constants.AUTHORITY, Constants.TABLE_SHOWS + "/#", SHOW_ID);
        uriMatcher.addURI(Constants.AUTHORITY, Constants.TABLE_EPISODES, EPISODES);
        uriMatcher.addURI(Constants.AUTHORITY, Constants.TABLE_EPISODES + "/#", EPISODE_ID);
        uriMatcher.addURI(Constants.AUTHORITY, Constants.TABLE_EPISODES + Constants.TAG_SHOW_ID + "/#", EPISODE_SHOW_ID);
        uriMatcher.addURI(Constants.AUTHORITY, Constants.TABLE_CAST, CAST);
        uriMatcher.addURI(Constants.AUTHORITY, Constants.TABLE_CAST + "/#", CAST_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // get readable database
        database = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case SHOWS:
                queryBuilder.setTables(Constants.TABLE_SHOWS);
                queryBuilder.setProjectionMap(showsProjectionMap);
                break;
            case SHOW_ID:
                queryBuilder.setTables(Constants.TABLE_SHOWS);
                queryBuilder.setProjectionMap(showsProjectionMap);
                queryBuilder.appendWhere(Constants.TAG_ID + "=" + uri.getLastPathSegment());
                break;
            case EPISODES:
                queryBuilder.setTables(Constants.TABLE_EPISODES);
                queryBuilder.setProjectionMap(episodesProjectionMap);
                break;
            case EPISODE_ID:
                queryBuilder.setTables(Constants.TABLE_EPISODES);
                queryBuilder.setProjectionMap(episodesProjectionMap);
                queryBuilder.appendWhere(Constants.TAG_ID + "=" + uri.getLastPathSegment());
                break;
            case EPISODE_SHOW_ID:
                queryBuilder.setTables(Constants.TABLE_EPISODES);
                queryBuilder.setProjectionMap(episodesProjectionMap);
                queryBuilder.appendWhere(Constants.TAG_SHOW_ID + "=" + uri.getLastPathSegment());
                break;
            case CAST:
                queryBuilder.setTables(Constants.TABLE_CAST);
                queryBuilder.setProjectionMap(castProjectionMap);
                break;
            case CAST_ID:
                queryBuilder.setTables(Constants.TABLE_CAST);
                queryBuilder.setProjectionMap(castProjectionMap);
                queryBuilder.appendWhere(Constants.TAG_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder))
            orderBy = Constants.TAG_ID;
        else
            orderBy = sortOrder;

        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, orderBy);
        // Register to watch a content URI for changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // permissions to be writable
        database = dbHelper.getWritableDatabase();
        long id;
        switch (uriMatcher.match(uri)) {
            case SHOWS:
                id = database.insert(Constants.TABLE_SHOWS, "", values);

                //if record is added successfully
                if (id > 0) {
                    Uri newUri = ContentUris.withAppendedId(Constants.SHOW_CONTENT_URI, id);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    return newUri;
                }
                break;
            case EPISODES:
                id = database.insert(Constants.TABLE_EPISODES, "", values);

                //if record is added successfully
                if (id > 0) {
                    Uri newUri = ContentUris.withAppendedId(Constants.EPISODES_CONTENT_URI, id);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    return newUri;
                }
                break;
            case CAST:
                id = database.insert(Constants.TABLE_CAST, "", values);

                //if record is added successfully
                if (id > 0) {
                    Uri newUri = ContentUris.withAppendedId(Constants.CAST_CONTENT_URI, id);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    return newUri;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        throw new SQLException("Fail to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // permissions to be writable
        database = dbHelper.getWritableDatabase();
        int count;
        String id;

        switch (uriMatcher.match(uri)) {
            case SHOWS:
                // delete all the records of the table
                count = database.delete(Constants.TABLE_SHOWS, selection, selectionArgs);
                break;
            case SHOW_ID:
                //get the id
                id = uri.getLastPathSegment();
                count = database.delete(Constants.TABLE_SHOWS, Constants.TAG_ID + "=" + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case EPISODES:
                // delete all the records of the table
                count = database.delete(Constants.TABLE_EPISODES, selection, selectionArgs);
                break;
            case EPISODE_ID:
                //get the id
                id = uri.getLastPathSegment();
                count = database.delete(Constants.TABLE_EPISODES, Constants.TAG_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case EPISODE_SHOW_ID:
                //get the id
                id = uri.getLastPathSegment();
                count = database.delete(Constants.TABLE_EPISODES, Constants.TAG_SHOW_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case CAST:
                // delete all the records of the table
                count = database.delete(Constants.TABLE_CAST, selection, selectionArgs);
                break;
            case CAST_ID:
                //get the id
                id = uri.getLastPathSegment();
                count = database.delete(Constants.TABLE_CAST, Constants.TAG_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // permissions to be writable
        database = dbHelper.getWritableDatabase();
        int count;
        String id;
        switch (uriMatcher.match(uri)) {
            case SHOWS:
                // delete all the records of the table
                count = database.update(Constants.TABLE_SHOWS, values, selection, selectionArgs);
                break;
            case SHOW_ID:
                //get the id
                id = uri.getLastPathSegment();
                count = database.update(Constants.TABLE_SHOWS, values, Constants.TAG_ID + "=" + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case EPISODES:
                // delete all the records of the table
                count = database.update(Constants.TABLE_EPISODES, values, selection, selectionArgs);
                break;
            case EPISODE_ID:
                //get the id
                id = uri.getLastPathSegment();
                count = database.update(Constants.TABLE_EPISODES, values, Constants.TAG_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case EPISODE_SHOW_ID:
                //get the id
                id = uri.getLastPathSegment();
                count = database.update(Constants.TABLE_EPISODES, values, Constants.TAG_SHOW_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case CAST:
                // delete all the records of the table
                count = database.update(Constants.TABLE_CAST, values, selection, selectionArgs);
                break;
            case CAST_ID:
                //get the id
                id = uri.getLastPathSegment();
                count = database.update(Constants.TABLE_CAST, values, Constants.TAG_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    //Helper static class
    public static class Helper {
        private static final String TAG = Helper.class.getName();

        public static boolean addShow(ContentResolver contentResolver, Show show) {
            ContentValues values = new ContentValues();
            values.put(Constants.TAG_ID, show.getId());
            values.put(Constants.TAG_URL, show.getUrl());
            values.put(Constants.TAG_NAME, show.getName());
            values.put(Constants.TAG_TYPE, show.getType());
            values.put(Constants.TAG_STATUS, show.getStatus());
            values.put(Constants.TAG_RUNTIME, show.getRuntime());
            values.put(Constants.TAG_PREMIERED, show.getPremiered());
            values.put(Constants.TAG_RATING + Constants.TAG_AVERAGE, show.getRatingAverage());
            values.put(Constants.TAG_NETWORK + Constants.TAG_ID, show.getNetwork().getId());
            values.put(Constants.TAG_NETWORK + Constants.TAG_NAME, show.getNetwork().getName());
            values.put(Constants.TAG_NETWORK + Constants.TAG_COUNTRY + Constants.TAG_NAME, show.getNetwork().getCountry().getName());
            values.put(Constants.TAG_NETWORK + Constants.TAG_COUNTRY + Constants.TAG_CODE, show.getNetwork().getCountry().getCode());
            values.put(Constants.TAG_NETWORK + Constants.TAG_COUNTRY + Constants.TAG_TIMEZONE, show.getNetwork().getCountry().getTimezone());
            values.put(Constants.TAG_IMAGE + Constants.TAG_MEDIUM, show.getImage().getMedium());
            values.put(Constants.TAG_IMAGE + Constants.TAG_ORIGINAL, show.getImage().getOriginal());
            values.put(Constants.TAG_SUMMARY, show.getSummary());
            values.put(Constants.TAG_LINKS + Constants.TAG_SELF + Constants.TAG_HREF, show.getLinks().getSelf());
            values.put(Constants.TAG_LINKS + Constants.TAG_EPISODES + Constants.TAG_HREF, show.getLinks().getEpisodes());
            values.put(Constants.TAG_LINKS + Constants.TAG_CAST + Constants.TAG_HREF, show.getLinks().getCast());
            values.put(Constants.TAG_LINKS + Constants.TAG_PREVIOUS_EPISODE + Constants.TAG_HREF, show.getLinks().getPreviousepisode());
            values.put(Constants.TAG_LINKS + Constants.TAG_NEXT_EPISODE + Constants.TAG_HREF, show.getLinks().getNextepisode());

            Uri uri = contentResolver.insert(Constants.SHOW_CONTENT_URI, values);
            Log.d(TAG, "Inserting new row to show table... Returned uri is " + uri);
            if (uri == null)
                return false;

            for (Episode episode : show.getEpisodes()) {
                if (!addEpisode(contentResolver, episode, show.getId())) {
                    return false;
                }
            }

            return true;
        }

        public static boolean deleteShow(ContentResolver contentResolver, int id) {
            Uri contentUriWithId = Uri.parse(Constants.SHOW_URL + "/" + id);
            int rowNum = contentResolver.delete(contentUriWithId, null, null);
            Log.d(TAG, "Removing a row from show table... Number of rows removed is " + rowNum);

            return (rowNum == 1 && deleteEpisodes(contentResolver, id));

        }

        public static Show getShow (ContentResolver contentResolver, int id){
            Uri contentUriWithId = Uri.parse(Constants.SHOW_URL + "/" + id);
            Cursor c = contentResolver.query(contentUriWithId, null, null, null, null);

            if (!c.moveToFirst()) {
                Log.w(TAG, "Show was not found in DB!");
                c.close();
                return null;
            } else {
                Show show = new Show();
                show.setId(c.getInt(c.getColumnIndex(Constants.TAG_ID)));
                show.setUrl(c.getString(c.getColumnIndex(Constants.TAG_URL)));
                show.setName(c.getString(c.getColumnIndex(Constants.TAG_NAME)));
                show.setType(c.getString(c.getColumnIndex(Constants.TAG_TYPE)));
                show.setStatus(c.getString(c.getColumnIndex(Constants.TAG_STATUS)));
                show.setRuntime(c.getInt(c.getColumnIndex(Constants.TAG_RUNTIME)));
                show.setPremiered(c.getString(c.getColumnIndex(Constants.TAG_PREMIERED)));
                show.setRatingAverage(c.getDouble(c.getColumnIndex(Constants.TAG_RATING + Constants.TAG_AVERAGE)));
                Country country = new Country();
                country.setName(c.getString(c.getColumnIndex(Constants.TAG_NETWORK + Constants.TAG_COUNTRY + Constants.TAG_NAME)));
                country.setCode(c.getString(c.getColumnIndex(Constants.TAG_NETWORK + Constants.TAG_COUNTRY + Constants.TAG_CODE)));
                country.setTimezone(c.getString(c.getColumnIndex(Constants.TAG_NETWORK + Constants.TAG_COUNTRY + Constants.TAG_TIMEZONE)));
                Network network = new Network();
                network.setId(c.getInt(c.getColumnIndex(Constants.TAG_NETWORK + Constants.TAG_ID)));
                network.setName(c.getString(c.getColumnIndex(Constants.TAG_NETWORK + Constants.TAG_NAME)));
                network.setCountry(country);
                show.setNetwork(network);
                Image image = new Image();
                image.setMedium(c.getString(c.getColumnIndex(Constants.TAG_IMAGE + Constants.TAG_MEDIUM)));
                image.setOriginal(c.getString(c.getColumnIndex(Constants.TAG_IMAGE + Constants.TAG_ORIGINAL)));
                show.setImage(image);
                show.setSummary(c.getString(c.getColumnIndex(Constants.TAG_SUMMARY)));
                Links links = new Links();
                links.setSelf(c.getString(c.getColumnIndex(Constants.TAG_LINKS + Constants.TAG_SELF + Constants.TAG_HREF)));
                links.setEpisodes(c.getString(c.getColumnIndex(Constants.TAG_LINKS + Constants.TAG_EPISODES + Constants.TAG_HREF)));
                links.setCast(c.getString(c.getColumnIndex(Constants.TAG_LINKS + Constants.TAG_CAST + Constants.TAG_HREF)));
                links.setPreviousepisode(c.getString(c.getColumnIndex(Constants.TAG_LINKS + Constants.TAG_PREVIOUS_EPISODE + Constants.TAG_HREF)));
                links.setNextepisode(c.getString(c.getColumnIndex(Constants.TAG_LINKS + Constants.TAG_NEXT_EPISODE + Constants.TAG_HREF)));
                show.setLinks(links);
                show.setEpisodes(getEpisodes(contentResolver, show.getId()));
                c.close();
                return show;
            }
        }

        public static ArrayList<Show> getShows(ContentResolver contentResolver) {
            Cursor c = contentResolver.query(Constants.SHOW_CONTENT_URI, null, null, null, Constants.TAG_NAME);
            ArrayList<Show> suggestedShows = new ArrayList<>();

            if (!c.moveToFirst()) {
                Log.w(TAG, "No content in DB yet!");
                c.close();
                return null;
            } else {
                do {
                    Show show = new Show();
                    show.setId(c.getInt(c.getColumnIndex(Constants.TAG_ID)));
                    show.setUrl(c.getString(c.getColumnIndex(Constants.TAG_URL)));
                    show.setName(c.getString(c.getColumnIndex(Constants.TAG_NAME)));
                    show.setType(c.getString(c.getColumnIndex(Constants.TAG_TYPE)));
                    show.setStatus(c.getString(c.getColumnIndex(Constants.TAG_STATUS)));
                    show.setRuntime(c.getInt(c.getColumnIndex(Constants.TAG_RUNTIME)));
                    show.setPremiered(c.getString(c.getColumnIndex(Constants.TAG_PREMIERED)));
                    show.setRatingAverage(c.getDouble(c.getColumnIndex(Constants.TAG_RATING + Constants.TAG_AVERAGE)));
                    Country country = new Country();
                    country.setName(c.getString(c.getColumnIndex(Constants.TAG_NETWORK + Constants.TAG_COUNTRY + Constants.TAG_NAME)));
                    country.setCode(c.getString(c.getColumnIndex(Constants.TAG_NETWORK + Constants.TAG_COUNTRY + Constants.TAG_CODE)));
                    country.setTimezone(c.getString(c.getColumnIndex(Constants.TAG_NETWORK + Constants.TAG_COUNTRY + Constants.TAG_TIMEZONE)));
                    Network network = new Network();
                    network.setId(c.getInt(c.getColumnIndex(Constants.TAG_NETWORK + Constants.TAG_ID)));
                    network.setName(c.getString(c.getColumnIndex(Constants.TAG_NETWORK + Constants.TAG_NAME)));
                    network.setCountry(country);
                    show.setNetwork(network);
                    Image image = new Image();
                    image.setMedium(c.getString(c.getColumnIndex(Constants.TAG_IMAGE + Constants.TAG_MEDIUM)));
                    image.setOriginal(c.getString(c.getColumnIndex(Constants.TAG_IMAGE + Constants.TAG_ORIGINAL)));
                    show.setImage(image);
                    show.setSummary(c.getString(c.getColumnIndex(Constants.TAG_SUMMARY)));
                    Links links = new Links();
                    links.setSelf(c.getString(c.getColumnIndex(Constants.TAG_LINKS + Constants.TAG_SELF + Constants.TAG_HREF)));
                    links.setEpisodes(c.getString(c.getColumnIndex(Constants.TAG_LINKS + Constants.TAG_EPISODES + Constants.TAG_HREF)));
                    links.setCast(c.getString(c.getColumnIndex(Constants.TAG_LINKS + Constants.TAG_CAST + Constants.TAG_HREF)));
                    links.setPreviousepisode(c.getString(c.getColumnIndex(Constants.TAG_LINKS + Constants.TAG_PREVIOUS_EPISODE + Constants.TAG_HREF)));
                    links.setNextepisode(c.getString(c.getColumnIndex(Constants.TAG_LINKS + Constants.TAG_NEXT_EPISODE + Constants.TAG_HREF)));
                    show.setLinks(links);
                    show.setEpisodes(getEpisodes(contentResolver, show.getId()));

                    suggestedShows.add(show);
                } while (c.moveToNext());
                c.close();
                return suggestedShows;
            }
        }

        public static boolean addEpisode(ContentResolver contentResolver, Episode episode, int showId) {
            ContentValues values = new ContentValues();
            values.put(Constants.TAG_ID, episode.getId());
            values.put(Constants.TAG_URL, episode.getUrl());
            values.put(Constants.TAG_NAME, episode.getName());
            values.put(Constants.TAG_SEASON, episode.getSeason());
            values.put(Constants.TAG_NUMBER, episode.getNumber());
            values.put(Constants.TAG_AIRDATE, episode.getAirdate());
            values.put(Constants.TAG_AIRTIME, episode.getAirtime());
            values.put(Constants.TAG_AIRSTAMP, episode.getAirstamp());
            values.put(Constants.TAG_RUNTIME, episode.getRuntime());
            values.put(Constants.TAG_IMAGE + Constants.TAG_MEDIUM, episode.getImage().getMedium());
            values.put(Constants.TAG_IMAGE + Constants.TAG_ORIGINAL, episode.getImage().getOriginal());
            values.put(Constants.TAG_SUMMARY, episode.getSummary());
            values.put(Constants.TAG_WATCHED, episode.isWatched() ? 1 : 0);
            values.put(Constants.TAG_SHOW_ID, showId);

            Uri uri = contentResolver.insert(Constants.EPISODES_CONTENT_URI, values);
            Log.d(TAG, "Inserting new row to episodes table... Returned uri is " + uri);
            return (uri != null);
        }

        private static boolean deleteEpisodes(ContentResolver contentResolver, int id) {
            Uri contentUriWithId = Uri.parse(Constants.EPISODES_SHOW_ID_URL + "/" + id);
            int rowNum = contentResolver.delete(contentUriWithId, null, null);
            Log.d(TAG, "Removing a rows from the episode table... Number of rows removed is " + rowNum);
            return (rowNum > 0);
        }

        private static ArrayList<Episode> getEpisodes(ContentResolver contentResolver, int id) {
            Uri contentUriWithId = Uri.parse(Constants.EPISODES_SHOW_ID_URL + "/" + id);
            Cursor c = contentResolver.query(contentUriWithId, null, null, null, "");
            ArrayList<Episode> episodes = new ArrayList<>();

            if (!c.moveToFirst()) {
                Log.w(TAG, "No content in DB yet!");
                c.close();
                return null;
            } else {
                do {
                    Episode episode = new Episode();
                    episode.setId(c.getInt(c.getColumnIndex(Constants.TAG_ID)));
                    episode.setUrl(c.getString(c.getColumnIndex(Constants.TAG_URL)));
                    episode.setName(c.getString(c.getColumnIndex(Constants.TAG_NAME)));
                    episode.setSeason(c.getInt(c.getColumnIndex(Constants.TAG_SEASON)));
                    episode.setNumber(c.getInt(c.getColumnIndex(Constants.TAG_NUMBER)));
                    episode.setAirdate(c.getString(c.getColumnIndex(Constants.TAG_AIRDATE)));
                    episode.setAirtime(c.getString(c.getColumnIndex(Constants.TAG_AIRTIME)));
                    episode.setAirstamp(c.getString(c.getColumnIndex(Constants.TAG_AIRSTAMP)));
                    episode.setRuntime(c.getInt(c.getColumnIndex(Constants.TAG_RUNTIME)));
                    Image image = new Image();
                    image.setMedium(c.getString(c.getColumnIndex(Constants.TAG_IMAGE + Constants.TAG_MEDIUM)));
                    image.setOriginal(c.getString(c.getColumnIndex(Constants.TAG_IMAGE + Constants.TAG_ORIGINAL)));
                    episode.setImage(image);
                    episode.setSummary(c.getString(c.getColumnIndex(Constants.TAG_SUMMARY)));
                    episode.setWatched(c.getInt(c.getColumnIndex(Constants.TAG_WATCHED)) == 1);
                    episode.setShowId(id);

                    episodes.add(episode);
                } while (c.moveToNext());
                c.close();
                return episodes;
            }
        }

        public static boolean updateEpisode(ContentResolver contentResolver, Episode episode) {
            ContentValues values = new ContentValues();
            values.put(Constants.TAG_ID, episode.getId());
            values.put(Constants.TAG_URL, episode.getUrl());
            values.put(Constants.TAG_NAME, episode.getName());
            values.put(Constants.TAG_SEASON, episode.getSeason());
            values.put(Constants.TAG_NUMBER, episode.getNumber());
            values.put(Constants.TAG_AIRDATE, episode.getAirdate());
            values.put(Constants.TAG_AIRTIME, episode.getAirtime());
            values.put(Constants.TAG_AIRSTAMP, episode.getAirstamp());
            values.put(Constants.TAG_RUNTIME, episode.getRuntime());
            values.put(Constants.TAG_IMAGE + Constants.TAG_MEDIUM, episode.getImage().getMedium());
            values.put(Constants.TAG_IMAGE + Constants.TAG_ORIGINAL, episode.getImage().getOriginal());
            values.put(Constants.TAG_SUMMARY, episode.getSummary());
            values.put(Constants.TAG_WATCHED, episode.isWatched() ? 1 : 0);
            values.put(Constants.TAG_SHOW_ID, episode.getShowId());

            Uri contentUriWithId = Uri.parse(Constants.EPISODES_URL + "/" + episode.getId());
            int rowsNum = contentResolver.update(contentUriWithId, values, null, null);
            Log.d(TAG, "Updating row in episodes table... Number of rows updated is " + rowsNum);
            return (rowsNum == 1);
        }
    }
}

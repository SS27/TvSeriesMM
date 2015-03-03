package com.spstanchev.tvseries.tasks;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.spstanchev.tvseries.common.Constants;
import com.spstanchev.tvseries.common.HttpHandler;
import com.spstanchev.tvseries.common.JsonUtils;
import com.spstanchev.tvseries.models.Episode;
import com.spstanchev.tvseries.models.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Stefan on 2/21/2015.
 */
public class AsyncDownloadEpisode extends AsyncTask<String, Void, ArrayList<Episode>> {
    private static final String TAG = AsyncDownloadEpisode.class.getSimpleName();
    private AsyncEpisodeJsonResponse asyncEpisodeJsonResponse;
    public AsyncDownloadEpisode(AsyncEpisodeJsonResponse asyncEpisodeJsonResponse) {
        this.asyncEpisodeJsonResponse = asyncEpisodeJsonResponse;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "Starting now...");
    }
    @Override
    protected ArrayList<Episode> doInBackground(String... params) {
        HttpHandler httpHandler = new HttpHandler();
        String response = httpHandler.makeHttpCall(params[0], HttpHandler.GET, null);
        Log.d(TAG, String.format("Episodes json response is: %s\n", response));
        return getEpisodes(response);
    }
    @Override
    protected void onPostExecute(ArrayList<Episode> episodes) {
        asyncEpisodeJsonResponse.handleEpisodesJsonResponse(episodes);
    }
    private ArrayList<Episode> getEpisodes(String response) {
        ArrayList<Episode> episodes = new ArrayList<>();
//if we are downloading json array
        try {
            JSONArray episodesJsonArray = new JSONArray(response);
            for (int i = 0; i < episodesJsonArray.length(); i++) {
                Episode episode = getEpisode(episodesJsonArray.getJSONObject(i));
                if (episode != null)
                    episodes.add(episode);
            }
            return episodes;
        } catch (JSONException e) {
            Log.v(TAG, "Caught JsonException when trying to get JSONArray from " + response);
        }
//if we are downloading json object
        try {
            JSONObject episodeJsonObject = new JSONObject(response);
            Episode episode = getEpisode(episodeJsonObject);
            if (episode != null) {
                episodes.add(episode);
                return episodes;
            }
        } catch (JSONException e) {
            Log.v(TAG, "Caught JsonException when trying to get JSONObject from " + response);
        }
        return null;
    }
    private Episode getEpisode(JSONObject jsonObject) {
        Episode episode = new Episode();
        episode.setId(JsonUtils.getJsonInteger(jsonObject, Constants.TAG_ID));
        episode.setUrl(JsonUtils.getJsonString(jsonObject, Constants.TAG_URL));
        episode.setName(JsonUtils.getJsonString(jsonObject, Constants.TAG_NAME));
        episode.setSeason(JsonUtils.getJsonInteger(jsonObject, Constants.TAG_SEASON));
        episode.setNumber(JsonUtils.getJsonInteger(jsonObject, Constants.TAG_NUMBER));
        episode.setAirdate(JsonUtils.getJsonString(jsonObject, Constants.TAG_AIRDATE));
        episode.setAirtime(JsonUtils.getJsonString(jsonObject, Constants.TAG_AIRTIME));
        episode.setAirstamp(JsonUtils.getJsonString(jsonObject, Constants.TAG_AIRSTAMP));
        episode.setRuntime(JsonUtils.getJsonInteger(jsonObject, Constants.TAG_RUNTIME));
        episode.setImage(getImage(jsonObject));
        episode.setSummary(Html.fromHtml(JsonUtils.getJsonString(jsonObject, Constants.TAG_SUMMARY)).toString());
        return episode;
    }
    private Image getImage(JSONObject showJsonObject) {
        JSONObject imageJsonObject = JsonUtils.getJSONObjectSafely(showJsonObject, Constants.TAG_IMAGE);
        Image image = new Image();
        image.setMedium(JsonUtils.getJsonString(imageJsonObject, Constants.TAG_MEDIUM));
        image.setOriginal(JsonUtils.getJsonString(imageJsonObject, Constants.TAG_ORIGINAL));
        return image;
    }
}

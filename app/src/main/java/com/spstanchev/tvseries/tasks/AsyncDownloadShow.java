package com.spstanchev.tvseries.tasks;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.spstanchev.tvseries.common.Constants;
import com.spstanchev.tvseries.common.HttpHandler;
import com.spstanchev.tvseries.common.JsonUtils;
import com.spstanchev.tvseries.models.Country;
import com.spstanchev.tvseries.models.Episode;
import com.spstanchev.tvseries.models.Image;
import com.spstanchev.tvseries.models.Links;
import com.spstanchev.tvseries.models.Network;
import com.spstanchev.tvseries.models.Show;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AsyncDownloadShow extends AsyncTask<String, Void, ArrayList<Show>> {

    private static final String TAG = AsyncDownloadShow.class.getSimpleName();
    private AsyncJsonResponse asyncJsonResponse;
    private boolean isQuery = false;

    public AsyncDownloadShow(AsyncJsonResponse asyncJsonResponse) {
        this.asyncJsonResponse = asyncJsonResponse;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "Starting now...");
    }

    @Override
    protected ArrayList<Show> doInBackground(String... params) {
        isQuery = params[0].startsWith(Constants.JSON_QUERY_SHOWS_URL);
        HttpHandler httpHandler = new HttpHandler();
        String response = httpHandler.makeHttpCall(params[0], HttpHandler.GET, null);
        Log.d(TAG, String.format("Show json response is: %s\n", response));
        ArrayList<Show> shows = getShowsFromJson(response);
        for (Show show : shows) {
            response = httpHandler.makeHttpCall(show.getLinks().getEpisodes(), HttpHandler.GET, null);
            Log.d(TAG, String.format("Episodes json response is: %s\n", response));
            ArrayList<Episode> episodes = getEpisodes(response);
            show.setEpisodes(episodes);
        }

        return shows;
    }

    @Override
    protected void onPostExecute(ArrayList<Show> shows) {
        if (isQuery)
            asyncJsonResponse.handleQueryShowsJsonResponse(shows);
        else
            asyncJsonResponse.handleShowJsonResponse(shows.get(0));
    }

    private ArrayList<Show> getShowsFromJson(String result) {
        ArrayList<Show> shows = new ArrayList<>();

        //if we are downloading json array
        try {
            JSONArray showsJsonArray = new JSONArray(result);
            for (int i = 0; i < showsJsonArray.length(); i++) {
                Show show = getShow(showsJsonArray.getJSONObject(i).getJSONObject(Constants.TAG_SHOW));
                if (show != null)
                    shows.add(show);
            }
            return shows;
        } catch (JSONException e) {
            Log.v(TAG, "Caught JsonException when trying to get JSONArray from " + result);
        }

        //if we are downloading json object
        try {
            JSONObject showJsonObject = new JSONObject(result);
            Show show = getShow(showJsonObject);
            if (show != null) {
                shows.add(show);
                return shows;
            }
        } catch (JSONException e) {
            Log.v(TAG, "Caught JsonException when trying to get JSONObject from " + result);
        }

        return null;
    }

    private Show getShow(JSONObject showJsonObject) {
        Show show = new Show();
        show.setId(JsonUtils.getJsonInteger(showJsonObject, Constants.TAG_ID));
        show.setUrl(JsonUtils.getJsonString(showJsonObject, Constants.TAG_URL));
        show.setName(JsonUtils.getJsonString(showJsonObject, Constants.TAG_NAME));
        show.setType(JsonUtils.getJsonString(showJsonObject, Constants.TAG_TYPE));
        show.setStatus(JsonUtils.getJsonString(showJsonObject, Constants.TAG_STATUS));
        show.setRuntime(JsonUtils.getJsonInteger(showJsonObject, Constants.TAG_RUNTIME));
        show.setPremiered(JsonUtils.getJsonString(showJsonObject, Constants.TAG_PREMIERED));
        show.setRatingAverage(getAverageRating(showJsonObject));
        show.setNetwork(getNetwork(showJsonObject));
        show.setImage(getImage(showJsonObject));
        show.setSummary(Html.fromHtml(JsonUtils.getJsonString(showJsonObject, Constants.TAG_SUMMARY)).toString());
        show.setLinks(getLinks(showJsonObject));
        return show;
    }

    private double getAverageRating(JSONObject showJsonObject) {
        JSONObject ratingJsonObject = JsonUtils.getJSONObjectSafely(showJsonObject, Constants.TAG_RATING);
        return JsonUtils.getJsonDouble(ratingJsonObject, Constants.TAG_AVERAGE);
    }

    private Network getNetwork(JSONObject showJsonObject) {
        JSONObject networkJsonObject = JsonUtils.getJSONObjectSafely(showJsonObject, Constants.TAG_NETWORK);
        JSONObject countryNetworkJsonObject = JsonUtils.getJSONObjectSafely(networkJsonObject, Constants.TAG_COUNTRY);
        Network network = new Network();
        network.setId(JsonUtils.getJsonInteger(networkJsonObject, Constants.TAG_ID));
        network.setName(JsonUtils.getJsonString(networkJsonObject, Constants.TAG_NAME));
        Country country = new Country();
        country.setName(JsonUtils.getJsonString(countryNetworkJsonObject, Constants.TAG_NAME));
        country.setCode(JsonUtils.getJsonString(countryNetworkJsonObject, Constants.TAG_CODE));
        country.setTimezone(JsonUtils.getJsonString(countryNetworkJsonObject, Constants.TAG_TIMEZONE));
        network.setCountry(country);
        return network;
    }

    private Image getImage(JSONObject showJsonObject) {
        JSONObject imageJsonObject = JsonUtils.getJSONObjectSafely(showJsonObject, Constants.TAG_IMAGE);
        Image image = new Image();
        image.setMedium(JsonUtils.getJsonString(imageJsonObject, Constants.TAG_MEDIUM));
        image.setOriginal(JsonUtils.getJsonString(imageJsonObject, Constants.TAG_ORIGINAL));
        return image;
    }


    private Links getLinks(JSONObject showJsonObject) {
        Links links = new Links();
        JSONObject linksJsonObject = JsonUtils.getJSONObjectSafely(showJsonObject, Constants.TAG_LINKS);
        JSONObject selfJsonObject = JsonUtils.getJSONObjectSafely(linksJsonObject, Constants.TAG_SELF);
        JSONObject episodesJsonObject = JsonUtils.getJSONObjectSafely(linksJsonObject, Constants.TAG_EPISODES);
        JSONObject castJsonObject = JsonUtils.getJSONObjectSafely(linksJsonObject, Constants.TAG_CAST);
        JSONObject previousEpisodeJsonObject = JsonUtils.getJSONObjectSafely(linksJsonObject, Constants.TAG_PREVIOUS_EPISODE);
        JSONObject nextEpisodeJsonObject = JsonUtils.getJSONObjectSafely(linksJsonObject, Constants.TAG_NEXT_EPISODE);
        links.setSelf(JsonUtils.getJsonString(selfJsonObject, Constants.TAG_HREF));
        links.setEpisodes(JsonUtils.getJsonString(episodesJsonObject, Constants.TAG_HREF));
        links.setCast(JsonUtils.getJsonString(castJsonObject, Constants.TAG_HREF));
        links.setPreviousepisode(JsonUtils.getJsonString(previousEpisodeJsonObject, Constants.TAG_HREF));
        links.setNextepisode(JsonUtils.getJsonString(nextEpisodeJsonObject, Constants.TAG_HREF));
        return links;
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

}





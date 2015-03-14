package com.spstanchev.tvseries.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.util.Log;

import com.spstanchev.tvseries.R;
import com.spstanchev.tvseries.activities.EpisodeListActivity;
import com.spstanchev.tvseries.activities.MainActivity;
import com.spstanchev.tvseries.common.Constants;
import com.spstanchev.tvseries.common.Utils;
import com.spstanchev.tvseries.models.AiringEpisode;
import com.spstanchev.tvseries.models.Episode;
import com.spstanchev.tvseries.models.Image;
import com.spstanchev.tvseries.models.Show;
import com.spstanchev.tvseries.providers.ShowProvider;
import com.spstanchev.tvseries.receivers.AlarmReceiver;
import com.spstanchev.tvseries.common.HttpHandler;
import com.spstanchev.tvseries.common.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Stefan on 2/25/2015.
 */
public class WebAndNotificationService extends IntentService {

    private static final String TAG = WebAndNotificationService.class.getName();
    private final static String GROUP_KEY_AIRING_EPISODES = "group_key_airing_episodes";
    // An ID used to post the grouped notification.
    public static final int NOTIFICATION_ID = 111111;

    public WebAndNotificationService() {
        super("WebAndNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //get current shows from DB
        ArrayList<Show> shows = ShowProvider.Helper.getShows(getContentResolver(), true);
        if (shows != null) {
            //check for airing episodes and display notification
            checkForAiringEpisodes(shows);

            // check for network connection
            if (Utils.isNetworkAvailable(this)) {
                // check for new episodes on the server
                checkForNewEpisodes(shows);
            }
        }

        // Release the wake lock provided by the BroadcastReceiver.
        AlarmReceiver.completeWakefulIntent(intent);
    }

    public void checkForAiringEpisodes(ArrayList<Show> shows) {
        ArrayList<AiringEpisode> episodesPlayingToday = new ArrayList<>();
        int showId = -1;
        Date currentDate = getCurrentDate();

        for (Show show : shows) {
            if ("Running".equals(show.getStatus()) && currentDate != null) {
                for (int i = show.getEpisodes().size() - 1; i >= 0; i--) {
                    Episode currentEpisode = show.getEpisodes().get(i);
                    Date episodeAirdate = Utils.getDateFromString(currentEpisode.getAirdate(), Utils.getAirdateFormat());
                    if (currentDate.equals(episodeAirdate)) {
                        AiringEpisode newAiringEpisode = new AiringEpisode();
                        newAiringEpisode.setShowName(show.getName());
                        newAiringEpisode.setNetworkName(show.getNetwork().getName());
                        newAiringEpisode.setEpisode(currentEpisode);
                        episodesPlayingToday.add(newAiringEpisode);
                        showId = show.getId();
                    } else if (currentDate.after(episodeAirdate)) {
                        break;
                    }
                }
            }
        }

        if (episodesPlayingToday.size() > 0)
            displaySummaryNotification(episodesPlayingToday, showId);

    }

    private Date getCurrentDate() {
        String currentDateAirdateFormat = Utils.getAirdateFormat().format(new Date());
        Date currentDate;
        try {
            currentDate = Utils.getAirdateFormat().parse(currentDateAirdateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return currentDate;
    }

    private void checkForNewEpisodes(ArrayList<Show> shows) {
        HttpHandler httpHandler = new HttpHandler();
        Date currentDate = getCurrentDate();

        for (Show show : shows) {
            ArrayList<Episode> jsonEpisodes = getEpisodes(httpHandler.makeHttpCall(show.getLinks().getEpisodes(), HttpHandler.GET, null));
            if (jsonEpisodes.size() > show.getEpisodes().size()) {
                //there are new episodes, add them to db
                for (int i = show.getEpisodes().size(); i < jsonEpisodes.size(); i++) {
                    ShowProvider.Helper.addEpisode(getContentResolver(), jsonEpisodes.get(i), show.getId());
                }
            } else if (jsonEpisodes.size() < show.getEpisodes().size()) {
                Log.e(TAG, "Something is wrong with the api or db episode information!");
                return;
            }

            if ("Running".equals(show.getStatus()) && currentDate != null) {
                for (int i = show.getEpisodes().size() - 1; i >= 0; i--) {
                    Episode currentEpisode = show.getEpisodes().get(i);
                    Date episodeAirdate = Utils.getDateFromString(currentEpisode.getAirdate(), Utils.getAirdateFormat());
                    if (currentDate.before(episodeAirdate)) {
                        //update upcoming episodes in database, because we are not sure that we have valid data
                        Episode episodeForUpdate = jsonEpisodes.get(i);
                        episodeForUpdate.setShowId(show.getId());
                        episodeForUpdate.setWatched(false);
                        ShowProvider.Helper.updateEpisode(getContentResolver(), episodeForUpdate);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private void displaySummaryNotification(ArrayList<AiringEpisode> episodes, int showId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        String contentTitleText = episodes.size() == 1 ? " new episode airing today" : " new episodes airing today";
        String contentTitle = episodes.size() + contentTitleText;
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(contentTitle);
        inboxStyle.setSummaryText("TvSeries");
        for (AiringEpisode episode : episodes) {
            Date episodeAirdate = Utils.getDateFromString(episode.getEpisode().getAirstamp(), Utils.getJsonAirstampFormat());
            String episodeAirtime = Utils.getAirtimeFormat().format(episodeAirdate);
            inboxStyle.addLine(episode.getShowName() + "  S" + episode.getEpisode().getSeason()
                    + "E" + episode.getEpisode().getNumber() + " on " + episode.getNetworkName()
                    + " at " + episodeAirtime);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.tv_shows);

        NotificationCompat.Builder summaryNotificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(contentTitle)
                .setSmallIcon(R.drawable.tv_series_thin)
                .setLargeIcon(largeIcon)
                .setStyle(inboxStyle)
                .setGroup(GROUP_KEY_AIRING_EPISODES)
                .setGroupSummary(true)
                .setAutoCancel(true);

        //define the notification action
        Intent resultIntent;
        if (episodes.size() == 1) {
            resultIntent = new Intent(this, EpisodeListActivity.class);
            resultIntent.putExtra("com.spstanchev.tvseries" + Constants.TAG_SHOW_ID, showId);
        } else {
            resultIntent = new Intent(this, MainActivity.class);
        }

        //use stack builder object
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        summaryNotificationBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(NOTIFICATION_ID, summaryNotificationBuilder.build());
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

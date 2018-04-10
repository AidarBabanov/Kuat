package kz.kuat.kuat;

import android.util.Log;

import com.macroyau.thingspeakandroid.ThingSpeakChannel;
import com.macroyau.thingspeakandroid.model.ChannelFeed;

public class ThingSpeakConnection extends ThingSpeakChannel {

    private final static long CHANNEL_ID = 408017;
    private final static String API_KEY = "WFHDF0CAMH3E7ZLH";

    private static ThingSpeakConnection instance;

    private ChannelFeed channelFeed;

    public ThingSpeakConnection(long channelId) {
        super(channelId);
        this.setChannelFeedUpdateListener(new ThingSpeakDataListener());
    }

    public ThingSpeakConnection(long channelId, String APIKey) {
        super(channelId, APIKey);
        this.setChannelFeedUpdateListener(new ThingSpeakDataListener());
    }

    //Don't forget to delete this
    public ThingSpeakConnection() {
        super(CHANNEL_ID, API_KEY);
        this.setChannelFeedUpdateListener(new ThingSpeakDataListener());
    }

    public static ThingSpeakConnection getInstance() {
        if (instance == null) instance = new ThingSpeakConnection();
        return instance;
    }

    public void setInstance(ThingSpeakConnection instance) {
        ThingSpeakConnection.instance = instance;
    }

    public ChannelFeed getChannelFeed() {
        return channelFeed;
    }

    public void setChannelFeed(ChannelFeed channelFeed) {
        this.channelFeed = channelFeed;
    }

    class ThingSpeakDataListener implements ChannelFeedUpdateListener {
        @Override
        public void onChannelFeedUpdated(long channelId, String channelName, ChannelFeed channelFeed) {
            setChannelFeed(channelFeed);
        }
    }
}

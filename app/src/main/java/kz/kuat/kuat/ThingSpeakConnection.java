package kz.kuat.kuat;

import com.macroyau.thingspeakandroid.ThingSpeakChannel;


public class ThingSpeakConnection extends ThingSpeakChannel {

    private final static long CHANNEL_ID = 408017;
    private final static String API_KEY = "WFHDF0CAMH3E7ZLH";

    private static ThingSpeakConnection instance;

    public ThingSpeakConnection(long channelId) {
        super(channelId);
    }

    public ThingSpeakConnection(long channelId, String APIKey) {
        super(channelId, APIKey);
    }

    //Don't forget to delete this
    public ThingSpeakConnection() {
        super(CHANNEL_ID, API_KEY);
    }

    public static ThingSpeakConnection getInstance() {
        if (instance == null) instance = new ThingSpeakConnection();
        return instance;
    }

    public void setInstance(ThingSpeakConnection instance) {
        ThingSpeakConnection.instance = instance;
    }




}

package kz.kuat.kuat;

import android.util.Log;

import com.macroyau.thingspeakandroid.ThingSpeakChannel;
import com.macroyau.thingspeakandroid.model.ChannelFeed;
import com.macroyau.thingspeakandroid.model.Feed;
import com.macroyau.thingspeakandroid.model.Channel;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;


public class ThingSpeakConnection extends ThingSpeakChannel implements ThingSpeakChannel.ChannelFeedUpdateListener {

    private final static long CHANNEL_ID =
            //476628;
            408017;
    private final static String API_KEY =
            //"KLHOTY7EJONSYFO6";
            "WFHDF0CAMH3E7ZLH";

    private SortedSet<Feed> totalFeeds;
    private boolean isStopped = false;
    private boolean isRunning = false;
    private Channel channel;
    private DataUpdateListener dataUpdateListener;

    public ThingSpeakConnection(long channelId) {
        super(channelId);
        setChannelFeedUpdateListener(this);
        totalFeeds = new TreeSet<>(new ThingSpeakDateComparator());
    }

    public ThingSpeakConnection(long channelId, String APIKey) {
        super(channelId, APIKey);
        setChannelFeedUpdateListener(this);
        totalFeeds = new TreeSet<>(new ThingSpeakDateComparator());
    }

    //Don't forget to delete this
    public ThingSpeakConnection() {
        super(CHANNEL_ID, API_KEY);
        setChannelFeedUpdateListener(this);
        totalFeeds = new TreeSet<>(new ThingSpeakDateComparator());
    }

    @Override
    public void onChannelFeedUpdated(long channelId, String channelName, ChannelFeed channelFeed) {
        channel = channelFeed.getChannel();
        List<Feed> feeds = channelFeed.getFeeds();
        totalFeeds.addAll(feeds);
        if (dataUpdateListener != null) dataUpdateListener.onDataUpdated();
    }

    public void scheduledDataUpdate() {
        loadChannelFeed();
        Timer timer = new Timer();
        isRunning = true;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isStopped) {
                    cancel();
                }
                if (channel != null) setStartDate(channel.getUpdatedAt());
                loadChannelFeed();
            }
        }, 0, 20000);
    }

    public void timePeriodRequest(Date start, Date end) {
        setStartDate(start);
        setEndDate(end);
        loadChannelFeed();
    }

    public void stop() {
        isStopped = true;
        isRunning = false;
    }

    public SortedSet<Feed> getTotalFeeds() {
        return totalFeeds;
    }

    public DataUpdateListener getDataUpdateListener() {
        return dataUpdateListener;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setDataUpdateListener(DataUpdateListener dataUpdateListener) {
        this.dataUpdateListener = dataUpdateListener;
    }

    public boolean isRunning() {
        return isRunning;
    }

    class ThingSpeakDateComparator implements Comparator<Feed> {
        @Override
        public int compare(Feed o1, Feed o2) {
            return o1.getCreatedAt().compareTo(o2.getCreatedAt());
        }
    }

    interface DataUpdateListener {
        void onDataUpdated();
    }
}

package kz.kuat.kuat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;

import com.macroyau.thingspeakandroid.ThingSpeakChannel;
import com.macroyau.thingspeakandroid.model.ChannelFeed;
import com.macroyau.thingspeakandroid.model.Feed;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

public class MetersListActivity extends AppCompatActivity implements RecyclerViewAdapter.RecyclerViewAdapterOnClickHandler {

    //Time intervals in milliseconds
    private static final long SECOND = 1000;
    private static final long MINUTE = SECOND * 60;
    private static final long HOUR = MINUTE * 60;
    private static final long DAY = HOUR * 24;
    private static final long MONTH = DAY * 30;
    private static final long YEAR = DAY * 365;

    private static final long DIFFERENCE_YEARS = 50 * YEAR;

    private ThingSpeakConnection thingSpeakConnection;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meters_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        thingSpeakConnection = new ThingSpeakConnection();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter(this, thingSpeakConnection);
        recyclerView.setAdapter(recyclerViewAdapter);

        thingSpeakConnection.setDataUpdateListener(recyclerViewAdapter);
        thingSpeakConnection.scheduledDataUpdate();

    }


    @Override
    public void onClick(int fieldNum) {
        TransferObjects transferObjects = TransferObjects.getInstance();
        transferObjects.setFieldNum(fieldNum);
        transferObjects.setThingSpeakConnection(thingSpeakConnection);
        Intent intentToStartNewActivity = new Intent(this, ChartsActivity.class);
        startActivity(intentToStartNewActivity);
    }
}

package kz.kuat.kuat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.macroyau.thingspeakandroid.ThingSpeakChannel;
import com.macroyau.thingspeakandroid.model.ChannelFeed;
import com.macroyau.thingspeakandroid.model.Feed;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ThingSpeakConnection.ChannelFeedUpdateListener {

    TextView speedValue;
    DecoView arcView;
    int series1Index;

    LineChart mChart;

    ConstraintLayout chartLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mChart = findViewById(R.id.lineChart);
        chartLayout = findViewById(R.id.chartLayout);

        chartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(chartLayout.getContext(), ChartsActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

//        Circle meter start
        speedValue = findViewById(R.id.speedValue);

        arcView = (DecoView) findViewById(R.id.dynamicArcView);

        arcView.configureAngles(270, 90);
        arcView.setRotation(270);

// Create background track
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(32f)
                .build());

//Create data series track
        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();

        series1Index = arcView.addSeries(seriesItem1);

        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(2000)
                .build());


        speedValue.postDelayed(new Runnable() {
            @Override
            public void run() {
                speedValue.setText("25");
            }
        }, 4000);
        arcView.addEvent(new DecoEvent.Builder(25).setIndex(series1Index).setDelay(4000).build());


        speedValue.postDelayed(new Runnable() {
            @Override
            public void run() {
                speedValue.setText("100");
            }
        }, 8000);
        arcView.addEvent(new DecoEvent.Builder(100).setIndex(series1Index).setDelay(8000).build());

        speedValue.postDelayed(new Runnable() {
            @Override
            public void run() {
                speedValue.setText("10");
            }
        }, 12000);
        arcView.addEvent(new DecoEvent.Builder(10).setIndex(series1Index).setDelay(12000).build());
//        Circle meter end
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Loading data from ThingSpeak
        ThingSpeakConnection thingSpeakConnection = ThingSpeakConnection.getInstance();
        thingSpeakConnection.setChannelFeedUpdateListener(this);
        thingSpeakConnection.loadChannelFeed();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onChannelFeedUpdated(long channelId, String channelName, ChannelFeed channelFeed) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (Feed feed : channelFeed.getFeeds()) {
            Float xValue = Long.valueOf(feed.getCreatedAt().getTime()).floatValue();
            Float yValue = Float.parseFloat(feed.getField2());
            entries.add(new Entry(xValue, yValue));
        }
        updateDataSet(entries);
    }

    public void updateDataSet(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "field2");
        LineData data = new LineData(dataSet);
        mChart.setData(data);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new DateValueFormatter());
        xAxis.setLabelCount(3);


        Description description = new Description();
        description.setText("");
        mChart.getLegend().setEnabled(false);
        mChart.setTouchEnabled(false);
        mChart.setDescription(description);
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    public class DateValueFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // Simple version. You should use a DateFormatter to specify how you want to textually represent your date.
            String pattern = "HH:mm:ss dd.MM.yy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Date date = new Date(new Float(value).longValue());
            String formattedDate = simpleDateFormat.format(date);
            return formattedDate;
        }
    }
}

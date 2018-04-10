package kz.kuat.kuat;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.macroyau.thingspeakandroid.ThingSpeakChannel;
import com.macroyau.thingspeakandroid.model.ChannelFeed;
import com.macroyau.thingspeakandroid.model.Feed;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChartsActivity extends AppCompatActivity implements ThingSpeakConnection.ChannelFeedUpdateListener, AdapterView.OnItemSelectedListener, View.OnClickListener {

    FrameLayout screenLayout;
    ConstraintLayout mainNavigation;
    Spinner listOfCharts;
    LineChart mChart;
    TextView yAxis;
    Button backButton;

    int fieldNum = 1;
    LastClickedRunnable lastClickedRunnable;
    ThingSpeakConnection thingSpeakConnection;
    ChannelFeed storedChannelFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        screenLayout = findViewById(R.id.screenLayout);
        mainNavigation = findViewById(R.id.mainNavigation);
        listOfCharts = findViewById(R.id.listOfCharts);
        mChart = findViewById(R.id.lineChart);
        yAxis = findViewById(R.id.yAxis);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //hide status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        //ThingSpeak Connection
        thingSpeakConnection = ThingSpeakConnection.getInstance();
        thingSpeakConnection.setChannelFeedUpdateListener(this);
        thingSpeakConnection.loadChannelFeed();

        //Spinner content
        ArrayList<String> charts = new ArrayList<>();
        charts.add("Current");
        charts.add("Voltage");
        charts.add("Apparent power");
        charts.add("Real power");
        charts.add("Power factor");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_gallery_item, charts);
        listOfCharts.setAdapter(adapter);

        //Spinner item selection
        listOfCharts.setOnItemSelectedListener(this);


        //hide mainNavigation
        screenLayout.setOnClickListener(this);
        mChart.setOnClickListener(this);
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
        mChart.setDescription(description);
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    @Override
    public void onChannelFeedUpdated(long channelId, String channelName, ChannelFeed channelFeed) {
        storedChannelFeed = channelFeed;
        ArrayList<Entry> entries = new ArrayList<>();
        for (Feed feed : channelFeed.getFeeds()) {
            Float xValue = Long.valueOf(feed.getCreatedAt().getTime()).floatValue();
            Float yValue = Float.parseFloat(feed.getField(fieldNum));
            entries.add(new Entry(xValue, yValue));
        }
        updateDataSet(entries);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        fieldNum = position + 1;

        //yAxis settings
        String yAxisName = (String) parent.getItemAtPosition(position);
        yAxis.setText(yAxisName);
//        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
//        yAxis.setAnimation(rotateAnimation);

        ChannelFeed channelFeed = storedChannelFeed;
        ArrayList<Entry> entries = new ArrayList<>();
        if (channelFeed != null) {
            for (Feed feed : channelFeed.getFeeds()) {
                Float xValue = Long.valueOf(feed.getCreatedAt().getTime()).floatValue();
                Float yValue = Float.parseFloat(feed.getField(fieldNum));
                entries.add(new Entry(xValue, yValue));
            }
            updateDataSet(entries);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (mainNavigation.getVisibility() == View.VISIBLE)
            lastClickedRunnable = new LastClickedRunnable();
        mainNavigation.setVisibility(View.VISIBLE);
        mainNavigation.postDelayed(lastClickedRunnable, 3000);
    }

    class LastClickedRunnable implements Runnable {
        @Override
        public void run() {
            if (lastClickedRunnable.equals(this))
                mainNavigation.setVisibility(View.INVISIBLE);
        }
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

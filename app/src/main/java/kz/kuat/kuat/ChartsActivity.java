package kz.kuat.kuat;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.macroyau.thingspeakandroid.ThingSpeakChannel;
import com.macroyau.thingspeakandroid.model.ChannelFeed;
import com.macroyau.thingspeakandroid.model.Feed;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ChartsActivity extends AppCompatActivity implements ThingSpeakChannel.ChannelFeedUpdateListener, AdapterView.OnItemSelectedListener {

    //Time intervals in milliseconds
    private static final long SECOND = 1000;
    private static final long MINUTE = SECOND * 60;
    private static final long HOUR = MINUTE * 60;
    private static final long DAY = HOUR * 24;
    private static final long MONTH = DAY * 30;
    private static final long YEAR = DAY * 365;

    FrameLayout screenLayout;
    FrameLayout additionalToolsPanelLayout;
    ConstraintLayout mainNavigation;
    //    Spinner listOfCharts;
    LineChart mChart;
    TextView yAxis;
    Button backButton;
    TextView dateInfo;
    TextView graphName;

    int fieldNum = 1;
    ThingSpeakConnection thingSpeakConnection;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //make activity full screen
        hideSystemUI();

        screenLayout = findViewById(R.id.screenLayout);
        additionalToolsPanelLayout = findViewById(R.id.additionalToolsPanelLayout);
        mChart = findViewById(R.id.lineChart);
        yAxis = findViewById(R.id.yAxis);
        dateInfo = findViewById(R.id.dateInfo);
        graphName = findViewById(R.id.graphName);

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
//        listOfCharts.setAdapter(adapter);

        //Spinner item selection
//        listOfCharts.setOnItemSelectedListener(this);

        additionalToolsPanelLayout.setOnTouchListener(new OnSwipeTouchListener(ChartsActivity.this) {
            public void onSwipeTop() {
//                Toast.makeText(ChartsActivity.this, "top", Toast.LENGTH_SHORT).show();
                hideSystemUI();
            }

            public void onSwipeRight() {
//                Toast.makeText(ChartsActivity.this, "right", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeLeft() {
//                Toast.makeText(ChartsActivity.this, "left", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeBottom() {
//                Toast.makeText(ChartsActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });

        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            Animation in = new AlphaAnimation(0, (float) 1);
                            in.setDuration(300);
                            additionalToolsPanelLayout.startAnimation(in);
                            additionalToolsPanelLayout.setVisibility(View.VISIBLE);
                        } else {
                            Animation out = new AlphaAnimation((float) 1, 0);
                            out.setDuration(300);
                            additionalToolsPanelLayout.startAnimation(out);
                            additionalToolsPanelLayout.setVisibility(View.GONE);
                        }
                    }
                });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                thingSpeakConnection.loadChannelFeed();
//                Log.e("TIMER", "Data updated");
            }
        }, 0, 20000);

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            LimitLine limitLine;
//
//            @Override
//            public void run() {
//                Timer timer2 = new Timer();
//                timer2.scheduleAtFixedRate(new TimerTask() {
//                    @Override
//                    public void run() {
////                if(fieldNum==1)fieldNum=2;
////                else if(fieldNum==2)fieldNum=1;
//                        LineData lineData = mChart.getData();
//                        Entry entry = new Entry();
//                        entry.setY(0.0f);
//                        entry.setX(new Date().getTime());
//                        Log.e("ENTRY", String.valueOf(entry.getY()));
//
//                        lineData.addEntry(entry, 0);
//                        Log.e("TIMER2", "New Entry added");
//                        limitLine = new LimitLine(entry.getX(), "Test");
//                        limitLine.setLineColor(R.color.colorPrimary);
//                        limitLine.setLineWidth(2f);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mChart.getXAxis().addLimitLine(limitLine);
//                                mChart.notifyDataSetChanged();
//                                mChart.invalidate();
//                            }
//                        });
//
//                    }
//                }, 0, 10000);
//            }
//        }, 5000);


    }

    public void updateGraph(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "dataSet");
        LineData data = new LineData(dataSet);
        data.setDrawValues(false);
        data.setHighlightEnabled(false);
        mChart.setData(data);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new DateValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        Description description = new Description();
        description.setText("");
        mChart.getLegend().setEnabled(false);
        mChart.setDescription(description);
        mChart.notifyDataSetChanged();
        mChart.invalidate();
        mChart.setVisibility(View.VISIBLE);

//        LimitLine limitLine = new LimitLine(1.523882E12f, "Test");
//        limitLine.setLineColor(R.color.colorPrimary);
//        limitLine.setLineWidth(2f);
//        Date date = new Date((long) 1.523882E12f);
//        Log.e("Date", date.toString());
//        mChart.getXAxis().addLimitLine(limitLine);
//        mChart.setVisibleXRange(1.52388187E12f, 1.52388213f);
//        mChart.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        fieldNum = position + 1;

        //yAxis settings
        String yAxisName = (String) parent.getItemAtPosition(position);
        yAxis.setText(yAxisName);
        thingSpeakConnection.loadChannelFeed();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    public Button getBackButton() {
        return backButton;
    }

    @Override
    public void onChannelFeedUpdated(long channelId, String channelName, ChannelFeed channelFeed) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (Feed feed : channelFeed.getFeeds()) {
            if (feed.getField(fieldNum) != null) {
                Float xValue = Long.valueOf(feed.getCreatedAt().getTime()).floatValue();
                Float yValue = Float.parseFloat(feed.getField(fieldNum));
                entries.add(new Entry(xValue, yValue));
            }
        }
        updateGraph(entries);
    }

    public class DateValueFormatter implements IAxisValueFormatter {

        private ScalingType prevScalingType = ScalingType.NONE;
        private boolean scalingChanged = true;

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            String xAxisPattern;
            String dateInfoPattern;

            ViewPortHandler handler = mChart.getViewPortHandler();

            MPPointD topLeft = mChart.getValuesByTouchPoint(handler.contentLeft(), handler.contentTop(), YAxis.AxisDependency.LEFT);
            MPPointD bottomRight = mChart.getValuesByTouchPoint(handler.contentRight(), handler.contentBottom(), YAxis.AxisDependency.LEFT);

            CustomDate leftDate = new CustomDate(topLeft.x);
            CustomDate rightDate = new CustomDate(bottomRight.x);

            double deltaTime = rightDate.getDate().getTime() - leftDate.getDate().getTime();
            double yearDensity = deltaTime / YEAR;
            double monthDensity = deltaTime / MONTH;
            double dayDensity = deltaTime / DAY;
            double hourDensity = deltaTime / HOUR;
            double minuteDensity = deltaTime / MINUTE;
            double secondDensity = deltaTime / SECOND;

            int maxLabelCount = 0;
            double labelCount = 0;
            double granularity = 0;

            if (yearDensity >= 2) {
                xAxisPattern = "yyyy";
                dateInfoPattern = "";
                scalingChanged = prevScalingType != ScalingType.YEAR;
                prevScalingType = ScalingType.YEAR;
                labelCount = yearDensity;
            } else if (monthDensity >= 2) {
                xAxisPattern = "MMM";
                dateInfoPattern = "yyyy";
                scalingChanged = prevScalingType != ScalingType.MONTH;
                prevScalingType = ScalingType.MONTH;
                maxLabelCount = 12;
                labelCount = Math.min(maxLabelCount, monthDensity);
            } else if (dayDensity >= 2) {
                xAxisPattern = "dd MMM";
                dateInfoPattern = "yyyy";
                scalingChanged = prevScalingType != ScalingType.DAY;
                prevScalingType = ScalingType.DAY;
                maxLabelCount = 15;
                labelCount = Math.min(maxLabelCount, dayDensity);
            } else if (hourDensity >= 2) {
                xAxisPattern = "HH";
                dateInfoPattern = "dd MMMM yyyy";
                scalingChanged = prevScalingType != ScalingType.HOUR;
                prevScalingType = ScalingType.HOUR;
                maxLabelCount = 12;
                labelCount = Math.min(maxLabelCount, hourDensity);
            } else if (minuteDensity >= 2) {
                xAxisPattern = "HH:mm";
                dateInfoPattern = "dd MMMM yyyy";
                scalingChanged = prevScalingType != ScalingType.MINUTE;
                prevScalingType = ScalingType.MINUTE;
                maxLabelCount = 15;
                labelCount = Math.min(maxLabelCount, minuteDensity);
            } else {
                xAxisPattern = "mm:ss";
                scalingChanged = prevScalingType != ScalingType.SECOND;
                prevScalingType = ScalingType.SECOND;
                maxLabelCount = 15;
                labelCount = Math.min(maxLabelCount, secondDensity);
                int hours = leftDate.getHours();
                String hourString;
                if (hours == 1) hourString = "час";
                else if (hours >= 2 && hours <= 4) hourString = "часа";
                else hourString = "часов";
                dateInfoPattern = "HH " + hourString + " dd MMMM yyyy";
            }


            granularity = (float) deltaTime / labelCount;

//            Log.e("SCALING CHANGED", String.valueOf(scalingChanged));
//            Log.e("LABEL COUNT MY", String.valueOf(labelCount));
//            Log.e("LABEL COUNT FROM CHART", String.valueOf(mChart.getXAxis().getLabelCount()));
//            Log.e("GRANULARITY", String.valueOf(deltaTime / maxLabelCount));
//            Log.e("GRANULARITY FROM CHART", String.valueOf(mChart.getXAxis().getGranularity()));

            mChart.getXAxis().setGranularity((float) (granularity));
            SimpleDateFormat dateInfoDateFormat = new SimpleDateFormat(dateInfoPattern, new Locale("ru"));
            dateInfo.setText(dateInfoDateFormat.format(leftDate.getDate()));
            SimpleDateFormat xAxisDateFormat = new SimpleDateFormat(xAxisPattern, new Locale("ru"));
            Date date = new Date(Float.valueOf(value).longValue());
            return xAxisDateFormat.format(date);
        }

        class CustomDate {
            private Date date;
            private Calendar calendar;

            CustomDate() {
                this.date = new Date();
                calendar = Calendar.getInstance();
            }

            CustomDate(long date) {
                this.date = new Date(date);
                calendar = Calendar.getInstance();
            }

            CustomDate(double date) {
                this.date = new Date((long) date);
                calendar = Calendar.getInstance();
            }

            public int getYear() {
                calendar.setTime(date);
                return calendar.get(Calendar.YEAR);
            }

            public int getMonthNumber() {
                calendar.setTime(date);
                return calendar.get(Calendar.MONTH);
            }

            public int getDay() {
                calendar.setTime(date);
                return calendar.get(Calendar.DAY_OF_MONTH);
            }

            public int getHours() {
                calendar.setTime(date);
                int hours = calendar.get(Calendar.HOUR);
                if (calendar.get(Calendar.AM_PM) == Calendar.PM) hours += 12;
                return hours;
            }

            public int getMinutes() {
                calendar.setTime(date);
                return calendar.get(Calendar.MINUTE);
            }

            public int getSeconds() {
                calendar.setTime(date);
                return calendar.get(Calendar.SECOND);
            }

            public String getMonthName() {
                String month = "wrong";
                DateFormatSymbols dfs = new DateFormatSymbols();
                String[] months = dfs.getMonths();
                int num = getMonthNumber();
                if (num >= 0 && num <= 11) {
                    month = months[num];
                }
                return month;
            }

            public int getWeekDayNumber() {
                calendar.setTime(date);
                return calendar.get(Calendar.DAY_OF_WEEK);
            }

            public String getWeekDayName() {
                String weekDay = "wrong";
                DateFormatSymbols dfs = new DateFormatSymbols();
                String[] months = dfs.getWeekdays();
                int num = getWeekDayNumber();
                if (num >= 0 && num <= 6) {
                    weekDay = months[num];
                }
                return weekDay;
            }

            public Date getDate() {
                return date;
            }
        }
    }

    enum ScalingType {
        YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, NONE
    }

}

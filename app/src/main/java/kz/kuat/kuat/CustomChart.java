package kz.kuat.kuat;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.macroyau.thingspeakandroid.model.Feed;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomChart implements ThingSpeakConnection.DataUpdateListener {

    //Time intervals in milliseconds
    private static final long SECOND = 1000;
    private static final long MINUTE = SECOND * 60;
    private static final long HOUR = MINUTE * 60;
    private static final long DAY = HOUR * 24;
    private static final long MONTH = DAY * 30;
    private static final long YEAR = DAY * 365;

    private static final long DIFFERENCE_YEARS = 50 * YEAR;

    private LineChart mChart;
    private TextView dateInfo;
    private TextView title;
    private ThingSpeakConnection thingSpeakConnection;
    private int fieldNum;

    public CustomChart(LineChart mChart, int fieldNum, ThingSpeakConnection thingSpeakConnection) {
        this.mChart = mChart;
        this.thingSpeakConnection = thingSpeakConnection;
        thingSpeakConnection.setDataUpdateListener(this);
        this.fieldNum = fieldNum;
    }

    public void drawChart(List<Entry> entries) {
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
    }

    private List<Entry> getDataFromThingSpeak() {
        ArrayList<Entry> entries = new ArrayList<>();
        for (Feed feed : thingSpeakConnection.getTotalFeeds()) {
            if (feed.getField(fieldNum) != null) {
                long xValue = feed.getCreatedAt().getTime();
                xValue = (xValue - DIFFERENCE_YEARS) / SECOND;
                Float yValue = Float.parseFloat(feed.getField(fieldNum));
                entries.add(new Entry(xValue, yValue));
            }

        }
        return entries;
    }

    public LineChart getmChart() {
        return mChart;
    }

    public TextView getDateInfo() {
        return dateInfo;
    }

    public void setDateInfo(TextView dateInfo) {
        this.dateInfo = dateInfo;
    }

    public void setFieldNum(int fieldNum) {
        this.fieldNum = fieldNum;
    }

    public int getFieldNum() {
        return fieldNum;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    @Override
    public void onDataUpdated() {
        List<Entry> entries = getDataFromThingSpeak();
        String chartName = thingSpeakConnection.getChannel().getFieldName(fieldNum);
        title.setText(chartName);
        drawChart(entries);
    }

    public class DateValueFormatter implements IAxisValueFormatter {

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
            double yearDensity = deltaTime / YEAR * SECOND;
            double monthDensity = deltaTime / MONTH * SECOND;
            double dayDensity = deltaTime / DAY * SECOND;
            double hourDensity = deltaTime / HOUR * SECOND;
            double minuteDensity = deltaTime / MINUTE * SECOND;
            double secondDensity = deltaTime / SECOND * SECOND;

            int maxLabelCount = 0;
            double labelCount = 0;
            double granularity = 0;

            if (yearDensity >= 2) {
                xAxisPattern = "yyyy";
                dateInfoPattern = "";
                labelCount = yearDensity;
            } else if (monthDensity >= 2) {
                xAxisPattern = "MMM";
                dateInfoPattern = "yyyy";
                maxLabelCount = 12;
                labelCount = Math.min(maxLabelCount, monthDensity);
            } else if (dayDensity >= 2) {
                xAxisPattern = "dd MMM";
                dateInfoPattern = "yyyy год";
                maxLabelCount = 15;
                labelCount = Math.min(maxLabelCount, dayDensity);
            } else if (hourDensity >= 2) {
                xAxisPattern = "HH";
                dateInfoPattern = "dd MMMM yyyy года";
                maxLabelCount = 12;
                labelCount = Math.min(maxLabelCount, hourDensity);
            } else if (minuteDensity >= 2) {
                xAxisPattern = "HH:mm";
                dateInfoPattern = "dd MMMM yyyy года";
                maxLabelCount = 15;
                labelCount = Math.min(maxLabelCount, minuteDensity);
            } else {
                xAxisPattern = "mm:ss";
                maxLabelCount = 15;
                labelCount = Math.min(maxLabelCount, secondDensity);
                int hours = leftDate.getHours();
                String hourString;
                if (hours == 1) hourString = "час";
                else if (hours >= 2 && hours <= 4) hourString = "часа";
                else if (hours >= 22 && hours <= 24) hourString = "часа";
                else hourString = "часов";
                dateInfoPattern = "HH " + hourString + " dd MMMM yyyy года";
            }

            granularity = (float) deltaTime / labelCount;

            mChart.getXAxis().setGranularity((float) (granularity));
            SimpleDateFormat dateInfoDateFormat = new SimpleDateFormat(dateInfoPattern, new Locale("ru"));
            Date dateInfoDate = new Date(leftDate.getDate().getTime() * SECOND + DIFFERENCE_YEARS);
            if (dateInfo != null) dateInfo.setText(dateInfoDateFormat.format(dateInfoDate));
            SimpleDateFormat xAxisDateFormat = new SimpleDateFormat(xAxisPattern, new Locale("ru"));
            Date axisDate = new Date(Float.valueOf(value).longValue() * SECOND + DIFFERENCE_YEARS);
            return xAxisDateFormat.format(axisDate);
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
}
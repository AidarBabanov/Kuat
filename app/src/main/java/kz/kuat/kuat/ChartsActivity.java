package kz.kuat.kuat;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

public class ChartsActivity extends AppCompatActivity implements ThingSpeakConnection.DataUpdateListener {

    FrameLayout screenLayout;
    FrameLayout additionalToolsPanelLayout;
    LineChart mChart;
    TextView yAxis;
    Button backButton;
    TextView dateInfo;
    TextView graphName;

    CustomChart customChart;

    ThingSpeakConnection thingSpeakConnection;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

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
        thingSpeakConnection = TransferObjects.getInstance().getThingSpeakConnection();
        customChart = new CustomChart(mChart, TransferObjects.getInstance().getFieldNum(), thingSpeakConnection);
        customChart.setTitle(graphName);
        customChart.setDateInfo(dateInfo);

        thingSpeakConnection.setDataUpdateListener(this);
        customChart.onDataUpdated();

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
    public void onDataUpdated() {
        customChart.onDataUpdated();
    }
}

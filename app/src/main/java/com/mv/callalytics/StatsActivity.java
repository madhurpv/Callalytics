package com.mv.callalytics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import pl.droidsonroids.gif.GifImageView;

public class StatsActivity extends AppCompatActivity {


    GifImageView loadingGIFImage;
    TextView statsTextView, totalCallsTextView;
    LineChart lineChart, lineChartAvgCall;
    BarChart barChartMostCallDuration, barChartCallTypes, barChartMostCalls;

    AllCallLogs allCallLogs = new AllCallLogs();
    AllContacts contacts = new AllContacts();

    SharedPreferences mPrefs;

    String statsString = "";

    ArrayList<String> listNamesMaxCallDurations = new ArrayList<>();
    ArrayList<Float> listNamesTotalCallDurations = new ArrayList<>();

    ArrayList<String> listNamesMaxCalls = new ArrayList<>();
    ArrayList<Integer> listNamesTotalCalls = new ArrayList<>();


    ArrayList<String> listNamesMaxCallTypes = new ArrayList<>();
    ArrayList<Float> listNamesTotalCallTypes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);


        lineChart = findViewById(R.id.lineChart);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);

        lineChartAvgCall = findViewById(R.id.lineChartAvgCall);
        lineChartAvgCall.setTouchEnabled(true);
        lineChartAvgCall.setPinchZoom(true);

        barChartMostCallDuration = findViewById(R.id.barChartMostCallDuration);
        barChartMostCallDuration.setTouchEnabled(true);
        barChartMostCallDuration.setPinchZoom(true);

        barChartMostCalls = findViewById(R.id.barChartMostCalls);
        barChartMostCalls.setTouchEnabled(true);
        barChartMostCalls.setPinchZoom(true);

        barChartCallTypes = findViewById(R.id.barChartCallTypes);
        barChartCallTypes.setTouchEnabled(true);
        barChartCallTypes.setPinchZoom(true);

        /*barChart = findViewById(R.id.barChart);
        barChart.setTouchEnabled(true);
        barChart.setPinchZoom(true);*/
/*
        ArrayList<Entry> values = new ArrayList<>();
        values.add(new Entry(1, 50));
        values.add(new Entry(2, 100));
        values.add(new Entry(3, 200));
        values.add(new Entry(4, 150));


        LineDataSet set1;
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "Sample Data");
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setDrawIcons(false);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.DKGRAY);
            set1.setCircleColor(Color.DKGRAY);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            set1.setFillColor(Color.DKGRAY);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            mChart.setData(data);
            mChart.animateX(2000);
        }*/


        mPrefs = getSharedPreferences("My_Preferences", MODE_PRIVATE);

        Gson gson = new Gson();
        if (!mPrefs.getString("allCallLogs", "").equals("")) {                                    // Check if not empty
            allCallLogs = gson.fromJson(mPrefs.getString("allCallLogs", ""), AllCallLogs.class);
        }
        if (!mPrefs.getString("contacts", "").equals("")) {                                    // Check if not empty
            contacts = gson.fromJson(mPrefs.getString("contacts", ""), AllContacts.class);
        }


        loadingGIFImage = findViewById(R.id.loadingGIFImage);
        loadingGIFImage.setVisibility(View.VISIBLE);

        statsTextView = findViewById(R.id.statsTextView);
        statsTextView.setVisibility(View.GONE);


        totalCallsTextView = findViewById(R.id.totalCallsTextView);
        totalCallsTextView.setText(String.valueOf(allCallLogs.size()) + " calls");


        STATS_totalCalls();
        STATS_totalContacts();
        STATS_totalCallDuration();
        STATS_averageCallDuration();
        STATS_mostCalls();
        STATS_mostCallsOfType(CallLog.Calls.INCOMING_TYPE);
        STATS_mostCallsOfType(CallLog.Calls.OUTGOING_TYPE);
        STATS_mostCallsOfType(CallLog.Calls.MISSED_TYPE);
        STATS_mostCallsOfType(CallLog.Calls.BLOCKED_TYPE);
        STATS_mostCallsOfType(CallLog.Calls.REJECTED_TYPE);
        STATS_mostCallsOfType(CallLog.Calls.VOICEMAIL_TYPE);
        STATS_mostCallsOfType(CallLog.Calls.ANSWERED_EXTERNALLY_TYPE);
        /*STATS_mostCallsOfDuration();*/
        STATS_mostCallsOfDuration(3);
        /*STATS_maxCallDurationInTotal();
        STATS_monthWithMostCalls();*/
        STATS_mostCalls(3);

        statsTextView.setText(statsString);
        //statsTextView.setVisibility(View.VISIBLE);
        loadingGIFImage.setVisibility(View.GONE);


        draw_permonthcalls_chart();

        draw_permonthavgcallduration_chart();

        draw_mostcallduration_chart();

        draw_calltypes_chart();

        draw_mostcalls_chart();
    }


    public void draw_permonthcalls_chart() {
        // Map to store the number of calls per month with the year and month as the key
        Map<String, Integer> callsPerMonth = new TreeMap<>();

        // Process the call logs to fill the map
        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            Calendar callDate = Calendar.getInstance();
            callDate.setTimeInMillis(entry.dateInMilliSec);
            // Format the year and month key as "YYYY-MM"
            String yearMonthKey = String.format("%d-%02d", callDate.get(Calendar.YEAR), callDate.get(Calendar.MONTH) + 1);

            callsPerMonth.put(yearMonthKey, callsPerMonth.getOrDefault(yearMonthKey, 0) + 1);
        }

        // Convert the map data to Entry list for the chart
        List<Entry> entries = new ArrayList<>();
        List<String> sortedKeys = new ArrayList<>(callsPerMonth.keySet());

        for (int i = 0; i < sortedKeys.size(); i++) {
            String key = sortedKeys.get(i);
            entries.add(new Entry(i, callsPerMonth.get(key)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Call Frequency");
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setDrawFilled(true);//Color.parseColor("#00AAFF"));
        dataSet.setFillDrawable(getResources().getDrawable(R.drawable.chart_gradient));
        dataSet.setValueTextColor(Color.parseColor("#FFFFFF"));
        dataSet.setValueTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);

        // Customize the X-axis to show labels at the bottom
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(sortedKeys.size());

        // Set a new ValueFormatter for the X-axis of the lineChart
        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat monthDateFormat = new SimpleDateFormat("MMM \nyy", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                // Get the yearMonthKey corresponding to the value from the sortedKeys list
                String yearMonthKey = sortedKeys.get((int) value);
                try {
                    // Parse the yearMonthKey into a Date object
                    Date date = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH).parse(yearMonthKey);
                    // Format the Date object into a more readable form, e.g., "Nov 2023"
                    return monthDateFormat.format(date);
                } catch (ParseException e) {
                    // In case of parsing error, return the yearMonthKey directly
                    e.printStackTrace();
                    return yearMonthKey;
                }
            }
        });

        lineChart.setXAxisRenderer(new CustomXAxisRenderer(lineChart.getViewPortHandler(), lineChart.getXAxis(), lineChart.getTransformer(YAxis.AxisDependency.LEFT)));


        lineChart.getLegend().setEnabled(false);
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#FFFFFF")); // left y-axis
        lineChart.getAxisRight().setTextColor(Color.parseColor("#FFFFFF")); // left y-axis
        lineChart.getXAxis().setTextColor(Color.parseColor("#FFFFFF"));
        lineChart.getLegend().setTextColor(Color.parseColor("#00000000"));
        lineChart.getDescription().setTextColor(Color.parseColor("#00000000"));
        lineChart.getAxisLeft().setTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        lineChart.getAxisRight().setTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        lineChart.getXAxis().setTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        lineChart.setExtraBottomOffset(20);
        lineChart.invalidate(); // Refresh the chart
        lineChart.animateY(1000);
    }


    public void draw_permonthavgcallduration_chart() {
        // Map to store the total duration and number of calls per month
        Map<String, Pair<Integer, Integer>> durationAndCallsPerMonth = new TreeMap<>();

        // Process the call logs to fill the map
        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            Calendar callDate = Calendar.getInstance();
            callDate.setTimeInMillis(entry.dateInMilliSec);
            // Format the year and month key as "YYYY-MM"
            String yearMonthKey = String.format("%d-%02d", callDate.get(Calendar.YEAR), callDate.get(Calendar.MONTH) + 1);

            // Get the current values for the month
            Pair<Integer, Integer> currentValues = durationAndCallsPerMonth.getOrDefault(yearMonthKey, new Pair<>(0, 0));
            // Update the total duration and number of calls
            durationAndCallsPerMonth.put(yearMonthKey, new Pair<>(currentValues.first + entry.duration, currentValues.second + 1));
        }

        // Convert the map data to Entry list for the chart
        List<Entry> entries = new ArrayList<>();
        List<String> sortedKeys = new ArrayList<>(durationAndCallsPerMonth.keySet());

        for (int i = 0; i < sortedKeys.size(); i++) {
            String key = sortedKeys.get(i);
            Pair<Integer, Integer> values = durationAndCallsPerMonth.get(key);
            // Calculate the average duration
            float averageDuration = values.first / (float) values.second;
            entries.add(new Entry(i, averageDuration));
        }


        LineDataSet dataSet = new LineDataSet(entries, "Call Frequency");
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setDrawFilled(true);//Color.parseColor("#00AAFF"));
        dataSet.setFillDrawable(getResources().getDrawable(R.drawable.chart_gradient));
        dataSet.setValueTextColor(Color.parseColor("#FFFFFF"));
        dataSet.setValueTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        LineData lineData = new LineData(dataSet);
        lineChartAvgCall.setData(lineData);
        lineChartAvgCall.getAxisLeft().setDrawGridLines(false);
        lineChartAvgCall.getAxisRight().setDrawGridLines(false);

        // Customize the X-axis to show labels at the bottom
        XAxis xAxis = lineChartAvgCall.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(sortedKeys.size());

        lineChartAvgCall.getXAxis().setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat monthDateFormat = new SimpleDateFormat("MMM \nyy", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                // Get the yearMonthKey corresponding to the value from the sortedKeys list
                String yearMonthKey = sortedKeys.get((int) value);
                try {
                    Date date = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH).parse(yearMonthKey);
                    return monthDateFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return yearMonthKey;
                }
            }
        });

        lineChartAvgCall.setXAxisRenderer(new CustomXAxisRenderer(lineChartAvgCall.getViewPortHandler(), lineChartAvgCall.getXAxis(), lineChartAvgCall.getTransformer(YAxis.AxisDependency.LEFT)));

        lineChartAvgCall.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + " sec";
            }
        });


        lineChartAvgCall.getLegend().setEnabled(false);
        lineChartAvgCall.getAxisLeft().setTextColor(Color.parseColor("#FFFFFF")); // left y-axis
        lineChartAvgCall.getAxisRight().setTextColor(Color.parseColor("#FFFFFF")); // left y-axis
        lineChartAvgCall.getXAxis().setTextColor(Color.parseColor("#FFFFFF"));
        lineChartAvgCall.getLegend().setTextColor(Color.parseColor("#00000000"));
        lineChartAvgCall.getDescription().setTextColor(Color.parseColor("#00000000"));
        lineChartAvgCall.getAxisLeft().setTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        lineChartAvgCall.getAxisRight().setTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        lineChartAvgCall.getXAxis().setTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        lineChartAvgCall.setExtraBottomOffset(20);
        lineChartAvgCall.invalidate(); // Refresh the chart
        lineChartAvgCall.animateY(1000);
    }


    public void draw_mostcallduration_chart() {
        String type = " sec";
        if (Collections.min(listNamesTotalCallDurations) > 3600) {
            type = " hrs";
            for (int i = 0; i < listNamesTotalCallDurations.size(); i++) {
                listNamesTotalCallDurations.set(i, (float) (listNamesTotalCallDurations.get(i) / 3600.0));
            }
        } else if (Collections.min(listNamesTotalCallDurations) > 60) {
            type = " min";
            for (int i = 0; i < listNamesTotalCallDurations.size(); i++) {
                listNamesTotalCallDurations.set(i, (float) (listNamesTotalCallDurations.get(i) / 60.0));
            }
        }

        ArrayList<BarEntry> entries = new ArrayList<>();

        // Create BarEntry for each item
        for (int i = 0; i < listNamesTotalCallDurations.size(); i++) {
            entries.add(new BarEntry(i, listNamesTotalCallDurations.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Label");
        dataSet.setDrawValues(true); // To show values on top of the bars

        // Customizing the bar to have rounded corners
        dataSet.setBarShadowColor(Color.parseColor("#FFF3F4F6"));
        float barWidth = 0.95f; // Set the width of the bars
        dataSet.setBarBorderWidth(barWidth);
        dataSet.setGradientColor(Color.parseColor("#00000000"), Color.parseColor("#74F4E9"));
        dataSet.setValueTextColor(Color.parseColor("#FFFFFF"));
        dataSet.setBarBorderColor(Color.parseColor("#00000000"));
        dataSet.setValueTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));

        BarData data = new BarData(dataSet);
        data.setBarWidth(barWidth); // Set custom bar width

        // Setting up the X-axis
        XAxis xAxis = barChartMostCallDuration.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(listNamesMaxCallDurations));

        // Setting up the Y-axis
        YAxis leftAxis = barChartMostCallDuration.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        YAxis rightAxis = barChartMostCallDuration.getAxisRight();
        rightAxis.setDrawGridLines(false);

        // Applying the data to the chart
        barChartMostCallDuration.setData(data);
        barChartMostCallDuration.setFitBars(true); // Make the x-axis fit exactly all bars
        barChartMostCallDuration.getDescription().setEnabled(false); // Hide the description
        barChartMostCallDuration.getLegend().setEnabled(false); // Hide the legend

        String finalType = type;
        barChartMostCallDuration.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Math.round(value) + finalType;
            }
        });

        barChartMostCallDuration.getLegend().setEnabled(false);
        barChartMostCallDuration.getAxisLeft().setTextColor(Color.parseColor("#FFFFFF")); // left y-axis
        barChartMostCallDuration.getAxisRight().setTextColor(Color.parseColor("#FFFFFF")); // left y-axis
        barChartMostCallDuration.getXAxis().setTextColor(Color.parseColor("#FFFFFF"));
        barChartMostCallDuration.getLegend().setTextColor(Color.parseColor("#00000000"));
        barChartMostCallDuration.getDescription().setTextColor(Color.parseColor("#00000000"));
        barChartMostCallDuration.getAxisLeft().setTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        barChartMostCallDuration.getAxisRight().setTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        barChartMostCallDuration.getXAxis().setTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        barChartMostCallDuration.setExtraBottomOffset(10);
        // Refresh the chart
        barChartMostCallDuration.invalidate();
    }

    public void draw_mostcalls_chart() {
        ArrayList<BarEntry> entries = new ArrayList<>();

        // Create BarEntry for each item
        for (int i = 0; i < listNamesTotalCalls.size(); i++) {
            entries.add(new BarEntry(i, listNamesTotalCalls.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Label");
        dataSet.setDrawValues(true); // To show values on top of the bars

        // Customizing the bar to have rounded corners
        dataSet.setBarShadowColor(Color.parseColor("#FFF3F4F6"));
        float barWidth = 0.95f; // Set the width of the bars
        //dataSet.setBarBorderWidth(barWidth);
        dataSet.setGradientColor(Color.parseColor("#00000000"), Color.parseColor("#74F4E9"));
        dataSet.setValueTextColor(Color.parseColor("#FFFFFF"));
        dataSet.setBarBorderColor(Color.parseColor("#00000000"));
        dataSet.setValueTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));

        BarData data = new BarData(dataSet);
        data.setBarWidth(barWidth); // Set custom bar width

        // Setting up the X-axis
        XAxis xAxis = barChartMostCalls.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(listNamesMaxCalls));

        // Setting up the Y-axis
        YAxis leftAxis = barChartMostCalls.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        YAxis rightAxis = barChartMostCalls.getAxisRight();
        rightAxis.setDrawGridLines(false);

        // Applying the data to the chart
        barChartMostCalls.setData(data);
        barChartMostCalls.setFitBars(true); // Make the x-axis fit exactly all bars
        barChartMostCalls.getDescription().setEnabled(false); // Hide the description
        barChartMostCalls.getLegend().setEnabled(false); // Hide the legend

        barChartMostCalls.getLegend().setEnabled(false);
        barChartMostCalls.getAxisLeft().setTextColor(Color.parseColor("#FFFFFF")); // left y-axis
        barChartMostCalls.getAxisRight().setTextColor(Color.parseColor("#FFFFFF")); // left y-axis
        barChartMostCalls.getXAxis().setTextColor(Color.parseColor("#FFFFFF"));
        barChartMostCalls.getLegend().setTextColor(Color.parseColor("#00000000"));
        barChartMostCalls.getDescription().setTextColor(Color.parseColor("#00000000"));
        barChartMostCalls.getAxisLeft().setTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        barChartMostCalls.getAxisRight().setTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        barChartMostCalls.getXAxis().setTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        barChartMostCalls.setExtraBottomOffset(10);
        // Refresh the chart
        barChartMostCalls.invalidate();
    }


    public void draw_calltypes_chart() {
        ArrayList<String> callTypesNames = new ArrayList<>();
        ArrayList<Integer> callTypesValues = new ArrayList<>();

        callTypesNames.add(CallLogEntry.callLogTypeToString(CallLog.Calls.INCOMING_TYPE));
        callTypesNames.add(CallLogEntry.callLogTypeToString(CallLog.Calls.OUTGOING_TYPE));
        callTypesNames.add(CallLogEntry.callLogTypeToString(CallLog.Calls.MISSED_TYPE));
        //callTypesNames.add(CallLogEntry.callLogTypeToString(CallLog.Calls.VOICEMAIL_TYPE));
        callTypesNames.add(CallLogEntry.callLogTypeToString(CallLog.Calls.REJECTED_TYPE));
        //callTypesNames.add(CallLogEntry.callLogTypeToString(CallLog.Calls.BLOCKED_TYPE));
        //callTypesNames.add(CallLogEntry.callLogTypeToString(CallLog.Calls.ANSWERED_EXTERNALLY_TYPE));

        callTypesValues.add(0);
        callTypesValues.add(0);
        callTypesValues.add(0);
        callTypesValues.add(0);
        callTypesValues.add(0);
        callTypesValues.add(0);
        //callTypesValues.add(0);

        for (int i = 0; i < allCallLogs.size(); i++) {
            if (i == CallLog.Calls.VOICEMAIL_TYPE || i == CallLog.Calls.BLOCKED_TYPE) {
                continue;
            }
            callTypesValues.set(allCallLogs.allCallLogs.get(i).type - 1, callTypesValues.get(allCallLogs.allCallLogs.get(i).type - 1) + 1);
        }
        callTypesValues.remove(5);
        callTypesValues.remove(3);

        ArrayList<BarEntry> entries = new ArrayList<>();

        // Create BarEntry for each item
        for (int i = 0; i < callTypesValues.size(); i++) {
            entries.add(new BarEntry(i, callTypesValues.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Label");
        dataSet.setDrawValues(true); // To show values on top of the bars

        // Customizing the bar to have rounded corners
        dataSet.setBarShadowColor(Color.parseColor("#FFF3F4F6"));
        float barWidth = 0.95f; // Set the width of the bars
        dataSet.setBarBorderWidth(barWidth);
        dataSet.setGradientColor(Color.parseColor("#00000000"), Color.parseColor("#74F4E9"));
        dataSet.setBarBorderColor(Color.parseColor("#00000000"));
        dataSet.setValueTextColor(Color.parseColor("#FFFFFF"));
        dataSet.setValueTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));

        BarData data = new BarData(dataSet);
        data.setBarWidth(barWidth); // Set custom bar width

        // Setting up the X-axis
        XAxis xAxis = barChartCallTypes.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(callTypesNames));
        xAxis.setTextSize(xAxis.getTextSize() / 2.9f);

        // Setting up the Y-axis
        YAxis leftAxis = barChartCallTypes.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        YAxis rightAxis = barChartCallTypes.getAxisRight();
        rightAxis.setDrawGridLines(false);

        // Applying the data to the chart
        barChartCallTypes.setData(data);
        barChartCallTypes.setFitBars(true); // Make the x-axis fit exactly all bars
        barChartCallTypes.getDescription().setEnabled(false); // Hide the description
        barChartCallTypes.getLegend().setEnabled(false); // Hide the legend

        /*barChartCallTypes.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Math.round(value) + finalType;
            }
        });*/

        barChartCallTypes.getLegend().setEnabled(false);
        barChartCallTypes.getAxisLeft().setTextColor(Color.parseColor("#FFFFFF")); // left y-axis
        barChartCallTypes.getAxisRight().setTextColor(Color.parseColor("#FFFFFF")); // left y-axis
        barChartCallTypes.getXAxis().setTextColor(Color.parseColor("#FFFFFF"));
        barChartCallTypes.getLegend().setTextColor(Color.parseColor("#00000000"));
        barChartCallTypes.getDescription().setTextColor(Color.parseColor("#00000000"));
        barChartCallTypes.getAxisLeft().setTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        barChartCallTypes.getAxisRight().setTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        barChartCallTypes.getXAxis().setTypeface(ResourcesCompat.getFont(this, R.font.baloo_paaji));
        barChartCallTypes.setExtraBottomOffset(10);
        // Refresh the chart
        barChartCallTypes.invalidate();
    }


    public void STATS_totalCalls() {
        int totalCalls = allCallLogs.size();
        Log.d("QWER_STATS", "Total Calls = " + totalCalls);
        statsString += "\nTotal Calls = " + totalCalls;
    }

    public void STATS_totalContacts() {
        int totalContacts = contacts.size();
        Log.d("QWER_STATS", "Total Contacts = " + totalContacts);
        statsString += "\nTotal Contacts = " + totalContacts;
    }

    public void STATS_totalCallDuration() {
        int totalDuration = 0;
        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            totalDuration += entry.duration;
        }
        Log.d("QWER_STATS", "Total Call duration in seconds = " + totalDuration);
        statsString += "\nTotal Call duration in seconds = " + totalDuration;
    }

    public void STATS_averageCallDuration() {
        int totalDuration = 0;
        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            totalDuration += entry.duration;
        }
        float averageDuration = totalDuration * 1.0f / allCallLogs.size();
        Log.d("QWER_STATS", "Total Call duration in seconds = " + averageDuration);
        statsString += "\nTotal Call duration in seconds = " + averageDuration;
    }

    public void STATS_mostCalls() {
        HashMap<String, Integer> frequencyMap = new HashMap<>();
        String mostFrequentPhoneNo = null;
        int maxCalls = 0;

        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            String phoneNo = entry.phoneNo;
            int currentFrequency = frequencyMap.getOrDefault(phoneNo, 0) + 1;
            frequencyMap.put(phoneNo, currentFrequency);

            if (currentFrequency > maxCalls) {
                maxCalls = currentFrequency;
                mostFrequentPhoneNo = phoneNo;
            }
        }

        Log.d("QWER_STATS", "Most Calls to = " + contacts.getContact(mostFrequentPhoneNo) + "\t\tFrequency = " + maxCalls);
        statsString += "\nMost Calls to = " + contacts.getContact(mostFrequentPhoneNo) + "\tFrequency = " + maxCalls;
    }

    public void STATS_mostCalls(int n) {
        HashMap<String, Integer> frequencyMap = new HashMap<>();
        PriorityQueue<Map.Entry<String, Integer>> maxHeap = new PriorityQueue<>(
                (a, b) -> b.getValue().compareTo(a.getValue())
        );

        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            String phoneNo = entry.phoneNo;
            frequencyMap.put(phoneNo, frequencyMap.getOrDefault(phoneNo, 0) + 1);
        }

        maxHeap.addAll(frequencyMap.entrySet());

        List<Map.Entry<String, Integer>> topCalls = new ArrayList<>();
        for (int i = 0; i < n && !maxHeap.isEmpty(); i++) {
            topCalls.add(maxHeap.poll());
        }

        StringBuilder topNCalls = new StringBuilder();
        for (int i = 0; i < topCalls.size(); i++) {
            Map.Entry<String, Integer> entry = topCalls.get(i);
            topNCalls.append("\nTop ").append(i + 1).append(" Calls to = ")
                    .append(contacts.getContact(entry.getKey()))
                    .append("\tFrequency = ").append(entry.getValue());
        }

        Log.d("QWER_STATS", topNCalls.toString());
        statsString += topNCalls.toString();

        // Print the top n calls one by one
        for (Map.Entry<String, Integer> entry : topCalls) {
            //Log.d("QWER_STATS", "Top Call to: " + contacts.getContact(entry.getKey()) + " - Frequency: " + entry.getValue());
            String name = contacts.getContact(entry.getKey());
            if (name.length() > 8) {
                name = name.substring(0, 9) + "..";
            }
            listNamesMaxCalls.add(name);
            listNamesTotalCalls.add(entry.getValue());
        }
    }


    public void STATS_mostCallsOfType(int type) {
        HashMap<String, Integer> frequencyMap = new HashMap<>();
        String mostFrequentPhoneNo = null;
        int maxCalls = 0;

        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            if (entry.type == type) {                                                  // Filter calls by type 2
                String phoneNo = entry.phoneNo;
                int currentFrequency = frequencyMap.getOrDefault(phoneNo, 0) + 1;
                frequencyMap.put(phoneNo, currentFrequency);

                if (currentFrequency > maxCalls) {
                    maxCalls = currentFrequency;
                    mostFrequentPhoneNo = phoneNo;
                }
            }
        }

        Log.d("QWER_STATS", "Most Calls of type " + type + " to = " + contacts.getContact(mostFrequentPhoneNo) + "\t\tFrequency = " + maxCalls);
        statsString += "\nMost Calls of type " + type + " to = " + contacts.getContact(mostFrequentPhoneNo) + "\tFrequency = " + maxCalls;
        //listNamesMaxCallTypes.add(CallLogEntry.callLogTypeToString(type));
        //listNamesTotalCallTypes.add((float) maxCalls);
    }


    public void STATS_mostCallsOfDuration() {                     // eg.  Most Calls duration is with = Hrishikesh Potnis		Time in seconds = 113371
        HashMap<String, Integer> durationMap = new HashMap<>();
        String phoneNoWithMaxDuration = null;
        int maxDuration = 0;

        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            String phoneNo = entry.phoneNo;
            int currentDuration = durationMap.getOrDefault(phoneNo, 0) + entry.duration;
            durationMap.put(phoneNo, currentDuration);

            if (currentDuration > maxDuration) {
                maxDuration = currentDuration;
                phoneNoWithMaxDuration = phoneNo;
            }
        }

        Log.d("QWER_STATS", "Most Calls duration is with = " + contacts.getContact(phoneNoWithMaxDuration) + "\t\tTime in seconds = " + maxDuration);
        statsString += "\nMost Calls duration is with = " + contacts.getContact(phoneNoWithMaxDuration) + "\tTime in seconds = " + maxDuration;
    }

    public void STATS_mostCallsOfDuration(int n) {
        HashMap<String, Integer> durationMap = new HashMap<>();

        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            String phoneNo = entry.phoneNo;
            int currentDuration = durationMap.getOrDefault(phoneNo, 0) + entry.duration;
            durationMap.put(phoneNo, currentDuration);
        }

        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list = new ArrayList<>(durationMap.entrySet());

        // Sort the list using custom comparator
        Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        // After sorting, get the top 'n' entries
        for (int i = 0; i < Math.min(list.size(), n); i++) {
            Map.Entry<String, Integer> entry = list.get(i);
            String phoneNo = entry.getKey();
            int duration = entry.getValue();

            // Log the durations and their associated phone numbers
            Log.d("QWER_STATS", "Top " + (i + 1) + " Calls duration is with = " + contacts.getContact(phoneNo) + "\t\tTime in seconds = " + duration);
            statsString += "\nTop " + (i + 1) + " Calls duration is with = " + contacts.getContact(phoneNo) + "\tTime in seconds = " + duration;
            String name = contacts.getContact(phoneNo);
            if (name.length() > 8) {
                name = name.substring(0, 9) + "..";
            }
            listNamesMaxCallDurations.add(name);
            listNamesTotalCallDurations.add((float) duration);
        }
    }


    public void STATS_maxCallDurationInTotal() {
        int maxDuration = 0;
        CallLogEntry maxCallLog = null;
        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            if (entry.duration > maxDuration) {
                maxDuration = entry.duration;
                maxCallLog = entry;
            }
        }
        Log.d("QWER_STATS", "Maximum Call duration in seconds = " + maxDuration + "\t\twith " + contacts.getContact(maxCallLog.phoneNo) + "\t\ton " + new Date(maxCallLog.dateInMilliSec));
        statsString += "\nMaximum Call duration in seconds = " + maxDuration + "\twith " + contacts.getContact(maxCallLog.phoneNo) + "\ton " + new Date(maxCallLog.dateInMilliSec);
    }

    public void STATS_monthWithMostCalls() {
        HashMap<Integer, Integer> callCountByMonth = new HashMap<>();
        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(entry.dateInMilliSec);
            int month = calendar.get(Calendar.MONTH);
            callCountByMonth.put(month, callCountByMonth.getOrDefault(month, 0) + 1);
        }

        int maxCalls = 0;
        int monthWithMostCalls = -1;
        for (Map.Entry<Integer, Integer> entry : callCountByMonth.entrySet()) {
            if (entry.getValue() > maxCalls) {
                maxCalls = entry.getValue();
                monthWithMostCalls = entry.getKey();
            }
        }
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        Log.d("QWER_STATS", "Month with Maximum Calls = " + months[monthWithMostCalls] + "\t\twith calls = " + maxCalls);
        statsString += "\nMonth with Maximum Calls = " + months[monthWithMostCalls] + "\twith calls = " + maxCalls;
    }








    public static class CustomXAxisRenderer extends XAxisRenderer {        // Source - https://stackoverflow.com/questions/32509174/in-mpandroidchart-library-how-to-wrap-x-axis-labels-to-two-lines-when-long
        public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
            super(viewPortHandler, xAxis, trans);
        }

        @Override
        protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
            String[] line = formattedLabel.split("\n");
            Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees);
            for (int i=1; i<line.length; i++) {
                Utils.drawXAxisValue(c, line[i], x, y + mAxisLabelPaint.getTextSize() * i, mAxisLabelPaint, anchor, angleDegrees);
            }
        }
    }
}
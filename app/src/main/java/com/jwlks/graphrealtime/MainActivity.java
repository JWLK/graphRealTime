package com.jwlks.graphrealtime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    int N = 1024; // 512
    double[] mag;

    int counter = 0;
    LineChart lineChart;
    int DATA_RANGE = 1000;
    LineData lineData;
    LineDataSet setValueTransfer;
    ArrayList<Entry> entryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChart = (LineChart) findViewById(R.id.mp_chart);
        chartInit();
        threadStart();

        setValueTransfer.notifyDataSetChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();

    }

    private void chartInit() {
        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setAxisMaximum(1000f);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//        lineChart.getAxisLeft().setAxisMaximum(1024f);
//        lineChart.getAxisLeft().setAxisMinimum(0f);

        entryData = new ArrayList<Entry>();
        setValueTransfer = new LineDataSet(entryData, "data/ms");
        setValueTransfer.setColor(Color.RED);
        setValueTransfer.setDrawValues(false);
        setValueTransfer.setDrawCircles(false);
        setValueTransfer.setAxisDependency(YAxis.AxisDependency.LEFT);

        lineData = new LineData();
        lineData.addDataSet(setValueTransfer);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private void chartClear() {
        lineChart.setData(null);
        lineChart.invalidate();
        lineChart.setNoDataText("NOTHING");
        lineChart.setNoDataTextColor(ContextCompat.getColor(this, R.color.purple_500));
    }


    public void chartUpdate(int data) {
        if(entryData.size() > DATA_RANGE){
            entryData.remove(0);
            for(int i = 0; i < DATA_RANGE; i++){
                entryData.get(i).setX(i);
            }
        }
        entryData.add(new Entry(entryData.size(), data));
        setValueTransfer.notifyDataSetChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                int data = 0;
                data = (int)(Math.random()*1024);
                chartUpdate(data);
                //chartUpdateFFT((float) mag[counter%mag.length] );
                //Log.d("Handler", counter%mag.length+"");
                counter++;
            }
        }
    };

    class GraphThread extends Thread {
        @Override
        public void run() {
            int i = 0;
            while(true){
                handler.sendEmptyMessage(i);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void threadStart() {
        GraphThread thread = new GraphThread();
        thread.setDaemon(true);
        thread.start();
    }

}
package com.example.wodlog;

import android.widget.TextView;

public class Timer extends Thread {
    private TextView timerText;
    private Long date;
    private boolean state = true;
    public Timer(TextView timerText, Long date) {
        this.timerText = timerText;
        this.date = date;
    }
    public void setState(boolean state) {
        this.state = state;
    }
    @Override
    public void run() {
        while (this.state) {
            long time = (System.currentTimeMillis() - date) / 100;
            long msec = time % 10;
            long secs = time / 10;
            long sec = secs % 60;
            long min = secs / 60;
            timerText.setText(String.format("%02d", min) + ":" + String.format("%02d", sec) +":"+ String.format("%02d", msec));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

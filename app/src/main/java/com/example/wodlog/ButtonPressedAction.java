package com.example.wodlog;

import android.widget.EditText;
import android.widget.TextView;

public class ButtonPressedAction extends Thread {
    private boolean run;
    private EditText scoreT;
    private boolean countUp;

    public void setRun(boolean run) {
        this.run = run;
    }

    public ButtonPressedAction(boolean run, EditText scoreT, boolean countUp) {
        this.run = run;
        this.scoreT = scoreT;
        this.countUp = countUp;
    }
    @Override
    public void run() {
        while(run) {
            if(countUp) {
                scoreT.setText(Integer.parseInt(String.valueOf(scoreT.getText())) + 1 + "");
            } else {
                scoreT.setText(Integer.parseInt(String.valueOf(scoreT.getText())) - 1 + "");
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

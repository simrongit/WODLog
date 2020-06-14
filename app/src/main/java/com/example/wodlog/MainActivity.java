package com.example.wodlog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {

    // https://www.dev2qa.com/how-to-define-custom-color-variables-in-android-studio/
    private Button newB;
    private Button upB;
    private Button downB;
    private Button submitB;
    private Button newRoundB;
    private Button stopStartTimerB;
    private Button clipB;
    private Button shareB;
    private Button resetB;
    private Button deleteB;
    private TextView stopStartTimerT;
    private boolean stopStartTimerState = false;
    private TextView logT;
    private EditText scoreT;
    private TextView timerT;

    private Gson gson = (new GsonBuilder()).create();
    private State state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initScreenElements();
        setupScreenElements();
        init();
    }

//    Runnable rUp = () -> {
//            while(upB.isPressed()){
//                upB(null);
//                try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); return;}
//            }
//        };
//    Runnable rDown = () -> {
//        while (downB.isPressed()) {
//            downB(null);
//            try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); return;}
//        }
//    };

    private ButtonPressedAction buttonPressedAction = new ButtonPressedAction(false, scoreT, true);

    private void initScreenElements(){
        newB = findViewById(R.id.newB);
        upB = findViewById(R.id.upB);
        downB = findViewById(R.id.downB);
        submitB = findViewById(R.id.submitB);
        newRoundB = findViewById(R.id.newRoundB);
        clipB = findViewById(R.id.clipB);
        shareB = findViewById(R.id.shareB);
        resetB = findViewById(R.id.resetB);
        logT = findViewById(R.id.logT);
        scoreT = findViewById(R.id.scoreT);
        timerT = findViewById(R.id.timerT);
        stopStartTimerB = findViewById(R.id.stopStartTimerB);
        stopStartTimerT = findViewById(R.id.stopStartTimerT);
    }

    private void setupScreenElements() {
//        upB.setOnLongClickListener(view -> {buttonPressedAction = new ButtonPressedAction(true, scoreT, true); buttonPressedAction.start(); return true;});
        upB.setOnTouchListener((view, motionEvent) -> upDownBTouchSetup(motionEvent, true));
//        downB.setOnLongClickListener(view -> {buttonPressedAction = new ButtonPressedAction(true, scoreT, false); buttonPressedAction.start(); return true;});
        downB.setOnTouchListener((view, motionEvent) -> upDownBTouchSetup(motionEvent, false));
        logT.setMovementMethod(new ScrollingMovementMethod());
    }

    private boolean upDownBTouchSetup(MotionEvent motionEvent, boolean b) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            buttonPressedAction.setRun(false);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            buttonPressedAction = new ButtonPressedAction(true, scoreT, b);
            buttonPressedAction.start();
        }
        return true;
    }

    private Timer timer = null;
    private void init() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String stateString = sharedPref.getString("obj", "");
        if(stateString.isEmpty()) {
            state = new State();
            state.data.add("Round "+state.roundCounter+"_"+timerT.getText());
        } else {
            state = gson.fromJson(stateString, State.class);
        }
        timer = new Timer(timerT, System.currentTimeMillis());
        timer.start();
        updateLogT();
        if(state.data.size()%(state.roundEvents) != 0) {
            scoreT.setText(getScore());
        } else {
            if(state.roundCounter == 2)
                state.roundEvents = (state.data.size()-1);
            scoreT.setText(getScore());
            newRoundB.setVisibility(View.GONE);
        }
    }

    private static String space = "            "; //10
    private void updateLogT() {
        String logStr = state.data.stream().map(str -> str.split("_")[0]+space.substring(str.split("_")[0].length())+str.split("_")[1]).collect(Collectors.joining("\n"));
        logStr += "\n\n\n"+"Total: "+state.data.stream().filter(str -> !str.startsWith("Round")).map(str->str.split("_")[0]).flatMapToInt(str -> IntStream.of(Integer.parseInt(str))).sum();
        List<Integer> eventTotals = new ArrayList<>();
        if(state.roundEvents < 100) {
            for(int i=0; i<state.roundEvents-1; i++) {
                eventTotals.add(0);
            }
            for(int i=0; i<state.data.size(); i++) {
                if(i%state.roundEvents != 0) {
                    eventTotals.set((i%state.roundEvents)-1, eventTotals.get((i%state.roundEvents)-1)+Integer.parseInt(state.data.get(i).split("_")[0]));
                }
            }
        }
        logStr += "\n"+"Events Total: "+eventTotals.stream().map(num -> num.toString()).collect(Collectors.joining(", "));
        logT.setText(logStr);
    }


    public void newB(View view){
        scoreT.setText("0");
    }

    private void persistData() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("obj", gson.toJson(state));
        editor.commit();
    }

    public void upB(View view) {
        scoreT.setText(Integer.parseInt(String.valueOf(scoreT.getText()))+1+"");
    }

    public void downB(View view) {
        scoreT.setText(Integer.parseInt(String.valueOf(scoreT.getText()))-1+"");
    }

    private Timer stopStartTimerThread = null;
    public void stopStartTimerB(View view) {
        stopStartTimerState = !stopStartTimerState;
        if(stopStartTimerState) {
            stopStartTimerThread = new Timer(stopStartTimerT, System.currentTimeMillis());
            stopStartTimerThread.start();
            stopStartTimerB.setText("Stop");
        } else {
            stopStartTimerThread.setState(false);
            stopStartTimerB.setText("Start");
        }
    }

    public void clipB(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Log Text", logT.getText());
        clipboard.setPrimaryClip(clip);
    }

    public void shareB(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, logT.getText());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void resetB(View view) {
        state = new State();
        newRoundB.setVisibility(View.VISIBLE);
        timer.setState(false);
        timer = new Timer(timerT, System.currentTimeMillis());
        timer.start();
        state.data.add("Round "+state.roundCounter+"_00:00:00");
        updateLogT();
    }

    public void deleteB(View view) {
        if(state.data.size() !=1) {
            if(state.data.get(state.data.size()-1).startsWith("Round ")) { //same as state.lookUpIndex == ((state.roundCounter-1)*state.roundEvents-1)
                state.data.remove(state.data.size()-1);
                state.data.remove(state.data.size()-1);
                if(state.data.size() ==1){
                    state.roundCounter = 1;
                    state.roundEvents = 100;
                } else {
                    --state.roundCounter;
                }
            } else {
                state.data.remove(state.data.size()-1);
            }
        }
        updateLogT();
        scoreT.setText(getScore());
        persistData();
    }


    public void submitB(View view) {
        state.data.add(String.valueOf(scoreT.getText())+"_"+timerT.getText());
        updateLogT();
        if(state.data.size()%(state.roundEvents) != 0) {
            scoreT.setText(getScore());
        } else {
            newRoundB(view);
        }
        persistData();
    }

    public void newRoundB(View view) {
        ++state.roundCounter;
        state.data.add("Round "+state.roundCounter+"_"+timerT.getText());
        updateLogT();
        if(state.roundCounter == 2)
            state.roundEvents = (state.data.size()-1);
        scoreT.setText(getScore());
        newRoundB.setVisibility(View.GONE);
    }

    private String getScore() {
        int showIndex = 0;
        if(state.roundEvents==100 || state.roundCounter==1) {
            showIndex = state.data.size()-1;
        } else {
            showIndex = state.data.size() - state.roundEvents;
        }
        return showIndex==0?"0":state.data.get(showIndex).split("_")[0];
    }
}
package com.example.wodlog;

import java.util.ArrayList;
import java.util.List;

public class State {
    protected List<String> data = new ArrayList<>();
    protected int roundCounter = 1;
    protected int roundEvents = 100; // Its +1 due to Round text
}

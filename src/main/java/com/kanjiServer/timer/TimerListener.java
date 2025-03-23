package com.kanjiServer.timer;

public interface TimerListener {
    int onTimeUpdate();
    boolean onTimeUp();
    String onWordCreated(String word);
}

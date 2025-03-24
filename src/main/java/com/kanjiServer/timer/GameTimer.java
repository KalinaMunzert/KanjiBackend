package com.kanjiServer.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameTimer {
    private int timeLeft;
    private boolean isRunning;
    private ScheduledExecutorService scheduler;
    private TimerListener listener;

    public GameTimer(int duration, TimerListener listener) {
        this.timeLeft = duration;
        this.listener = listener;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        isRunning = true;
        if (scheduler.isShutdown() || scheduler.isTerminated()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        scheduler.scheduleWithFixedDelay(() -> {
//            System.out.println("In scheduler");
            if (isRunning) {
                tick();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void tick() {
        if (timeLeft > 0) {
            timeLeft -= 1000;
            listener.onTimeUpdate();
        } else {
            stop();
            listener.onTimeUp();
        }
    }

    public void addTime(int addTime) {
        timeLeft += addTime;
        listener.onTimeUpdate();
    }

    public void stop() {
        isRunning = false;
        scheduler.shutdown();
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public boolean getRunning() {
        return isRunning;
    }
}


